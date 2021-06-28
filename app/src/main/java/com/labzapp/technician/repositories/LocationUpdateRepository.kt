package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.LocUpdateResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationUpdateRepository(val application: Application) {

    val locresp = MutableLiveData<LocUpdateResponse>()

    fun updateLoc(labid : String,lablat : String,lablong : String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.updateLabLoc(authTokn,apiTokn,labid,lablat,lablong)
        requestCall.enqueue(object : Callback<LocUpdateResponse> {
            override fun onResponse(call: Call<LocUpdateResponse>, resp: Response<LocUpdateResponse>) {
                resp.body().let {
                    if (it != null) {
                        if(it.code == 200) {
                            toastz(application, it.message as String)
                        }else{
                            toastz(application, it.message as String)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<LocUpdateResponse>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}