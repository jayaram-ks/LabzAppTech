package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.LabDetailsResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LabDetailsRepository(val application: Application) {


    val labsdetresp = MutableLiveData<LabDetailsResponse>()

    fun labDetails(labID:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.labDetails(authTokn,apiTokn,labID)
        requestCall.enqueue(object : Callback<LabDetailsResponse> {
            override fun onResponse(call: Call<LabDetailsResponse>, resp: Response<LabDetailsResponse>) {
                resp.body().let {
                    if (it != null) {
                        labsdetresp.value  = it
                    }
                }
            }
            override fun onFailure(call: Call<LabDetailsResponse>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}