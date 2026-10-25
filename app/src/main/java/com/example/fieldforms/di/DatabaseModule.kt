package com.example.fieldforms.di

import android.content.Context
import com.example.fieldforms.data.local.FormDao
import com.example.fieldforms.data.local.FormDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt module — consumed by the annotation processor, not from application Kotlin code. */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFormDatabase(
        @ApplicationContext context: Context,
    ): FormDatabase = FormDatabase.create(context)

    @Provides
    fun provideFormDao(database: FormDatabase): FormDao = database.formDao()
}
