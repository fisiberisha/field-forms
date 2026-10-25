package com.example.fieldforms.ui.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.fieldforms.domain.model.answeredChoiceCount
import com.example.fieldforms.domain.model.choiceQuestionIds
import java.text.DateFormat
import java.util.Date
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fieldforms.R
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.model.ImageQuestion
import com.example.fieldforms.presentation.FormUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    uiState: FormUiState,
    onRetry: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onImageClick: (ImageQuestion) -> Unit,
    onChoiceResponseToggle: (questionId: Int, responseId: Int, multipleSelection: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.app_bar_title)) })
        },
    ) { innerPadding ->
        when {
            uiState.showFullScreenLoading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }
            uiState.showFullScreenError -> {
                PullToRefreshFormContainer(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRetry,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    ErrorContent(
                        messageRes = checkNotNull(uiState.errorMessageRes),
                        onRetry = onRetry,
                    )
                }
            }
            uiState.showEmpty -> {
                PullToRefreshFormContainer(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRetry,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    EmptyContent(onRetry = onRetry)
                }
            }
            uiState.showContent -> {
                PullToRefreshFormContainer(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRetry,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    FormContent(
                        uiState = uiState,
                        onRetry = onRetry,
                        onPageSelected = onPageSelected,
                        onImageClick = onImageClick,
                        onChoiceResponseToggle = onChoiceResponseToggle,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullToRefreshFormContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        content()
    }
}

@Composable
private fun FormContent(
    uiState: FormUiState,
    onRetry: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onImageClick: (ImageQuestion) -> Unit,
    onChoiceResponseToggle: (questionId: Int, responseId: Int, multipleSelection: Boolean) -> Unit,
) {
    val selectedPage = uiState.selectedPage ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.errorMessageRes != null) {
            RefreshErrorBanner(
                messageRes = uiState.errorMessageRes,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        if (uiState.pages.size > 1) {
            PrimaryTabRow(selectedTabIndex = uiState.selectedPageIndex) {
                uiState.pages.forEachIndexed { index, page ->
                    PageTab(
                        page = page,
                        selected = uiState.selectedPageIndex == index,
                        onClick = { onPageSelected(index) },
                    )
                }
            }
        }

        FormPageStatusLines(
            page = selectedPage,
            choiceSelections = uiState.choiceSelections,
            lastRefreshEpochMillis = uiState.lastRefreshEpochMillis,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "page_${selectedPage.id}") {
                FormPageContent(
                    page = selectedPage,
                    showPageTitle = uiState.pages.size <= 1,
                    choiceSelections = uiState.choiceSelections,
                    onImageClick = onImageClick,
                    onChoiceResponseToggle = onChoiceResponseToggle,
                )
            }
        }
    }
}

@Composable
private fun FormPageStatusLines(
    page: FormPage,
    choiceSelections: Map<Int, Set<Int>>,
    lastRefreshEpochMillis: Long?,
    modifier: Modifier = Modifier,
) {
    val choiceCount = page.choiceQuestionIds().size
    if (choiceCount == 0 && lastRefreshEpochMillis == null) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (choiceCount > 0) {
            val answered = page.answeredChoiceCount(choiceSelections)
            Text(
                text = stringResource(
                    R.string.form_progress_answered,
                    answered,
                    choiceCount,
                ),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        if (lastRefreshEpochMillis != null) {
            val formatted = remember(lastRefreshEpochMillis) {
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(lastRefreshEpochMillis))
            }
            Text(
                text = stringResource(R.string.form_last_updated, formatted),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PageTab(
    page: FormPage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = { Text(text = page.title) },
    )
}

@Composable
private fun RefreshErrorBanner(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.empty_no_form_data),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = stringResource(R.string.action_load_form))
        }
    }
}

@Composable
private fun ErrorContent(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}
