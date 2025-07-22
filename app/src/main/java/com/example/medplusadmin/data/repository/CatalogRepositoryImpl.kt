package com.example.medplusadmin.data.repository

import android.util.Log
import com.example.medplusadmin.data.mappers.toCategory
import com.example.medplusadmin.data.remote.firebaseServices.CatalogService
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.utils.Resource
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val catalogService: CatalogService,
    private val supabaseClient: SupabaseClient // till logic not implemented in mvvm
): CatalogRepository {
    /******************************************************
     *  Categories Section..
     ******************************************************/
//    ---------------------------get all categories--------------------
    override fun getAllCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            catalogService.getCategoriesFlow().map { list -> list.map { it.toCategory() } }
                .collect { modelList ->
                    emit(Resource.Success(modelList))
                }
        } catch (e: Exception) {
            Log.e("GetAllCategories", "Error: ${e.message}", e)
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
//    ---------------------------add , update categories--------------------
    override suspend fun upsertCategory(category: Category): Resource<Boolean> {
    val result = catalogService.upsertCategory(category)
    return if (result) {
        Resource.Success(result)
    } else {
        Resource.Error("Something went wrong")
    }
}
//    ---------------------------delete categories--------------------
    override suspend fun deleteCategory(id: String): Resource<Boolean> {
    val result = catalogService.deleteCategory(id)
    return if (result) {
        Resource.Success(result)
    } else {
        Resource.Error("Something went wrong")
    }
}
    /******************************************************
     *  Medicines Section..
     ******************************************************/
    override suspend fun addMedicine(medicine: Medicine): Result<Unit> {
        TODO("Not yet implemented")
    }
//    ---------------------------get all categories--------------------
    override suspend fun getAllMedicines(): List<Medicine> {
        TODO("Not yet implemented")
    }
//    ---------------------------get all categories--------------------
}