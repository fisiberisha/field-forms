package com.example.fieldforms.domain.model

sealed interface FormChild {
    val id: Int
}

data class FormPage(
    val id: Int,
    val title: String,
    val items: List<FormChild> = emptyList(),
)

data class FormSection(
    override val id: Int,
    val title: String,
    val items: List<FormChild> = emptyList(),
) : FormChild

data class TextQuestion(
    override val id: Int,
    val content: String,
) : FormChild

data class ImageQuestion(
    override val id: Int,
    val src: String,
    val title: String,
) : FormChild

data class ChoiceQuestion(
    override val id: Int,
    val content: String,
    val responseSet: ResponseSet,
) : FormChild

data class ResponseSet(
    val id: Int,
    val multipleSelection: Boolean,
    val responses: List<ResponseOption> = emptyList(),
)

data class ResponseOption(
    val id: Int,
    val label: String,
    val score: Int? = null,
)
