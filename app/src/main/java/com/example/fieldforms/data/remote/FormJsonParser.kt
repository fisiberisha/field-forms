package com.example.fieldforms.data.remote

import com.example.fieldforms.data.remote.dto.ChoiceQuestionDto
import com.example.fieldforms.data.remote.dto.FormNodeDto
import com.example.fieldforms.data.remote.dto.ImageQuestionDto
import com.example.fieldforms.data.remote.dto.PageDto
import com.example.fieldforms.data.remote.dto.SectionDto
import com.example.fieldforms.data.remote.dto.TextQuestionDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object FormJsonParser {

    private val formNodePolymorphicFactory: PolymorphicJsonAdapterFactory<FormNodeDto> =
        PolymorphicJsonAdapterFactory.of(FormNodeDto::class.java, "type")
            .withSubtype(SectionDto::class.java, "section")
            .withSubtype(TextQuestionDto::class.java, "text")
            .withSubtype(ImageQuestionDto::class.java, "image")
            .withSubtype(ChoiceQuestionDto::class.java, "choice")

    fun createMoshi(): Moshi =
        Moshi.Builder()
            .add(formNodePolymorphicFactory)
            .addLast(KotlinJsonAdapterFactory())
            .build()

    private val pagesJsonAdapter by lazy {
        createMoshi().adapter<List<PageDto>>(
            Types.newParameterizedType(List::class.java, PageDto::class.java),
        )
    }

    fun parsePages(json: String): List<PageDto> =
        pagesJsonAdapter.fromJson(json) ?: emptyList()
}
