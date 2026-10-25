package com.example.fieldforms.domain.repository

import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.model.ChoiceSelections
import kotlinx.coroutines.flow.Flow

interface FormRepository {
    fun observeForm(): Flow<List<FormPage>>

    fun observeChoiceSelections(): Flow<ChoiceSelections>

    suspend fun refresh(): RefreshResult

    suspend fun updateChoiceSelection(
        questionId: Int,
        responseId: Int,
        multipleSelection: Boolean,
    )
}
