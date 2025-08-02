package com.example.medplusadmin.domain.models

data class Medicine(
    var medId :String,
    var medicineName :String,
    var description :String,
    var medicineImg :String,
    var belongingCategory: MutableList<String>,
    var dosageForm:String,
    var unit :String,
    var ingredients :String,
    var howToUse :String,
    var precautions :String,
    var storageInfo :String,
    var sideEffects :String,
    var productDetail: ProductDetail
)
