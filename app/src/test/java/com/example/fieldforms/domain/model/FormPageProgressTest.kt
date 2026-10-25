package com.example.fieldforms.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FormPageProgressTest {

    @Test
    fun answeredChoiceCount_countsQuestionsWithSelections() {
        val page = FormPage(
            id = 10,
            title = "Second Page",
            items = listOf(
                FormSection(
                    id = 11,
                    title = "Chapter 2",
                    items = listOf(
                        TextQuestion(id = 12, content = "Text"),
                        ChoiceQuestion(
                            id = 13,
                            content = "Single",
                            responseSet = ResponseSet(
                                id = 101,
                                multipleSelection = false,
                                responses = listOf(ResponseOption(1011, "A")),
                            ),
                        ),
                        ChoiceQuestion(
                            id = 14,
                            content = "Multi",
                            responseSet = ResponseSet(
                                id = 102,
                                multipleSelection = true,
                                responses = listOf(ResponseOption(1021, "B")),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val selections = mapOf(13 to setOf(1011))

        assertEquals(2, page.choiceQuestionIds().size)
        assertEquals(1, page.answeredChoiceCount(selections))
    }
}
