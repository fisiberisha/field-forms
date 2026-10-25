package com.example.fieldforms.data.repository

import com.example.fieldforms.data.local.FormDao
import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity
import com.example.fieldforms.data.mapper.toPersistModels
import com.example.fieldforms.data.remote.FormApiService
import com.example.fieldforms.data.remote.FormJsonParser
import com.example.fieldforms.domain.model.FormNodeType
import com.example.fieldforms.domain.model.FormSection
import com.example.fieldforms.domain.repository.RefreshResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class FormRepositoryImplTest {

    private val formDao: FormDao = mockk(relaxed = true)
    private val formApiService: FormApiService = mockk()
    private val repository = FormRepositoryImpl(formDao, formApiService)

    @Test
    fun observeForm_mapsCombinedDaoFlowsToDomainPages() = runTest {
        val page = FormNodeEntity(
            id = 1,
            parentId = null,
            type = FormNodeType.PAGE,
            sortOrder = 0,
            title = "Main Page",
        )
        val section = FormNodeEntity(
            id = 2,
            parentId = 1,
            type = FormNodeType.SECTION,
            sortOrder = 0,
            title = "Introduction",
        )
        val text = FormNodeEntity(
            id = 3,
            parentId = 2,
            type = FormNodeType.TEXT,
            sortOrder = 0,
            content = "Hello",
        )

        every { formDao.observeAllNodes() } returns flowOf(listOf(page, section, text))
        every { formDao.observeResponseSets() } returns flowOf(emptyList())
        every { formDao.observeResponseOptions() } returns flowOf(emptyList())

        val pages = repository.observeForm().first()

        assertEquals(1, pages.size)
        assertEquals("Main Page", pages[0].title)
        assertEquals("Introduction", (pages[0].items[0] as FormSection).title)
    }

    @Test
    fun refresh_success_replacesCacheTransactionally() = runTest {
        val remotePages = FormJsonParser.parsePages(readFixture("lumiform-android-test.json"))
        val persistModels = remotePages.toPersistModels()

        coEvery { formApiService.getPages() } returns remotePages
        coEvery {
            formDao.replaceAll(
                nodes = persistModels.nodes,
                responseSets = persistModels.responseSets,
                responseOptions = persistModels.responseOptions,
            )
        } returns Unit

        val result = repository.refresh()

        assertTrue(result is RefreshResult.Success)
        coVerify(exactly = 1) {
            formDao.replaceAll(
                nodes = persistModels.nodes,
                responseSets = persistModels.responseSets,
                responseOptions = persistModels.responseOptions,
            )
        }
    }

    @Test
    fun refresh_failure_leavesCacheUntouched() = runTest {
        coEvery { formApiService.getPages() } throws IOException("offline")

        val result = repository.refresh()

        assertTrue(result is RefreshResult.Failure)
        coVerify(exactly = 0) {
            formDao.replaceAll(
                nodes = any(),
                responseSets = any(),
                responseOptions = any(),
            )
        }
    }

    private fun readFixture(name: String): String {
        val stream = checkNotNull(javaClass.classLoader?.getResourceAsStream(name)) {
            "Missing test resource: $name"
        }
        return stream.bufferedReader().use { it.readText() }
    }
}
