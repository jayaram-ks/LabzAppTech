package com.labzapp.technician.ui.bookings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.labzapp.technician.model.BookingDetailsResp
import com.labzapp.technician.repositories.BookingDetailsRepository

class BookingDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookingDetailsRepository(application)
    val bookdetresp : LiveData<BookingDetailsResp>
    init{
        this.bookdetresp = repository.bookdetresp
    }

    fun bookDetails(bookID:String){
        repository.bookDetails(bookID)
    }
}