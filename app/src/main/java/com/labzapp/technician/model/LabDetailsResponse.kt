package com.labzapp.technician.model

data class LabDetailsResponse(
    val code : Int,
    val status : String,
    val message : String,
    val data : LabDetail
)

data class LabDetail (
    val lab_id : Int,
    val lab_name : String,
    val lab_phone : String,
    val lab_address : String,
    val lab_pincode : String,
    val district_name : String,
    val lab_latitude : Double,
    val lab_longitude : Double,
    val service_charge : String,
    val lab_logo: String
)