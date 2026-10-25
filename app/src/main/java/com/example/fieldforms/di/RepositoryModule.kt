package com.example.fieldforms.di

import com.example.fieldforms.data.repository.FormRepositoryImpl
import com.example.fieldforms.domain.repository.FormRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt module — consumed by the annotation processor, not from application Kotlin code. */
@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFormRepository(
        formRepositoryImpl: FormRepositoryImpl,
    ): FormRepository
}
