package com.labzapp.technician.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.RegisterResponse
import com.labzapp.technician.repositories.AuthActivityRepository

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthActivityRepository(application)
    val response : LiveData<RegisterResponse>
    init {
        response = repository.response
    }

    fun loginTechnician(mobil:String,authcode:String){
        repository.loginTechnician(mobil,authcode)
    }
}