package com.labzapp.technician.model

data class RegisterResponse(val code : Int, val status : String, val message : Any?, val api_token: String?, val auth_key: String?)

data class LocUpdateResponse(val code : Int, val status : String, val message : Any?)

data class BookStsUpdateResp(val code : Int, val status : String, val message : Any?)

data class PrintStsUpdateResp(val code : Int, val status : String, val message : Any?)

data class UploadReportResponse(val code : Int, val status : String, val message : Any?)



