package com.example.medplusadmin.data.remote.firebaseServices

import com.example.medplusadmin.domain.models.Pharmacist
import com.example.medplusadmin.utils.Constants.Companion.PHARMACIST
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val db: FirebaseFirestore,
) {

    fun getNotYetValidShopkeepers(): Flow<List<Pharmacist>> = callbackFlow {
        val listener = db.collection(PHARMACIST).whereEqualTo("verified", 1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close flow if Firestore returns an error
                    return@addSnapshotListener
                }
                val shopkeepers = snapshot?.toObjects(Pharmacist::class.java) ?: emptyList()
                trySend(shopkeepers) // Emit data to Flow
            }
        awaitClose { listener.remove() }
    }

    fun getAllValidShopkeepers(): Flow<List<Pharmacist>> = callbackFlow {
        val listener = db.collection(PHARMACIST).whereEqualTo("verified", 2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close flow if Firestore returns an error
                    return@addSnapshotListener
                }
                val shopkeepers = snapshot?.toObjects(Pharmacist::class.java) ?: emptyList()
                trySend(shopkeepers) // Emit data to Flow
            }
        awaitClose { listener.remove() }
    }

}