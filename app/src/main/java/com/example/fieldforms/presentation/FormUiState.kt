package com.example.fieldforms.presentation

import com.example.fieldforms.domain.model.FormPage

data class FormUiState(
    val pages: List<FormPage> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val showFullScreenLoading: Boolean
        get() = isLoading && pages.isEmpty()

    val showFullScreenError: Boolean
        get() = !isLoading && pages.isEmpty() && errorMessage != null

    val showEmpty: Boolean
        get() = !isLoading && pages.isEmpty() && errorMessage == null

    val showContent: Boolean
        get() = pages.isNotEmpty()
}
