package com.example.medplusadmin.data.repository

import android.util.Log
import com.example.medplusadmin.data.mappers.toCategory
import com.example.medplusadmin.data.mappers.toDto
import com.example.medplusadmin.data.mappers.toMedicine
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
    //    ---------------------------get all medicines--------------------
    override fun getAllMedicines(): Flow<Resource<List<Medicine>>> = flow{
        emit(Resource.Loading())
        try {
            catalogService.getMedicinesFlow().map { list-> list.map { it.toMedicine()  } }
                .collect { modelList ->
                    emit(Resource.Success(modelList))
                }
        } catch (e: Exception) {
            Log.e("GetAllMedicines", "Error: ${e.message}", e)
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
//    ----------------------------add , update medicines --------------------
    override suspend fun upsertMedicine(medicine: Medicine): Resource<Boolean> {
        var upsertMedicineDto = medicine.toDto()
        val result = catalogService.upsertMedicines(upsertMedicineDto)
        return if (result) {
            Resource.Success(result)
        } else {
            Resource.Error("Something went wrong")
        }
    }
//    ---------------------------delete medicines--------------------
    override suspend fun deleteMedicine(id: String): Resource<Boolean> {
        val result = catalogService.deleteMedicine(id)
        return if (result) {
            Resource.Success(result)
        } else {
            Resource.Error("Something went wrong")
        }
    }

    /**
     * ---------------------- RELATIONAL QUERIES ----------------------
     */

    override suspend fun getMedicineBy(
        medId: String?,
        catId: String?
    ): Resource<List<Medicine>> {
        val result = catalogService.getMedicinesBy(medId = medId, catId = catId)
        return if (result.isNotEmpty()) {
            Resource.Success(result)
        } else {
            Resource.Error("Something went wrong")
        }
    }

}