package com.example.fieldforms.data.repository

import com.example.fieldforms.data.local.FormDao
import com.example.fieldforms.data.mapper.toDomainPages
import com.example.fieldforms.data.mapper.toPersistModels
import com.example.fieldforms.data.remote.FormApiService
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val formApiService: FormApiService,
) : FormRepository {

    override fun observeForm(): Flow<List<FormPage>> =
        combine(
            formDao.observeAllNodes(),
            formDao.observeResponseSets(),
            formDao.observeResponseOptions(),
        ) { nodes, responseSets, responseOptions ->
            nodes.toDomainPages(responseSets, responseOptions)
        }

    override suspend fun refresh(): RefreshResult =
        try {
            val pages = formApiService.getPages()
            val persistModels = pages.toPersistModels()
            formDao.replaceAll(
                nodes = persistModels.nodes,
                responseSets = persistModels.responseSets,
                responseOptions = persistModels.responseOptions,
            )
            RefreshResult.Success
        } catch (exception: Exception) {
            RefreshResult.Failure(exception.toRefreshError())
        }
}
