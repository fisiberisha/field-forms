package com.example.fieldforms.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "form_nodes",
    indices = [Index(value = ["parentId"]), Index(value = ["responseSetId"])],
)
data class FormNodeEntity(
    @PrimaryKey val id: Int,
    val parentId: Int?,
    val type: String,
    val sortOrder: Int,
    val title: String? = null,
    val content: String? = null,
    val imageSrc: String? = null,
    val responseSetId: Int? = null,
)
