package com.example.fieldforms.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "response_options",
    foreignKeys = [
        ForeignKey(
            entity = ResponseSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["responseSetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["responseSetId"])],
)
data class ResponseOptionEntity(
    @PrimaryKey val id: Int,
    val responseSetId: Int,
    val label: String,
    val score: Int?,
    val sortOrder: Int,
)
