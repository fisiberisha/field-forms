package com.example.fieldforms.ui.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.fieldforms.domain.model.ChoiceQuestion

@Composable
fun ChoiceQuestionItem(
    question: ChoiceQuestion,
    selectedResponseIds: Set<Int>,
    onResponseToggle: (responseId: Int) -> Unit,
    sectionDepth: Int,
    modifier: Modifier = Modifier,
) {
    val multipleSelection = question.responseSet.multipleSelection

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = (sectionDepth * 8).dp,
                top = 4.dp,
                bottom = 8.dp,
            )
            .then(if (!multipleSelection) Modifier.selectableGroup() else Modifier),
    ) {
        Text(
            text = question.content,
            style = FormTypography.questionText(),
            modifier = Modifier.padding(bottom = 6.dp),
        )

        question.responseSet.responses.forEach { response ->
            val isSelected = response.id in selectedResponseIds
            if (multipleSelection) {
                MultiChoiceOptionRow(
                    label = response.label,
                    isSelected = isSelected,
                    onToggle = { onResponseToggle(response.id) },
                )
            } else {
                SingleChoiceOptionRow(
                    label = response.label,
                    isSelected = isSelected,
                    onSelect = { onResponseToggle(response.id) },
                )
            }
        }
    }
}

@Composable
private fun SingleChoiceOptionRow(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton,
            )
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun MultiChoiceOptionRow(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
