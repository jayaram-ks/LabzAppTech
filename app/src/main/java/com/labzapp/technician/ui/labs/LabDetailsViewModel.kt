package com.labzapp.technician.ui.labs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.LabDetailsResponse
import com.labzapp.technician.repositories.LabDetailsRepository

class LabDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository  = LabDetailsRepository(application)

    val labsdetresp : LiveData<LabDetailsResponse>

    init{
        this.labsdetresp = repository.labsdetresp
    }

    fun labDetails(labId:String){
        repository.labDetails(labId)
    }
}