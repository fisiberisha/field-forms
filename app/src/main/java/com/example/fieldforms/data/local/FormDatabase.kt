package com.example.fieldforms.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity

@Database(
    entities = [
        FormNodeEntity::class,
        ResponseSetEntity::class,
        ResponseOptionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class FormDatabase : RoomDatabase() {

    abstract fun formDao(): FormDao

    companion object {
        private const val DATABASE_NAME = "field_forms.db"

        fun create(context: Context): FormDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                FormDatabase::class.java,
                DATABASE_NAME,
            ).fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
