package com.example.medplusadmin.di

import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.domain.usecases.catalog.DeleteCategoryUseCase
import com.example.medplusadmin.domain.usecases.catalog.DeleteMedicineUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetAllCategoriesUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetAllMedicinesUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetMedicinesByCategoryUseCase
import com.example.medplusadmin.domain.usecases.catalog.GetaMedicineByIdUseCase
import com.example.medplusadmin.domain.usecases.catalog.UpsertCategoriesUseCase
import com.example.medplusadmin.domain.usecases.catalog.UpsertMedicinesUseCse
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
    fun provideGetAllCategoriesUseCase(
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
    @ViewModelScoped
    fun provideUpsertCategoryUseCase(
        catalogRepository: CatalogRepository
    ) : UpsertCategoriesUseCase{
        return UpsertCategoriesUseCase(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMedicinesUseCase(
        catalogRepository: CatalogRepository
    ) : GetAllMedicinesUseCase{
        return GetAllMedicinesUseCase(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteMedicinesUseCase(
        catalogRepository: CatalogRepository
    ) : DeleteMedicineUseCase {
        return DeleteMedicineUseCase(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpsertMedicinesUseCase(
        catalogRepository: CatalogRepository
    ) : UpsertMedicinesUseCse{
        return UpsertMedicinesUseCse(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMedicinesByCategoryUseCase(
        catalogRepository: CatalogRepository
    ) : GetMedicinesByCategoryUseCase {
        return GetMedicinesByCategoryUseCase(catalogRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetaMedicineByIdUseCase(
        catalogRepository: CatalogRepository
    ) : GetaMedicineByIdUseCase {
        return GetaMedicineByIdUseCase(catalogRepository)
    }


}
