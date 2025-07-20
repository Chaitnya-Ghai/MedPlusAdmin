package com.example.medplusadmin.Presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.medplusadmin.domain.usecases.catalog.GetAllMedicinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllMedicinesUseCase: GetAllMedicinesUseCase
) : ViewModel() {

    suspend fun uploadImageToSupabase() {

    }
}
