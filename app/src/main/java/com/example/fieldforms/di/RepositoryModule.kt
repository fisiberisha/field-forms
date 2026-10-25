package com.example.fieldforms.di

import com.example.fieldforms.data.repository.FormRepositoryImpl
import com.example.fieldforms.domain.repository.FormRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFormRepository(
        formRepositoryImpl: FormRepositoryImpl,
    ): FormRepository
}
