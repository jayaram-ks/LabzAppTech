package com.labzapp.technician.model

data class MyProfileResponse(val code : Int,
                             val status : String,
                             val message : String,
                             val data : Profile)
data class Profile (

    val technician_id : Int,
    val technician_name : String,
    val technician_mobile : String,
    val technician_address : String,
    val district_name : String
)