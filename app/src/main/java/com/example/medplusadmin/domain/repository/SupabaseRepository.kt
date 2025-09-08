package com.example.medplusadmin.domain.repository

import com.example.medplusadmin.utils.ImageUploadState
import com.example.medplusadmin.utils.UploadType
import kotlinx.coroutines.flow.Flow

interface SupabaseRepository {
    fun uploadImageToSupabase(byteArray: ByteArray , type: UploadType,position: Int): Flow<ImageUploadState>
}
