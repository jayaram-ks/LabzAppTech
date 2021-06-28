package com.labzapp.technician.ui.labs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.LabsResponse
import com.labzapp.technician.repositories.LabsFragmentRepository

class LabsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository  = LabsFragmentRepository(application)

    val labsresp : LiveData<LabsResponse>
    init{

        this.labsresp = repository.labsresp
    }

    fun allocLabs(){
        repository.allocLabs()
    }
}