package com.example.medplusadmin.domain.repository

import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
//  categories
    fun getAllCategories(): Flow<Resource<List<Category>>>
    suspend fun upsertCategory(category: Category): Resource<Boolean>
    suspend fun deleteCategory(id: String): Resource<Boolean>
//  medicines
    fun getAllMedicines():  Flow<Resource<List<Medicine>>>
    suspend fun upsertMedicine(medicine: Medicine): Resource<Boolean>
    suspend fun deleteMedicine(id: String): Resource<Boolean>
//  RELATIONAL QUERIES
    suspend fun getMedicineBy(medId: String? = null, catId: String? = null): Resource<List<Medicine>>
}
