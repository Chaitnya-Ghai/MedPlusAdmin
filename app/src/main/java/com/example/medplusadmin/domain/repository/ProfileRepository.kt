package com.example.medplusadmin.domain.repository

import com.example.medplusadmin.domain.models.Pharmacist
import com.example.medplusadmin.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getAllValidShopkeepers(): Flow<Resource<List<Pharmacist>>>
    suspend fun getNotYetValidShops(userId: String): Flow<Resource<List<Pharmacist>>>
}
