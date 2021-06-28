package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.RegisterResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthActivityRepository(val application: Application) {

    val response = MutableLiveData<RegisterResponse>()

    fun loginTechnician(mobile: String, authcode: String) {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.loginTechn(mobile,authcode)
        requestCall.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, resp: Response<RegisterResponse>) {
                response.value = resp.body()
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                toastz(application,"Error getting data")
            }
        })
    }
}