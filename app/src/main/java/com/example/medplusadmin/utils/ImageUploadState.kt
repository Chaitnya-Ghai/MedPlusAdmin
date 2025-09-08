package com.example.medplusadmin.utils

sealed class ImageUploadState {
    object Idle : ImageUploadState()
    object Loading : ImageUploadState()
    data class Success(val imageUrl: String, val position: Int) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}
