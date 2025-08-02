package com.example.medplusadmin.data.repository

import android.net.Uri
import com.example.medplusadmin.domain.repository.SupabaseRepository
import com.example.medplusadmin.utils.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

class SupabaseRepositoryImpl @Inject constructor (
    private val supabaseClient: SupabaseClient
): SupabaseRepository {
//    no idea what is happening
    private val bucket = supabaseClient.storage.from("MedPlus Admin")

    override suspend fun uploadImageToSupabase(uri: Uri): Resource<String> {
        return Resource.Success(uri.toString())
    }

    override suspend fun deleteImageFromSupabase(url: String): Resource<Unit> {
        return Resource.Success(Unit)
    }

}