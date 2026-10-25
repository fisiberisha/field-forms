package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity
import com.example.fieldforms.domain.model.ChoiceQuestion
import com.example.fieldforms.domain.model.FormChild
import com.example.fieldforms.domain.model.FormNodeType
import com.example.fieldforms.domain.model.FormPage
import com.example.fieldforms.domain.model.FormSection
import com.example.fieldforms.domain.model.ImageQuestion
import com.example.fieldforms.domain.model.ResponseOption
import com.example.fieldforms.domain.model.ResponseSet
import com.example.fieldforms.domain.model.TextQuestion

fun List<FormNodeEntity>.toDomainPages(
    responseSets: List<ResponseSetEntity>,
    responseOptions: List<ResponseOptionEntity>,
): List<FormPage> {
    val nodesByParent = groupBy { it.parentId }
    val responseSetById = responseSets.associateBy { it.id }
    val optionsBySetId = responseOptions
        .groupBy { it.responseSetId }
        .mapValues { (_, options) -> options.sortedBy { it.sortOrder } }

    fun mapChild(entity: FormNodeEntity): FormChild =
        when (entity.type) {
            FormNodeType.SECTION -> FormSection(
                id = entity.id,
                title = entity.title.orEmpty(),
                items = childrenOf(entity.id, nodesByParent, ::mapChild),
            )
            FormNodeType.TEXT -> TextQuestion(
                id = entity.id,
                content = entity.content.orEmpty(),
            )
            FormNodeType.IMAGE -> ImageQuestion(
                id = entity.id,
                src = entity.imageSrc.orEmpty(),
                title = entity.title.orEmpty(),
            )
            FormNodeType.CHOICE -> {
                val setId = requireNotNull(entity.responseSetId) {
                    "Choice node ${entity.id} is missing responseSetId"
                }
                val setEntity = requireNotNull(responseSetById[setId]) {
                    "Missing response set $setId for choice node ${entity.id}"
                }
                ChoiceQuestion(
                    id = entity.id,
                    content = entity.content.orEmpty(),
                    responseSet = setEntity.toDomainResponseSet(optionsBySetId[setId].orEmpty()),
                )
            }
            else -> error("Unexpected child node type: ${entity.type}")
        }

    return nodesByParent[null]
        .orEmpty()
        .sortedBy { it.sortOrder }
        .filter { it.type == FormNodeType.PAGE }
        .map { pageEntity ->
            FormPage(
                id = pageEntity.id,
                title = pageEntity.title.orEmpty(),
                items = childrenOf(pageEntity.id, nodesByParent, ::mapChild),
            )
        }
}

private fun <T> childrenOf(
    parentId: Int,
    nodesByParent: Map<Int?, List<FormNodeEntity>>,
    mapChild: (FormNodeEntity) -> T,
): List<T> =
    nodesByParent[parentId]
        .orEmpty()
        .sortedBy { it.sortOrder }
        .map(mapChild)

private fun ResponseSetEntity.toDomainResponseSet(
    options: List<ResponseOptionEntity>,
): ResponseSet =
    ResponseSet(
        id = id,
        multipleSelection = multipleSelection,
        responses = options.map { it.toDomainResponseOption() },
    )

private fun ResponseOptionEntity.toDomainResponseOption(): ResponseOption =
    ResponseOption(id = id, label = label, score = score)

fun FormPersistModels.toDomainPages(): List<FormPage> =
    nodes.toDomainPages(responseSets, responseOptions)
