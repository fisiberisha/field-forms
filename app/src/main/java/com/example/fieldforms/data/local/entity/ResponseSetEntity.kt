package com.example.fieldforms.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "response_sets")
data class ResponseSetEntity(
    @PrimaryKey val id: Int,
    val multipleSelection: Boolean,
)
