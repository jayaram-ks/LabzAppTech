package com.labzapp.technician.ui.labs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.labzapp.technician.repositories.LocationUpdateRepository

class LocationUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocationUpdateRepository(application)

    fun updateLoc(labid : String,lablat : String,lablong : String) {
        repository.updateLoc(labid,lablat,lablong)
    }
}