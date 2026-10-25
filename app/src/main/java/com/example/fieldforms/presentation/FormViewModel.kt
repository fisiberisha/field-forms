package com.example.fieldforms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val formRepository: FormRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private val refreshGeneration = AtomicInteger(0)
    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            formRepository.observeForm().collect { pages ->
                _uiState.update { current ->
                    val clampedIndex = current.selectedPageIndex.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                    current.copy(pages = pages, selectedPageIndex = clampedIndex)
                }
            }
        }
        viewModelScope.launch {
            formRepository.observeChoiceSelections().collect { selections ->
                _uiState.update { it.copy(choiceSelections = selections) }
            }
        }
        refresh()
    }

    fun refresh() {
        val generation = refreshGeneration.incrementAndGet()
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessageRes = null) }
            try {
                when (val result = formRepository.refresh()) {
                    RefreshResult.Success -> {
                        if (generation != refreshGeneration.get()) return@launch
                        _uiState.update { it.copy(isRefreshing = false, errorMessageRes = null) }
                    }
                    is RefreshResult.Failure -> {
                        if (generation != refreshGeneration.get()) return@launch
                        _uiState.update { current ->
                            val messageRes = if (current.pages.isEmpty()) {
                                FormErrorMessages.noCachedDataMessageRes(result.error)
                            } else {
                                FormErrorMessages.cachedDataRefreshMessageRes(result.error)
                            }
                            current.copy(
                                isRefreshing = false,
                                errorMessageRes = messageRes,
                            )
                        }
                    }
                }
            } catch (_: CancellationException) {
                if (generation == refreshGeneration.get()) {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    fun onPageSelected(index: Int) {
        _uiState.update { it.copy(selectedPageIndex = index) }
    }

    fun onChoiceResponseToggle(
        questionId: Int,
        responseId: Int,
        multipleSelection: Boolean,
    ) {
        viewModelScope.launch {
            formRepository.updateChoiceSelection(
                questionId = questionId,
                responseId = responseId,
                multipleSelection = multipleSelection,
            )
        }
    }
}
