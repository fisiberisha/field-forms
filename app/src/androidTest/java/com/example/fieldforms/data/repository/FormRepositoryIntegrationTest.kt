package com.example.fieldforms.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fieldforms.data.local.FormDatabase
import com.example.fieldforms.data.mapper.toPersistModels
import com.example.fieldforms.data.remote.FormApiService
import com.example.fieldforms.data.remote.FormJsonParser
import com.example.fieldforms.data.remote.dto.PageDto
import com.example.fieldforms.domain.repository.RefreshError
import com.example.fieldforms.domain.repository.RefreshResult
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FormRepositoryIntegrationTest {

    private lateinit var database: FormDatabase
    private var remotePages: List<PageDto> = emptyList()
    private var shouldFailRefresh: Boolean = false

    private val api = object : FormApiService {
        override suspend fun getPages(): List<PageDto> {
            if (shouldFailRefresh) throw IOException("network down")
            return remotePages
        }
    }

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, FormDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        remotePages = FormJsonParser.parsePages(readFixture("lumiform-android-test.json"))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun successfulRefresh_replacesPreviousFormRows() = runBlocking {
        val repository = FormRepositoryImpl(database.formDao(), api)

        assertEquals(RefreshResult.Success, repository.refresh())
        val idsAfterFirst = database.formDao().observeAllNodes().first().map { it.id }.toSet()
        assertTrue(idsAfterFirst.contains(1))

        remotePages = FormJsonParser.parsePages(readFixture("lumiform-android-test-replacement.json"))
        val replacementModels = remotePages.toPersistModels()
        assertEquals(RefreshResult.Success, repository.refresh())

        val nodesAfterSecond = database.formDao().observeAllNodes().first()
        val idsAfterSecond = nodesAfterSecond.map { it.id }.toSet()
        assertFalse(idsAfterSecond.contains(1))
        assertTrue(idsAfterSecond.contains(100))
        assertEquals(replacementModels.nodes.size, nodesAfterSecond.size)
    }

    @Test
    fun failedRefresh_leavesCachedFormRowsInDatabase() = runBlocking {
        val repository = FormRepositoryImpl(database.formDao(), api)
        val persistModels = remotePages.toPersistModels()

        assertEquals(RefreshResult.Success, repository.refresh())

        shouldFailRefresh = true
        val result = repository.refresh()
        assertTrue(result is RefreshResult.Failure)
        assertEquals(RefreshError.Network, (result as RefreshResult.Failure).error)

        val nodesAfterFailure = database.formDao().observeAllNodes().first()
        assertEquals(persistModels.nodes.size, nodesAfterFailure.size)
    }

    @Test
    fun concurrentChoiceUpdates_keepBothSelections() = runBlocking {
        val repository = FormRepositoryImpl(database.formDao(), api)
        repository.refresh()

        val first = async {
            repository.updateChoiceSelection(
                questionId = 14,
                responseId = 1021,
                multipleSelection = true,
            )
        }
        val second = async {
            repository.updateChoiceSelection(
                questionId = 14,
                responseId = 1022,
                multipleSelection = true,
            )
        }
        first.await()
        second.await()

        val selections = repository.observeChoiceSelections().first()
        assertEquals(setOf(1021, 1022), selections[14])
    }

    @Test
    fun choiceSelectionsPersistAcrossRepositoryUpdates() = runBlocking {
        val repository = FormRepositoryImpl(database.formDao(), api)
        repository.refresh()

        repository.updateChoiceSelection(
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )
        repository.updateChoiceSelection(
            questionId = 14,
            responseId = 1022,
            multipleSelection = true,
        )

        repository.refresh()

        val selections = repository.observeChoiceSelections().first()
        assertEquals(setOf(1021, 1022), selections[14])
    }

    private fun readFixture(name: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(name).bufferedReader().use { it.readText() }
    }
}
