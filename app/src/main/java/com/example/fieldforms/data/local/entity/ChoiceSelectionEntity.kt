package com.example.fieldforms.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "choice_selections",
    primaryKeys = ["questionId", "responseId"],
)
data class ChoiceSelectionEntity(
    val questionId: Int,
    val responseId: Int,
)
