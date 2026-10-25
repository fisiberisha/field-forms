package com.example.fieldforms.presentation

typealias ChoiceSelections = Map<Int, Set<Int>>

object ChoiceSelectionLogic {

    fun toggleSelection(
        current: ChoiceSelections,
        questionId: Int,
        responseId: Int,
        multipleSelection: Boolean,
    ): ChoiceSelections {
        val selectedForQuestion = current[questionId].orEmpty()
        val updatedForQuestion = if (multipleSelection) {
            toggleMultiSelection(selectedForQuestion, responseId)
        } else {
            toggleSingleSelection(selectedForQuestion, responseId)
        }

        return if (updatedForQuestion.isEmpty()) {
            current - questionId
        } else {
            current + (questionId to updatedForQuestion)
        }
    }

    private fun toggleMultiSelection(
        selected: Set<Int>,
        responseId: Int,
    ): Set<Int> =
        if (responseId in selected) selected - responseId else selected + responseId

    private fun toggleSingleSelection(
        selected: Set<Int>,
        responseId: Int,
    ): Set<Int> =
        if (responseId in selected) emptySet() else setOf(responseId)
}
