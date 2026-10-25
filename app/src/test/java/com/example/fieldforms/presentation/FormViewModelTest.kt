package com.example.fieldforms.presentation

import com.example.fieldforms.R
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshError
import com.example.fieldforms.domain.repository.RefreshResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val repository: FormRepository = mockk()
    private val pagesFlow = MutableStateFlow<List<FormPage>>(emptyList())
    private val selectionsFlow = MutableStateFlow(emptyMap<Int, Set<Int>>())

    init {
        every { repository.observeChoiceSelections() } returns selectionsFlow
    }

    @Test
    fun init_refreshSuccess_showsPagesFromObserveForm() = runTest {
        val cachedPages = listOf(FormPage(id = 1, title = "Main Page"))
        pagesFlow.value = cachedPages
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Success

        val viewModel = FormViewModel(repository)
        advanceUntilIdle()

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertNull(viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun init_observeFormEmitsCachedPagesWhileRefreshIsInFlight() = runTest {
        val cachedPages = listOf(
            FormPage(id = 10, title = "Second Page"),
            FormPage(id = 1, title = "Main Page"),
        )
        val refreshGate = CompletableDeferred<Unit>()
        every { repository.observeForm() } returns pagesFlow
        pagesFlow.value = cachedPages
        coEvery { repository.refresh() } coAnswers {
            refreshGate.await()
            RefreshResult.Success
        }

        val viewModel = FormViewModel(repository)
        advanceUntilIdle()

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertTrue(viewModel.uiState.value.isRefreshing)

        refreshGate.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun refreshFailure_withCachedPages_keepsContentAndShowsError() = runTest {
        val cachedPages = listOf(FormPage(id = 1, title = "Main Page"))
        pagesFlow.value = cachedPages
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Failure(RefreshError.Network)

        val viewModel = FormViewModel(repository)
        advanceUntilIdle()

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertEquals(
            R.string.error_refresh_offline_cache,
            viewModel.uiState.value.errorMessageRes,
        )
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertTrue(viewModel.uiState.value.showContent)
    }

    @Test
    fun refreshFailure_withoutCache_showsFullScreenError() = runTest {
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Failure(RefreshError.Network)

        val viewModel = FormViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.pages.isEmpty())
        assertEquals(
            R.string.error_load_network,
            viewModel.uiState.value.errorMessageRes,
        )
        assertTrue(viewModel.uiState.value.showFullScreenError)
    }

    @Test
    fun onChoiceResponseToggle_persistsViaRepository() = runTest {
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Success
        coEvery {
            repository.updateChoiceSelection(
                questionId = 14,
                responseId = 1021,
                multipleSelection = true,
            )
        } returns Unit

        val viewModel = FormViewModel(repository)
        advanceUntilIdle()

        viewModel.onChoiceResponseToggle(
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )
        advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.updateChoiceSelection(
                questionId = 14,
                responseId = 1021,
                multipleSelection = true,
            )
        }
    }

    @Test
    fun cancelledRefresh_doesNotSetErrorWhenNewerRefreshSucceeded() = runTest {
        pagesFlow.value = listOf(FormPage(id = 1, title = "Main Page"))
        every { repository.observeForm() } returns pagesFlow

        val refreshCalls = AtomicInteger(0)
        coEvery { repository.refresh() } coAnswers {
            when (refreshCalls.getAndIncrement()) {
                0 -> {
                    delay(10_000.milliseconds)
                    RefreshResult.Failure(RefreshError.Network)
                }
                else -> RefreshResult.Success
            }
        }

        val viewModel = FormViewModel(repository)
        advanceTimeBy(1.milliseconds)

        viewModel.refresh()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessageRes)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }
}
