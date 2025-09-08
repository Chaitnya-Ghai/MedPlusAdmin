package com.example.medplusadmin.data.remote.supabase

import com.example.medplusadmin.bucketId
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupabaseServices @Inject constructor(
    private val supabaseClient: SupabaseClient,
) {

    fun uploadImageToSupabase(
        filePath: String,
        byteArray: ByteArray,
    ): Flow<UploadStatus> {
        val bucket = supabaseClient.storage.from(bucketId)
        return bucket.uploadAsFlow(filePath, byteArray)
    }

    fun getPublicUrl(filePath: String): String {
        return supabaseClient.storage.from(bucketId).publicUrl(filePath)
    }

}