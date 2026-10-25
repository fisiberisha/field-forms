package com.example.fieldforms.presentation

import androidx.lifecycle.ViewModel
import com.example.fieldforms.domain.repository.FormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    @Suppress("UnusedPrivateMember")
    private val formRepository: FormRepository,
) : ViewModel() {
    // UiState and form observation are added in Phase 6.
}
