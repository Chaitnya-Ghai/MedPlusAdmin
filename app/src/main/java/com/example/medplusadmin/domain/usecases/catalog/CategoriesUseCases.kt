package com.example.medplusadmin.domain.usecases.catalog

import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.utils.Resource
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(): Flow<Resource<List<Category>>> {
        return catalogRepository.getAllCategories()
    }
}

class DeleteCategoryUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return catalogRepository.deleteCategory(id)
    }
}

class UpsertCategoriesUseCase(
    private val catalogRepository: CatalogRepository
){
    suspend operator fun invoke(category: Category): Resource<Boolean> {
        return catalogRepository.upsertCategory(category)
    }
}

