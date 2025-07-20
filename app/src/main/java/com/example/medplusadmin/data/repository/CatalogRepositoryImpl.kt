package com.example.medplusadmin.data.repository

import com.example.medplusadmin.data.remote.firebaseServices.CatalogService
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.repository.CatalogRepository
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val catalogService: CatalogService
): CatalogRepository {
    override suspend fun addCategory(category: Category): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addMedicine(medicine: Medicine): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCategories(): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMedicines(): List<Medicine> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}