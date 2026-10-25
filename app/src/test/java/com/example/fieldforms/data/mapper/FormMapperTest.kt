package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.remote.FormJsonParser
import com.example.fieldforms.domain.model.ChoiceQuestion
import com.example.fieldforms.domain.model.FormSection
import com.example.fieldforms.domain.model.ImageQuestion
import com.example.fieldforms.domain.model.TextQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormMapperTest {

    @Test
    fun dtoToDomain_mapsFullHierarchy() {
        val pages = FormJsonParser.parsePages(readFixture("lumiform-android-test.json"))

        val domain = pages.toDomainPages()

        assertEquals(2, domain.size)
        assertEquals("Main Page", domain[0].title)

        val introduction = domain[0].items[0] as FormSection
        assertEquals("Introduction", introduction.title)
        assertEquals("Welcome to the main page!", (introduction.items[0] as TextQuestion).content)
        assertEquals("Welcome Image", (introduction.items[1] as ImageQuestion).title)

        val chapter1 = domain[0].items[1] as FormSection
        val subsection = chapter1.items[1] as FormSection
        assertEquals("Subsection 1.1", subsection.title)
    }

    @Test
    fun dtoToPersistToDomain_roundTripsFixture() {
        val pages = FormJsonParser.parsePages(readFixture("lumiform-android-test.json"))

        val fromDto = pages.toDomainPages()
        val fromEntities = pages.toPersistModels().toDomainPages()

        assertEquals(fromDto, fromEntities)
    }

    @Test
    fun persistModels_preserveChoiceMetadata() {
        val pages = FormJsonParser.parsePages(readFixture("lumiform-android-test.json"))
        val persist = pages.toPersistModels()
        val domain = persist.toDomainPages()

        val chapter2 = domain[1].items[0] as FormSection
        val singleChoice = chapter2.items[1] as ChoiceQuestion
        val multiChoice = chapter2.items[2] as ChoiceQuestion

        assertFalse(singleChoice.responseSet.multipleSelection)
        assertEquals(1, singleChoice.responseSet.responses[0].score)
        assertNull(singleChoice.responseSet.responses[2].score)

        assertTrue(multiChoice.responseSet.multipleSelection)
        assertEquals("Loading bay", multiChoice.responseSet.responses[2].label)

        assertEquals(14, persist.nodes.size)
        assertEquals(2, persist.responseSets.size)
        assertEquals(6, persist.responseOptions.size)
    }

    private fun readFixture(name: String): String {
        val stream = checkNotNull(javaClass.classLoader?.getResourceAsStream(name)) {
            "Missing test resource: $name"
        }
        return stream.bufferedReader().use { it.readText() }
    }
}
