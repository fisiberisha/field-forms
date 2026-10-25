package com.example.fieldforms.ui.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fieldforms.domain.model.ChoiceQuestion
import com.example.fieldforms.domain.model.FormChild
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.model.FormSection
import com.example.fieldforms.domain.model.ImageQuestion
import com.example.fieldforms.domain.model.TextQuestion

@Composable
fun FormPageContent(
    page: FormPage,
    choiceSelections: Map<Int, Set<Int>>,
    onImageClick: (ImageQuestion) -> Unit,
    onChoiceResponseToggle: (questionId: Int, responseId: Int, multipleSelection: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = page.title,
            style = FormTypography.pageTitle(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        page.items.forEach { child ->
            FormChildContent(
                child = child,
                sectionDepth = 0,
                choiceSelections = choiceSelections,
                onImageClick = onImageClick,
                onChoiceResponseToggle = onChoiceResponseToggle,
            )
        }
    }
}

@Composable
private fun FormChildContent(
    child: FormChild,
    sectionDepth: Int,
    choiceSelections: Map<Int, Set<Int>>,
    onImageClick: (ImageQuestion) -> Unit,
    onChoiceResponseToggle: (questionId: Int, responseId: Int, multipleSelection: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (child) {
        is FormSection -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = (sectionDepth * 8).dp, top = 8.dp, bottom = 4.dp),
            ) {
                Text(
                    text = child.title,
                    style = FormTypography.sectionTitle(sectionDepth),
                )
                Spacer(modifier = Modifier.height(4.dp))
                child.items.forEach { nested ->
                    FormChildContent(
                        child = nested,
                        sectionDepth = sectionDepth + 1,
                        choiceSelections = choiceSelections,
                        onImageClick = onImageClick,
                        onChoiceResponseToggle = onChoiceResponseToggle,
                    )
                }
            }
        }
        is TextQuestion -> {
            Text(
                text = child.content,
                style = FormTypography.questionText(),
                modifier = modifier.padding(
                    start = (sectionDepth * 8).dp,
                    top = 4.dp,
                    bottom = 4.dp,
                ),
            )
        }
        is ImageQuestion -> {
            ImageQuestionItem(
                question = child,
                sectionDepth = sectionDepth,
                onClick = { onImageClick(child) },
                modifier = modifier,
            )
        }
        is ChoiceQuestion -> {
            ChoiceQuestionItem(
                question = child,
                selectedResponseIds = choiceSelections[child.id].orEmpty(),
                onResponseToggle = { responseId ->
                    onChoiceResponseToggle(
                        child.id,
                        responseId,
                        child.responseSet.multipleSelection,
                    )
                },
                sectionDepth = sectionDepth,
                modifier = modifier,
            )
        }
    }
}
