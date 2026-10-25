package com.example.fieldforms.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChoiceSelectionLogicTest {

    @Test
    fun singleSelection_selectsOneResponse() {
        val result = ChoiceSelectionLogic.toggleSelection(
            current = emptyMap(),
            questionId = 13,
            responseId = 1011,
            multipleSelection = false,
        )

        assertEquals(setOf(1011), result[13])
    }

    @Test
    fun singleSelection_replacesPreviousSelection() {
        val current = mapOf(13 to setOf(1011))

        val result = ChoiceSelectionLogic.toggleSelection(
            current = current,
            questionId = 13,
            responseId = 1012,
            multipleSelection = false,
        )

        assertEquals(setOf(1012), result[13])
    }

    @Test
    fun singleSelection_deselectsWhenSameResponseTapped() {
        val current = mapOf(13 to setOf(1011))

        val result = ChoiceSelectionLogic.toggleSelection(
            current = current,
            questionId = 13,
            responseId = 1011,
            multipleSelection = false,
        )

        assertFalse(result.containsKey(13))
    }

    @Test
    fun multiSelection_togglesResponsesIndependently() {
        var selections = ChoiceSelectionLogic.toggleSelection(
            current = emptyMap(),
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )
        selections = ChoiceSelectionLogic.toggleSelection(
            current = selections,
            questionId = 14,
            responseId = 1022,
            multipleSelection = true,
        )

        assertEquals(setOf(1021, 1022), selections[14])

        selections = ChoiceSelectionLogic.toggleSelection(
            current = selections,
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )

        assertEquals(setOf(1022), selections[14])
    }

    @Test
    fun selectionsAreIsolatedPerQuestion() {
        val current = mapOf(13 to setOf(1011))

        val result = ChoiceSelectionLogic.toggleSelection(
            current = current,
            questionId = 14,
            responseId = 1021,
            multipleSelection = true,
        )

        assertEquals(setOf(1011), result[13])
        assertEquals(setOf(1021), result[14])
        assertTrue(result.keys.containsAll(setOf(13, 14)))
    }
}
