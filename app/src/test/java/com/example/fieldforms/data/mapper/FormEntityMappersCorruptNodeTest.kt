package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.domain.model.FormNodeType
import com.example.fieldforms.domain.model.TextQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormEntityMappersCorruptNodeTest {

    @Test
    fun toDomainPages_skipsChoiceNodeWithMissingResponseSet() {
        val page = FormNodeEntity(
            id = 1,
            parentId = null,
            type = FormNodeType.PAGE,
            sortOrder = 0,
            title = "Page",
        )
        val validText = FormNodeEntity(
            id = 2,
            parentId = 1,
            type = FormNodeType.TEXT,
            sortOrder = 0,
            content = "Hello",
        )
        val corruptChoice = FormNodeEntity(
            id = 3,
            parentId = 1,
            type = FormNodeType.CHOICE,
            sortOrder = 1,
            content = "Pick one",
            responseSetId = null,
        )

        val pages = listOf(page, validText, corruptChoice).toDomainPages(
            responseSets = emptyList(),
            responseOptions = emptyList(),
        )

        assertEquals(1, pages.size)
        assertEquals(1, pages[0].items.size)
        assertTrue(pages[0].items[0] is TextQuestion)
    }
}
