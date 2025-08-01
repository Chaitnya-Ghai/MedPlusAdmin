package com.example.medplusadmin.data.remote.firebaseServices

import android.util.Log
import com.example.medplusadmin.data.remote.dto.CategoryDto
import com.example.medplusadmin.data.remote.dto.MedicineDto
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
import javax.inject.Singleton

@Singleton
class CatalogService @Inject constructor(
    private val db: FirebaseFirestore,
    private val supabaseClient: SupabaseClient
) {

    /**
     * ---------------------- CATEGORY RELATED OPERATIONS ----------------------
     */

    /**
     * Get real-time updates for all categories using [callbackFlow].
     */
    fun getCategoriesFlow(): Flow<List<CategoryDto>> = callbackFlow {
        val listener = db.collection(CATEGORY)
            .addSnapshotListener { snapshot, _ ->
                val categories = snapshot?.toObjects(CategoryDto::class.java) ?: emptyList()
                trySend(categories) // Emit data to Flow
            }

        awaitClose { listener.remove() } // Remove listener on cancellation
    }

    /**
     * Insert or update a category in Firestore.
     * If `id` is empty → create new category.
     * Else → update the existing category.
     */
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

    /** Delete category by its ID.*/
    suspend fun deleteCategory(id: String): Boolean {
        return try {
            db.collection(CATEGORY).document(id).delete().await()
            true
        } catch (e: Exception){
            Log.e("deleteCategory", "FirebaseService error = ${e.message}")
            false
        }
    }


    /**
     * ---------------------- MEDICINE RELATED OPERATIONS ----------------------
     */

    /**
     * Get real-time updates for all medicines using [callbackFlow].
     */
    fun getMedicinesFlow(): Flow<List<MedicineDto>> = callbackFlow {
        val listener = db.collection(MEDICINE)
            .addSnapshotListener { snapshot, _ ->
                val medicines = snapshot?.toObjects(MedicineDto::class.java) ?: emptyList()
                trySend(medicines) // Emit data
            }

        awaitClose { listener.remove() }
    }

    /**
     * Insert or update a medicine in Firestore.
     * If `id` is empty → create new medicine.
     * Else → update the existing medicine.
     */
    suspend fun upsertMedicines(medicine: MedicineDto): Boolean {
        return try {
            if (medicine.id.isNullOrEmpty()) {
                // Check if medicine with same name already exists
                val existing = db.collection(MEDICINE)
                    .whereEqualTo("medicineName", medicine.medicineName)
                    .get()
                    .await()

                if (!existing.isEmpty) return false

                // Create new document
                val docRef = db.collection(MEDICINE).document()
                val finalMedicine = medicine.copy(id = docRef.id)
                docRef.set(finalMedicine).await()
            } else {
                // Update existing medicine
                db.collection(MEDICINE)
                    .document(medicine.id!!)
                    .set(medicine)
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e("upsertMedicines", "FirebaseService error = ${e.message}")
            false
        }
    }

    /** Delete medicine by its ID.*/
    suspend fun deleteMedicine(id: String): Boolean {
        return try {
            db.collection(MEDICINE).document(id).delete().await()
            true
        } catch (e: Exception){
            Log.e("deleteMedicine", "FirebaseService error = ${e.message}")
            false
        }
    }

    /**
     * ---------------------- RELATIONAL QUERIES ----------------------
     */
    suspend fun getMedicinesBy(
        medId: String? = null,
        catId: String? = null
    ): List<Medicine> {
        return try {
            val query = when {
                medId != null -> db.collection(MEDICINE)
                    .whereEqualTo("medId", medId)

                catId != null -> db.collection(MEDICINE)
                    .whereArrayContains("belongingCategory", catId)

                else -> return emptyList()
            }

            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { it.toObject(Medicine::class.java) }

        } catch (e: Exception) {
            Log.e("FirebaseService", "getMedicinesBy error = ${e.message}")
            emptyList()
        }
    }
}
