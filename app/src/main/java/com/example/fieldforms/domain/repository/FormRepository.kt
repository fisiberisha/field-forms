package com.example.fieldforms.domain.repository

import com.example.fieldforms.domain.model.FormPage
import kotlinx.coroutines.flow.Flow

interface FormRepository {
    fun observeForm(): Flow<List<FormPage>>

    suspend fun refresh(): RefreshResult
}
