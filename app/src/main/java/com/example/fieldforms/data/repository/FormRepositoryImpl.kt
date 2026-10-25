package com.example.fieldforms.data.repository

import com.example.fieldforms.data.local.FormDao
import com.example.fieldforms.data.mapper.toChoiceSelectionEntities
import com.example.fieldforms.data.mapper.toChoiceSelections
import com.example.fieldforms.data.mapper.toDomainPages
import com.example.fieldforms.data.mapper.toPersistModels
import com.example.fieldforms.data.remote.FormApiService
import com.example.fieldforms.domain.model.ChoiceSelectionLogic
import com.example.fieldforms.domain.model.ChoiceSelections
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val formApiService: FormApiService,
) : FormRepository {

    private val choiceSelectionMutex = Mutex()

    override fun observeForm(): Flow<List<FormPage>> =
        combine(
            formDao.observeAllNodes(),
            formDao.observeResponseSets(),
            formDao.observeResponseOptions(),
        ) { nodes, responseSets, responseOptions ->
            nodes.toDomainPages(responseSets, responseOptions)
        }

    override fun observeChoiceSelections(): Flow<ChoiceSelections> =
        formDao.observeChoiceSelections().map { rows -> rows.toChoiceSelections() }

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
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            RefreshResult.Failure(exception.toRefreshError())
        }

    override suspend fun updateChoiceSelection(
        questionId: Int,
        responseId: Int,
        multipleSelection: Boolean,
    ) {
        choiceSelectionMutex.withLock {
            val current = formDao.getChoiceSelections().toChoiceSelections()
            val updated = ChoiceSelectionLogic.toggleSelection(
                current = current,
                questionId = questionId,
                responseId = responseId,
                multipleSelection = multipleSelection,
            )
            formDao.replaceAllChoiceSelections(updated.toChoiceSelectionEntities())
        }
    }
}
