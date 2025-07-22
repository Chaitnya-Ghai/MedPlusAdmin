package com.example.medplusadmin.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.medplusadmin.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
):ViewModel() {
}