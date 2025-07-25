package com.example.medplusadmin.data.remote.firebaseServices

import android.util.Log
import com.example.medplusadmin.data.remote.dto.CategoryDto
import com.example.medplusadmin.domain.models.Category
import com.example.medplusadmin.domain.models.Medicine
import com.example.medplusadmin.utils.Constants.Companion.CATEGORY
import com.example.medplusadmin.utils.Constants.Companion.MEDICINE
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val db : FirebaseFirestore,
    private val supabaseClient: SupabaseClient
){
    fun getCategoriesFlow(): Flow<List<CategoryDto>> = callbackFlow {
        val listener = db.collection(CATEGORY)
            .addSnapshotListener { snapshot, _ ->
                val categories = snapshot?.toObjects(CategoryDto::class.java) ?: emptyList()
                trySend(categories)
            }

        awaitClose { listener.remove() }
    }

    suspend fun upsertCategory(category: Category): Boolean {
        return try {
            if (category.id.isEmpty()) {
                val existing = db.collection(CATEGORY)
                    .whereEqualTo("categoryName", category.categoryName)
                    .get()
                    .await()
                if (!existing.isEmpty) return false

                val docRef = db.collection(CATEGORY).document()
                val finalCategory = category.copy(id = docRef.id)
                docRef.set(finalCategory).await()
            } else {
                db.collection(CATEGORY).document(category.id).set(category).await()
            }
            true
        } catch (e: Exception) {
            Log.e("upsertCategory", "FirebaseService error = ${e.message}")
            false
        }
    }

    suspend fun deleteCategory(id: String): Boolean  {
        try {
            db.collection(CATEGORY).document(id).delete().await()
            return true
        } catch (e: Exception){
            Log.e("deleteCategory", "FirebaseService error = ${e.message}")
            return false
        }
    }

    suspend fun getMedicines(): List<Medicine>{
        return try {
            val snapshot = db.collection(MEDICINE).get().await()
            snapshot.documents.mapNotNull { document -> document.toObject(Medicine::class.java) }
        }catch (e: Exception){
            Log.e("getMedicines", "FirebaseService error = ${e.message}")
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