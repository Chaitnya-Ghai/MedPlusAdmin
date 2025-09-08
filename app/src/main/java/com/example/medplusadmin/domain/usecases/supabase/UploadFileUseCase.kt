package com.example.medplusadmin.domain.usecases.supabase

import android.util.Log
import com.example.medplusadmin.domain.repository.SupabaseRepository
import com.example.medplusadmin.utils.ImageUploadState
import com.example.medplusadmin.utils.UploadType
import kotlinx.coroutines.flow.Flow

class UploadImageUseCase(private val supabaseRepository: SupabaseRepository) {
    operator fun invoke(
        type: UploadType,
        byteArray: ByteArray,
        position: Int
    ): Flow<ImageUploadState> {
        Log.e("supabase", "invoke ", )
        return supabaseRepository.uploadImageToSupabase( byteArray = byteArray, type = type, position = position)
    }
}