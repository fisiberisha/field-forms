package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity
import com.example.fieldforms.data.remote.dto.ChoiceQuestionDto
import com.example.fieldforms.data.remote.dto.FormNodeDto
import com.example.fieldforms.data.remote.dto.ImageQuestionDto
import com.example.fieldforms.data.remote.dto.PageDto
import com.example.fieldforms.data.remote.dto.ResponseOptionDto
import com.example.fieldforms.data.remote.dto.ResponseSetDto
import com.example.fieldforms.data.remote.dto.SectionDto
import com.example.fieldforms.data.remote.dto.TextQuestionDto
import com.example.fieldforms.domain.model.ChoiceQuestion
import com.example.fieldforms.domain.model.FormChild
import com.example.fieldforms.domain.model.FormNodeType
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.model.FormSection
import com.example.fieldforms.domain.model.ImageQuestion
import com.example.fieldforms.domain.model.ResponseOption
import com.example.fieldforms.domain.model.ResponseSet
import com.example.fieldforms.domain.model.TextQuestion

fun List<PageDto>.toDomainPages(): List<FormPage> = map { it.toDomainPage() }

fun PageDto.toDomainPage(): FormPage =
    FormPage(
        id = id,
        title = title,
        items = items.map { it.toDomainChild() },
    )

fun FormNodeDto.toDomainChild(): FormChild =
    when (this) {
        is SectionDto -> FormSection(
            id = id,
            title = title,
            items = items.map { it.toDomainChild() },
        )
        is TextQuestionDto -> TextQuestion(id = id, content = content)
        is ImageQuestionDto -> ImageQuestion(id = id, src = src, title = title)
        is ChoiceQuestionDto -> ChoiceQuestion(
            id = id,
            content = content,
            responseSet = responseSet.toDomainResponseSet(),
        )
    }

private fun ResponseSetDto.toDomainResponseSet(): ResponseSet =
    ResponseSet(
        id = id,
        multipleSelection = multipleSelection,
        responses = responses.map { it.toDomainResponseOption() },
    )

private fun ResponseOptionDto.toDomainResponseOption(): ResponseOption =
    ResponseOption(id = id, label = label, score = score)

fun List<PageDto>.toPersistModels(): FormPersistModels {
    val nodes = mutableListOf<FormNodeEntity>()
    val responseSets = mutableListOf<ResponseSetEntity>()
    val responseOptions = mutableListOf<ResponseOptionEntity>()

    forEachIndexed { pageIndex, page ->
        nodes += page.toPageNode(pageIndex)
        page.items.forEachIndexed { index, child ->
            child.collectPersistModels(parentId = page.id, sortOrder = index, nodes, responseSets, responseOptions)
        }
    }

    return FormPersistModels(
        nodes = nodes,
        responseSets = responseSets.distinctBy { it.id },
        responseOptions = responseOptions,
    )
}

private fun PageDto.toPageNode(sortOrder: Int): FormNodeEntity =
    FormNodeEntity(
        id = id,
        parentId = null,
        type = FormNodeType.PAGE,
        sortOrder = sortOrder,
        title = title,
    )

private fun FormNodeDto.collectPersistModels(
    parentId: Int,
    sortOrder: Int,
    nodes: MutableList<FormNodeEntity>,
    responseSets: MutableList<ResponseSetEntity>,
    responseOptions: MutableList<ResponseOptionEntity>,
) {
    when (this) {
        is SectionDto -> {
            nodes += FormNodeEntity(
                id = id,
                parentId = parentId,
                type = FormNodeType.SECTION,
                sortOrder = sortOrder,
                title = title,
            )
            items.forEachIndexed { index, child ->
                child.collectPersistModels(id, index, nodes, responseSets, responseOptions)
            }
        }
        is TextQuestionDto -> {
            nodes += FormNodeEntity(
                id = id,
                parentId = parentId,
                type = FormNodeType.TEXT,
                sortOrder = sortOrder,
                content = content,
            )
        }
        is ImageQuestionDto -> {
            nodes += FormNodeEntity(
                id = id,
                parentId = parentId,
                type = FormNodeType.IMAGE,
                sortOrder = sortOrder,
                title = title,
                imageSrc = src,
            )
        }
        is ChoiceQuestionDto -> {
            responseSet.collectPersistModels(responseSets, responseOptions)
            nodes += FormNodeEntity(
                id = id,
                parentId = parentId,
                type = FormNodeType.CHOICE,
                sortOrder = sortOrder,
                content = content,
                responseSetId = responseSet.id,
            )
        }
    }
}

private fun ResponseSetDto.collectPersistModels(
    responseSets: MutableList<ResponseSetEntity>,
    responseOptions: MutableList<ResponseOptionEntity>,
) {
    responseSets += ResponseSetEntity(id = id, multipleSelection = multipleSelection)
    responses.forEachIndexed { index, option ->
        responseOptions += ResponseOptionEntity(
            id = option.id,
            responseSetId = id,
            label = option.label,
            score = option.score,
            sortOrder = index,
        )
    }
}
