package com.example.fieldforms.presentation

import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.repository.FormRepository
import com.example.fieldforms.domain.repository.RefreshResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class FormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: FormRepository = mockk()
    private val pagesFlow = MutableStateFlow<List<FormPage>>(emptyList())

    @Test
    fun init_refreshSuccess_showsPagesFromObserveForm() = runTest {
        val cachedPages = listOf(FormPage(id = 1, title = "Main Page"))
        pagesFlow.value = cachedPages
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Success

        val viewModel = FormViewModel(repository)

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun init_observeFormEmitsCachedPages_beforeRefreshCompletes() = runTest {
        val cachedPages = listOf(
            FormPage(id = 10, title = "Second Page"),
            FormPage(id = 1, title = "Main Page"),
        )
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } coAnswers {
            pagesFlow.value = cachedPages
            RefreshResult.Success
        }

        val viewModel = FormViewModel(repository)

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun refreshFailure_withCachedPages_keepsContentAndShowsError() = runTest {
        val cachedPages = listOf(FormPage(id = 1, title = "Main Page"))
        pagesFlow.value = cachedPages
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Failure(IOException("offline"))

        val viewModel = FormViewModel(repository)

        assertEquals(cachedPages, viewModel.uiState.value.pages)
        assertEquals("offline", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showContent)
    }

    @Test
    fun refreshFailure_withoutCache_showsFullScreenError() = runTest {
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Failure(IOException("offline"))

        val viewModel = FormViewModel(repository)

        assertTrue(viewModel.uiState.value.pages.isEmpty())
        assertEquals("offline", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.showFullScreenError)
    }

    @Test
    fun onChoiceResponseToggle_updatesChoiceSelections() = runTest {
        every { repository.observeForm() } returns pagesFlow
        coEvery { repository.refresh() } returns RefreshResult.Success

        val viewModel = FormViewModel(repository)

        viewModel.onChoiceResponseToggle(
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )
        viewModel.onChoiceResponseToggle(
            questionId = 14,
            responseId = 1022,
            multipleSelection = true,
        )

        assertEquals(setOf(1021, 1022), viewModel.uiState.value.choiceSelections[14])
    }
}
