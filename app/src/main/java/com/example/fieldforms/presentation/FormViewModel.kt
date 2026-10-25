package com.example.fieldforms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val formRepository: FormRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            formRepository.observeForm().collect { pages ->
                _uiState.update { current ->
                    current.copy(pages = pages)
                }
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessageRes = null) }

            when (val result = formRepository.refresh()) {
                RefreshResult.Success -> {
                    _uiState.update { it.copy(isRefreshing = false, errorMessageRes = null) }
                }
                is RefreshResult.Failure -> {
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
        }
    }

    fun onChoiceResponseToggle(
        questionId: Int,
        responseId: Int,
        multipleSelection: Boolean,
    ) {
        _uiState.update { current ->
            current.copy(
                choiceSelections = ChoiceSelectionLogic.toggleSelection(
                    current = current.choiceSelections,
                    questionId = questionId,
                    responseId = responseId,
                    multipleSelection = multipleSelection,
                ),
            )
        }
    }
}
