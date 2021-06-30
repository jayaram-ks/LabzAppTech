package com.labzapp.technician.activities

import android.app.Application
import android.icu.text.CaseMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.labzapp.technician.model.BookingsResponse
import com.labzapp.technician.repositories.BookingsRepository

class MainViewModel(application: Application) : AndroidViewModel(application){
    private val brepository  = BookingsRepository(application)

    val bookingsresp : LiveData<BookingsResponse>
    var pagetitle = MutableLiveData<String>()
    init{
        this.bookingsresp = brepository.bookingsresp
    }

    fun setTitl(titl: String){
        pagetitle.value = titl
    }

    fun getBookings(bookStsFlg:String,reqtyp:String){
        brepository.getBookings(bookStsFlg,reqtyp)
    }
}