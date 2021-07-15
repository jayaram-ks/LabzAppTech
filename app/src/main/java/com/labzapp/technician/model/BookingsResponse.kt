package com.labzapp.technician.model

data class BookingsResponse(val code : Int,
                            val status : String,
                            val message : String,
                            val bookings : List<Bookings>)
data class Bookings (

    val id : Int,
    val patient_name : String,
    val mobile : String,
    val booking_total : String,
    val booking_status : String,
    val total_to_pay : String,
    val booking_date : String,
    val booking_device : String,
    val lab_name : String,
    val report_file : String,
    val pref_date : String,
    val test_or_pack : String,
    val package_id : String?,

)