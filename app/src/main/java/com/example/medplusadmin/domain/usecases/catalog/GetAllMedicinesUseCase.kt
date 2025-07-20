package com.example.medplusadmin.domain.usecases.catalog

import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.domain.repository.CatalogRepository

class GetAllMedicinesUseCase(
    private val catalogRepository: CatalogRepository
) {
    suspend operator fun invoke(): List<Medicine> {
        return catalogRepository.getAllMedicines()
    }
}
