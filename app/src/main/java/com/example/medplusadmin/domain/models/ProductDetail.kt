package com.example.medplusadmin.domain.models

data class ProductDetail(
    var expiryDate: String?=null,
    var brandName: String?=null,
    var originalPrice: String?=null,
)

data class InventoryItem(
    var medicineId: String = "",
    var medicineName: String = "",
    var shopMedicinePrice: String = "",
)