package com.example.fieldforms.presentation

import androidx.annotation.StringRes
import com.example.fieldforms.domain.model.FormPage

data class FormUiState(
    val pages: List<FormPage> = emptyList(),
    val isRefreshing: Boolean = true,
    @param:StringRes val errorMessageRes: Int? = null,
    val choiceSelections: ChoiceSelections = emptyMap(),
) {
    val showFullScreenLoading: Boolean
        get() = isRefreshing && pages.isEmpty()

    val showFullScreenError: Boolean
        get() = !isRefreshing && pages.isEmpty() && errorMessageRes != null

    val showEmpty: Boolean
        get() = !isRefreshing && pages.isEmpty() && errorMessageRes == null

    val showContent: Boolean
        get() = pages.isNotEmpty()
}
