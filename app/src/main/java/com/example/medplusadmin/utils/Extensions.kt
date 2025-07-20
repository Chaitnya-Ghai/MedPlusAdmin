package com.example.medplusadmin.utils

import android.net.Uri
import com.example.medplusadmin.Presentation.screens.activities.MainActivity
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.google.firebase.firestore.QueryDocumentSnapshot

 fun convertCategoryObject(snapshot: QueryDocumentSnapshot): Category {
    val model = snapshot.toObject(Category::class.java)
    model.id = snapshot.id ?: ""
    return model
}

fun convertMedicineObject(snapshot: QueryDocumentSnapshot): Medicine {
    val model = snapshot.toObject(Medicine::class.java)
    model.medId = snapshot.id ?: ""
    return model
}

fun uriToByteArray(mainActivity: MainActivity, uri: Uri): ByteArray {
    val inputStream = mainActivity.contentResolver.openInputStream(uri)
    return inputStream?.readBytes() ?: ByteArray(0)
}