package com.digidocx.core.di

import com.digidocx.core.database.dao.DocumentDao
import com.digidocx.core.database.dao.MockDocumentDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindDocumentDao(
        mockDocumentDao: MockDocumentDao
    ): DocumentDao
}