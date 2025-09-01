package com.example.medplusadmin.domain.usecases.catalog

import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.repository.CatalogRepository
import com.example.medplusadmin.utils.Resource
import kotlinx.coroutines.flow.Flow

class GetAllMedicinesUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(): Flow<Resource<List<Medicine>>> {
        return catalogRepository.getAllMedicines()
    }
}

class DeleteMedicineUseCase(
    private val catalogRepository: CatalogRepository
){
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return catalogRepository.deleteMedicine(id)
    }
}

class UpsertMedicinesUseCse(
    private val catalogRepository: CatalogRepository
){
    suspend operator fun invoke(medicine: Medicine): Resource<Boolean> {
        return catalogRepository.upsertMedicine(medicine)
    }
}
//
class GetaMedicineByIdUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(medId: String): Flow<Resource<List<Medicine>>> = catalogRepository.getMedicineBy(medId = medId)
}

class GetMedicinesByCategoryUseCase(
    private val catalogRepository: CatalogRepository
) {
    operator fun invoke(catId: String): Flow<Resource<List<Medicine>>> = catalogRepository.getMedicineBy( catId = catId)
}

