package com.example.fieldforms.data.remote

import com.example.fieldforms.data.remote.dto.ChoiceQuestionDto
import com.example.fieldforms.data.remote.dto.ImageQuestionDto
import com.example.fieldforms.data.remote.dto.SectionDto
import com.example.fieldforms.data.remote.dto.TextQuestionDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormJsonParserTest {

    @Test
    fun parsePages_parsesRootPagesAndNestedHierarchy() {
        val json = readFixture("lumiform-android-test.json")

        val pages = FormJsonParser.parsePages(json)

        assertEquals(2, pages.size)
        assertEquals("page", pages[0].type)
        assertEquals(1, pages[0].id)
        assertEquals("Main Page", pages[0].title)

        val introduction = pages[0].items[0] as SectionDto
        assertEquals("Introduction", introduction.title)
        assertEquals(2, introduction.items.size)

        val welcomeText = introduction.items[0] as TextQuestionDto
        assertEquals("Welcome to the main page!", welcomeText.content)

        val welcomeImage = introduction.items[1] as ImageQuestionDto
        assertEquals("Welcome Image", welcomeImage.title)
        assertTrue(welcomeImage.src.contains("android.png"))

        val chapter1 = pages[0].items[1] as SectionDto
        val subsection = chapter1.items[1] as SectionDto
        assertEquals("Subsection 1.1", subsection.title)
        assertEquals(
            "This is a subsection under Chapter 1.",
            (subsection.items[0] as TextQuestionDto).content,
        )
    }

    @Test
    fun parsePages_parsesChoiceQuestionsWithSingleAndMultipleSelection() {
        val json = readFixture("lumiform-android-test.json")

        val pages = FormJsonParser.parsePages(json)
        val secondPageSection = pages[1].items[0] as SectionDto
        val singleChoice = secondPageSection.items[1] as ChoiceQuestionDto
        val multiChoice = secondPageSection.items[2] as ChoiceQuestionDto

        assertEquals("What is the main topic of Chapter 2?", singleChoice.content)
        assertFalse(singleChoice.responseSet.multipleSelection)
        assertEquals(3, singleChoice.responseSet.responses.size)
        assertEquals(1, singleChoice.responseSet.responses[0].score)
        assertNull(singleChoice.responseSet.responses[2].score)

        assertEquals("Which areas were inspected?", multiChoice.content)
        assertTrue(multiChoice.responseSet.multipleSelection)
        assertEquals(3, multiChoice.responseSet.responses.size)
        assertEquals("Loading bay", multiChoice.responseSet.responses[2].label)
    }

    private fun readFixture(name: String): String {
        val stream = checkNotNull(javaClass.classLoader?.getResourceAsStream(name)) {
            "Missing test resource: $name"
        }
        return stream.bufferedReader().use { it.readText() }
    }
}
