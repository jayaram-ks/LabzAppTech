package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.LabsResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LabsFragmentRepository(val application: Application) {


    val labsresp = MutableLiveData<LabsResponse>()

    fun allocLabs() {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.labsAllocated(authTokn,apiTokn)
        requestCall.enqueue(object : Callback<LabsResponse> {
            override fun onResponse(call: Call<LabsResponse>, resp: Response<LabsResponse>) {
                resp.body().let {
                    if (it != null) {
                        labsresp.value  = it
                    }
                }
            }
            override fun onFailure(call: Call<LabsResponse>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}