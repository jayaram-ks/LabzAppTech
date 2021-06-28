package com.labzapp.technician.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.LabDetailsResponse
import com.labzapp.technician.model.Profile
import com.labzapp.technician.repositories.LabDetailsRepository
import com.labzapp.technician.repositories.ProfileRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository  = ProfileRepository(application)

    val profiledet : LiveData<Profile>

    init{
        this.profiledet = repository.profiledet
    }

    fun myDetails(){
        repository.myDetails()
    }
}