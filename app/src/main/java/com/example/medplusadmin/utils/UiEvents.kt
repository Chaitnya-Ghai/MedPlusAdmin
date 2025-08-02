package com.example.medplusadmin.utils

sealed class CatalogUIEvent {
    object OpenCategoryImagePicker : CatalogUIEvent()
    object OpenMedicineImagePicker : CatalogUIEvent()
}
