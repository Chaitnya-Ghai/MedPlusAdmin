package com.example.medplusadmin.utils

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medplusadmin.presentation.screens.activities.MainActivity
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


inline fun Fragment.collectFlowSafely(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            block()
        }
    }
}

fun convertMedicineObject(snapshot: QueryDocumentSnapshot): Medicine {
    val model = snapshot.toObject(Medicine::class.java)
    model.medId = snapshot.id ?: ""
    return model
}

fun uriToByteArray(mainActivity: MainActivity?, uri: Uri): ByteArray {
    val inputStream = mainActivity?.contentResolver?.openInputStream(uri)
    return inputStream?.readBytes() ?: ByteArray(0)
}