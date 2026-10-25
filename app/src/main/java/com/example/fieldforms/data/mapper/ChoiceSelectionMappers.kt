package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.local.entity.ChoiceSelectionEntity
import com.example.fieldforms.domain.model.ChoiceSelections

fun List<ChoiceSelectionEntity>.toChoiceSelections(): ChoiceSelections =
    groupBy { it.questionId }
        .mapValues { (_, rows) -> rows.map { it.responseId }.toSet() }

fun ChoiceSelections.toChoiceSelectionEntities(): List<ChoiceSelectionEntity> =
    flatMap { (questionId, responseIds) ->
        responseIds.map { responseId ->
            ChoiceSelectionEntity(questionId = questionId, responseId = responseId)
        }
    }
