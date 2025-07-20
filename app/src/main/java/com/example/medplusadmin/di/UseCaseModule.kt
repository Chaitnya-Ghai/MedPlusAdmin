package com.example.medplusadmin.di

import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.domain.usecases.catalog.GetAllMedicinesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAllMedicinesUseCase(
        catalogRepository: CatalogRepository
    ): GetAllMedicinesUseCase {
        return GetAllMedicinesUseCase(catalogRepository)
    }

}
