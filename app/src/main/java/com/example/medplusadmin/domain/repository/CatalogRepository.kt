package com.example.medplusadmin.domain.repository

import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine

interface CatalogRepository {
    suspend fun addCategory(category: Category): Result<Unit>
    suspend fun addMedicine(medicine: Medicine): Result<Unit>
    suspend fun getAllCategories(): List<Category>
    suspend fun getAllMedicines(): List<Medicine>
    suspend fun deleteCategory(id: String): Result<Unit>
    // More catalog related logic
}
