package com.example.medplusadmin.domain.repository

import android.net.Uri
import com.example.medplusadmin.utils.Resource

interface SupabaseRepository {
    suspend fun uploadImageToSupabase(uri: Uri): Resource<String> // returns URL
    suspend fun deleteImageFromSupabase(url: String): Resource<Unit>
}
