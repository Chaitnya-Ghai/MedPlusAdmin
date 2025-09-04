package com.example.medplusadmin.data.repository

import android.util.Log
import com.example.medplusadmin.data.remote.firebaseServices.ProfileService
import com.example.medplusadmin.domain.models.Pharmacist
import com.example.medplusadmin.domain.repository.ProfileRepository
import com.example.medplusadmin.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
) : ProfileRepository {

    override suspend fun getAllValidShopkeepers(): Flow<Resource<List<Pharmacist>>> = flow {
        emit(Resource.Loading())
        try {
            profileService.getAllValidShopkeepers().collectLatest {
                emit(Resource.Success(it))
            }
        } catch (e: Exception) {
            Log.e("ProfileRepositoryImpl", "${e.message}", e)
            emit(Resource.Error(e.message.toString()))
        }
    }

    override suspend fun getNotYetValidShops(userId: String): Flow<Resource<List<Pharmacist>>> =
        flow {
            emit(Resource.Loading())
            try {
                profileService.getNotYetValidShopkeepers().collectLatest {
                    emit(Resource.Success(it))
                }
            } catch (e: Exception) {
                Log.e("ProfileRepositoryImpl", "${e.message}", e)
                emit(Resource.Error(e.message.toString()))
            }
        }

}