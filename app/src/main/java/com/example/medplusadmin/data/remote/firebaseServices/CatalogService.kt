package com.example.medplusadmin.data.remote.firebaseServices

import android.util.Log
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.utils.Constants.Companion.CATEGORY
import com.example.medplusadmin.utils.Constants.Companion.MEDICINE
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val db : FirebaseFirestore,
    private val supabaseClient: SupabaseClient
){
    suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = db.collection(CATEGORY).get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Category::class.java)
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Deserialization failed: ${doc.data}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "getCategories error = ${e.message}")
            emptyList()
        }
    }

    suspend fun getMedicines(): List<Medicine>{
        return try {
            val snapshot = db.collection(MEDICINE).get().await()
            snapshot.documents.mapNotNull { document -> document.toObject(Medicine::class.java) }
        }catch (e: Exception){
            Log.e("FirebaseService", "getMedicines error = ${e.message}")
            emptyList()
        }
    }

    suspend fun getMedicinesBy(medicineId: String? = null, categoryId: String? = null, name: String? = null ): List<Medicine> {
        return try {
            val query = when {
                medicineId != null -> db.collection(MEDICINE)
                    .whereEqualTo("id", medicineId)

                categoryId != null -> db.collection(MEDICINE)
                    .whereArrayContains("belongingCategory", categoryId)

                name != null -> db.collection(MEDICINE)
                    .whereEqualTo("medicineName", name)

                else -> db.collection(MEDICINE)
            }
            val snapshot = query.get().await()
            snapshot.documents.mapNotNull {
                it.toObject(Medicine::class.java)
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "getMedicinesBy error = ${e.message}")
            emptyList()
        }
    }

}