package com.example.medplusadmin.data.remote.dto

import com.example.medplusadmin.domain.models.ProductDetail

data class MedicineDto(
    var id :String?=null,
    var medicineName :String?=null,
    var description :String?=null,
    var medicineImg :String?=null,
    var belongingCategory: MutableList<String>?=null,
    var dosageForm:String?=null,
    var unit :String?=null,
    var ingredients :String?=null,
    var howToUse :String?=null,
    var precautions :String?=null,
    var storageInfo :String?=null,
    var sideEffects :String?=null,
    var productDetail: ProductDetail?=null
)
