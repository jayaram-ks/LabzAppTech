package com.labzapp.technician.model

data class BookingDetailsforPathResp(val code : Int,
                                     val status : String,
                                     val message : String,
                                     val booking : BookingPath)

data class BookingPath (

    val id : Int,
    val patient_name : String,
    val patient_latitude : Double,
    val patient_longitude : Double,
    val lab_name : String,
    val lab_address : String,
    val lab_pincode : Int,
    val lab_latitude : Double,
    val lab_longitude : Double,
    val lab_district: String
)