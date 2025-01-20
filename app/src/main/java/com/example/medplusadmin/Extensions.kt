package com.example.medplusadmin

import com.example.medplusadmin.dataClasses.CategoryModel
import com.example.medplusadmin.dataClasses.MedicineModel
import com.google.firebase.firestore.QueryDocumentSnapshot

 fun convertCategoryObject(snapshot: QueryDocumentSnapshot): CategoryModel {
    val model = snapshot.toObject(CategoryModel::class.java)
    model.id = snapshot.id ?: ""
    return model
}

fun convertMedicineObject(snapshot: QueryDocumentSnapshot): MedicineModel {
    val model = snapshot.toObject(MedicineModel::class.java)
    model.id = snapshot.id ?: ""
    return model
}