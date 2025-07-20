package com.example.medplusadmin.data.repository

import android.net.Uri
import com.example.medplusadmin.domain.repository.SupabaseRepository
import com.example.medplusadmin.utils.Resource

class SupabaseRepositoryImpl: SupabaseRepository {
    override suspend fun uploadImageToSupabase(uri: Uri): Resource<String> {
        return Resource.Success(uri.toString())
    }

    override suspend fun deleteImageFromSupabase(url: String): Resource<Unit> {
        return Resource.Success(Unit)
    }

}