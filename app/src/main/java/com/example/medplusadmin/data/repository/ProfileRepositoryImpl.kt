package com.example.medplusadmin.data.repository

import com.example.medplusadmin.data.remote.firebaseServices.ProfileService
import com.example.medplusadmin.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService
): ProfileRepository {

}