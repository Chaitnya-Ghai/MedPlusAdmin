package com.example.medplusadmin.domain.models

data class Pharmacist(
    val authId: String = "",
    val shopName: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val inventory: List<InventoryItem> = emptyList(),
    val medicineId: List<String> = emptyList(),//for faster query
    val licenseImageUrl: String = "",
    val shopImageUrl: String = "",
    var isVerified: Int ?=0,  // 0 for not Register , 1 for not verified, 2 for verified , 3 for rejected
)