package com.labzapp.technician.model

data class LabsResponse(val code : Int, val status : String, val message : String, val logopath : String, val labs : List<LabData>)

data class LabData (val lab_id : Int, val lab_name : String, val lab_phone : String, val lab_address : String, val lab_pincode : String,
                    val district_name : String, val service_charge : String, val lab_logo: String
)
