package com.example.medplusadmin.data.repository

import android.net.Uri
import android.util.Log
import com.example.medplusadmin.data.remote.supabase.SupabaseServices
import com.example.medplusadmin.domain.repository.SupabaseRepository
import com.example.medplusadmin.utils.ImageUploadState
import com.example.medplusadmin.utils.Resource
import com.example.medplusadmin.utils.UploadType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SupabaseRepositoryImpl @Inject constructor (
    private val supabaseServices: SupabaseServices
): SupabaseRepository {

    override fun uploadImageToSupabase(
        byteArray: ByteArray,
        type: UploadType,
        position: Int
    ): Flow<ImageUploadState> {
        val fileName = "${type.name.lowercase()}/${System.currentTimeMillis()}.jpg"

        return supabaseServices.uploadImageToSupabase(fileName, byteArray)
            .map { status ->
                when (status) {
                    is UploadStatus.Progress -> ImageUploadState.Loading
                    is UploadStatus.Success -> {
                        Log.e("supabase", "repo Success: ", )
                        val publicUrl = supabaseServices.getPublicUrl(fileName)
                        Log.e("supabase", "repo url: $publicUrl ", )
                        ImageUploadState.Success(publicUrl, position)
                    }
                }
            }
            .catch { e ->
                emit(ImageUploadState.Error(e.message ?: "Unknown error"))
            }
    }

}