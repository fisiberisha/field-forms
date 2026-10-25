package com.example.fieldforms.domain.model

fun FormPage.choiceQuestionIds(): List<Int> {
    val ids = mutableListOf<Int>()
    fun walk(children: List<FormChild>) {
        for (child in children) {
            when (child) {
                is ChoiceQuestion -> ids.add(child.id)
                is FormSection -> walk(child.items)
                else -> Unit
            }
        }
    }
    walk(items)
    return ids
}

fun FormPage.answeredChoiceCount(selections: ChoiceSelections): Int =
    choiceQuestionIds().count { questionId -> !selections[questionId].isNullOrEmpty() }
