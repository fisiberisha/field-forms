package com.example.fieldforms.data.mapper

import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity

data class FormPersistModels(
    val nodes: List<FormNodeEntity>,
    val responseSets: List<ResponseSetEntity>,
    val responseOptions: List<ResponseOptionEntity>,
)
