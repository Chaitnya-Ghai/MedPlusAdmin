package com.example.medplusadmin.di

import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.domain.usecases.catalog.DeleteCategoryUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetAllCategoriesUseCase
import com.example.medplusadmin.domain.usecases.catalog.UpsertCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetAllMedicinesUseCase(
        catalogRepository: CatalogRepository
    ): GetAllCategoriesUseCase {
        return GetAllCategoriesUseCase(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteCategoryUseCase(
        catalogRepository: CatalogRepository
    ) : DeleteCategoryUseCase{
        return DeleteCategoryUseCase(catalogRepository)
    }

    @Provides
    fun provideUpsertCategoryUseCase(
        catalogRepository: CatalogRepository
    ) : UpsertCategoriesUseCase{
        return UpsertCategoriesUseCase(catalogRepository)
    }

}
