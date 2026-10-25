package com.example.fieldforms.data.remote.dto

import com.squareup.moshi.Json

data class PageDto(
    val id: Int,
    val type: String,
    val title: String,
    val items: List<FormNodeDto> = emptyList(),
)

sealed interface FormNodeDto {
    val id: Int
    val type: String
}

data class SectionDto(
    override val id: Int,
    override val type: String,
    val title: String,
    val items: List<FormNodeDto> = emptyList(),
) : FormNodeDto

data class TextQuestionDto(
    override val id: Int,
    override val type: String,
    val content: String,
) : FormNodeDto

data class ImageQuestionDto(
    override val id: Int,
    override val type: String,
    val src: String,
    val title: String,
) : FormNodeDto

data class ChoiceQuestionDto(
    override val id: Int,
    override val type: String,
    val content: String,
    @param:Json(name = "response_set") val responseSet: ResponseSetDto,
) : FormNodeDto

data class ResponseSetDto(
    val id: Int,
    @param:Json(name = "multiple_selection") val multipleSelection: Boolean,
    val responses: List<ResponseOptionDto> = emptyList(),
)

data class ResponseOptionDto(
    val id: Int,
    val label: String,
    val score: Int? = null,
)
