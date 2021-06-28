package com.labzapp.technician.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.BookingsResponse
import com.labzapp.technician.repositories.BookingsRepository

class MainViewModel(application: Application) : AndroidViewModel(application){
    private val brepository  = BookingsRepository(application)

    val bookingsresp : LiveData<BookingsResponse>
    init{
        this.bookingsresp = brepository.bookingsresp
    }

    fun getBookings(bookStsFlg:String,reqtyp:String){
        brepository.getBookings(bookStsFlg,reqtyp)
    }
}