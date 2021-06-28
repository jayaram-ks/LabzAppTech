package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.MyProfileResponse
import com.labzapp.technician.model.Profile
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository(val application: Application) {

    val profiledet = MutableLiveData<Profile>()

    fun myDetails() {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.myProfDetails(authTokn,apiTokn)
        requestCall.enqueue(object : Callback<MyProfileResponse> {
            override fun onResponse(call: Call<MyProfileResponse>, resp: Response<MyProfileResponse>) {
                resp.body().let {
                    if (it != null) {
                        if(it.code == 200) {
                            profiledet.value = it.data
                        }else{
                            toastz(application,it.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}