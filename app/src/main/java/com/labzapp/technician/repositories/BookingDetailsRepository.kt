package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.BookingDetailsResp
import com.labzapp.technician.model.LabDetailsResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDetailsRepository(val application: Application) {


    val bookdetresp = MutableLiveData<BookingDetailsResp>()

    fun bookDetails(bookID:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.bookDetails(authTokn,apiTokn,bookID)
        requestCall.enqueue(object : Callback<BookingDetailsResp> {
            override fun onResponse(call: Call<BookingDetailsResp>, resp: Response<BookingDetailsResp>) {
                resp.body().let {
                    if (it != null) {
                        bookdetresp.value  = it
                    }
                }
            }
            override fun onFailure(call: Call<BookingDetailsResp>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}