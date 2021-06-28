package com.labzapp.technician.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.BookingsResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.toastz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingsRepository(val application: Application) {

    val bookingsresp = MutableLiveData<BookingsResponse>()
    fun getBookings(bookStsFlg:String,bookReqtype:String) {

        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(application).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(application).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)

        var requestCall = if(bookReqtype == "printyes"){
            apiService.myBookingsPrint(authTokn,apiTokn,bookStsFlg)
        }
        else{
            apiService.myBookings(authTokn,apiTokn,bookStsFlg)
        }

        requestCall.enqueue(object : Callback<BookingsResponse> {
            override fun onResponse(call: Call<BookingsResponse>, resp: Response<BookingsResponse>) {
                resp.body().let {
                    if (it != null) {
                        bookingsresp.value = it
                    }
                }
            }
            override fun onFailure(call: Call<BookingsResponse>, t: Throwable) {
                toastz(application,t.message.toString())
            }
        })
    }
}