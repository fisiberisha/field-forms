package com.example.fieldforms.ui.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fieldforms.domain.model.ImageQuestion

private val ThumbnailHeight = 160.dp

@Composable
fun ImageQuestionItem(
    question: ImageQuestion,
    sectionDepth: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = (sectionDepth * 8).dp,
                top = 4.dp,
                bottom = 8.dp,
            ),
    ) {
        Text(
            text = question.title,
            style = FormTypography.questionText(),
            modifier = Modifier.padding(bottom = 6.dp),
        )
        AsyncImage(
            model = question.src,
            contentDescription = question.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(ThumbnailHeight)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
        )
    }
}
