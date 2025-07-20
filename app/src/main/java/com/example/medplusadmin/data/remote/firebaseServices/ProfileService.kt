package com.example.medplusadmin.data.remote.firebaseServices

import com.example.medplusadmin.utils.Constants.Companion.PHARMACIST
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileService @Inject constructor(
    private val db: FirebaseFirestore,
    private val supabaseClient: SupabaseClient
) {
//    suspend fun getAllShopkeepers(): List<Shopkeeper>{
//        val db= db.collection(PHARMACIST).get().await()
//        return db.documents.mapNotNull { it.toObject(Shopkeeper::class.java) }
//    }
}