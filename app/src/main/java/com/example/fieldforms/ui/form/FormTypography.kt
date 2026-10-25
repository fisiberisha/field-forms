package com.example.fieldforms.ui.form

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object FormTypography {

    @Composable
    fun pageTitle(): TextStyle =
        MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
        )

    @Composable
    fun sectionTitle(depth: Int): TextStyle {
        val base = when (depth) {
            0 -> MaterialTheme.typography.titleLarge
            1 -> MaterialTheme.typography.titleMedium
            else -> MaterialTheme.typography.titleSmall
        }
        return base.copy(fontWeight = FontWeight.SemiBold)
    }

    @Composable
    fun questionText(): TextStyle =
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
}
