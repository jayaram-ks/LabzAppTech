package com.labzapp.technician.services.network

import com.labzapp.technician.model.*
import com.labzapp.technician.utils.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST(LOGIN_TECHNICIAN)
    fun loginTechn(
        @Field("technician_phone") mobile: String,
        @Field("technician_auth_code") authcode: String,
    ): Call<RegisterResponse>

    @GET(LABS_ALLOCATED)
    fun labsAllocated(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
    ): Call<LabsResponse>

    @GET(LAB_DETAILS)
    fun labDetails(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
        @Query("lab_id") labid: String?,
    ):Call<LabDetailsResponse>

    @GET(MY_BOOKINGS)
    fun myBookings(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
        @Query("booking_status_flag") bookStatusFlag: String?,
    ):Call<BookingsResponse>

    @GET(MY_BOOKINGS_PRINT)
    fun myBookingsPrint(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
        @Query("bill_status_flag") billStatusFlag: String?,
    ):Call<BookingsResponse>

    @GET(MY_PROFILE)
    fun myProfDetails(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
    ):Call<MyProfileResponse>

    @FormUrlEncoded
    @POST(UPDATE_LAB_LOCATION)
    fun updateLabLoc(
        @Header("Authorization") authtoken: String?,
        @Field("api_token") apitoken: String?,
        @Field("lab_id") labid: String?,
        @Field("lab_latitude") lablat: String?,
        @Field("lab_longitude") lablong: String?,
    ):Call<LocUpdateResponse>

    @GET(BOOKING_DETAILS)
    fun bookDetails(
        @Header("Authorization") authtoken: String?,
        @Query("api_token") apitoken: String?,
        @Query("booking_id") bookid: String?,
    ):Call<BookingDetailsResp>

    @FormUrlEncoded
    @POST(CHANGE_BOOK_STS)
    fun changeBookingSts(
        @Header("Authorization") authtoken: String?,
        @Field("api_token") apitoken: String?,
        @Field("booking_id") bookid: String?,
        @Field("booking_status_flag") booksts: String?,
    ):Call<BookStsUpdateResp>

    @FormUrlEncoded
    @POST(CHANGE_PRINTGIVEN_STS)
    fun changePrintgivenSts(
        @Header("Authorization") authtoken: String?,
        @Field("api_token") apitoken: String?,
        @Field("booking_id") bookid: String?,
        @Field("bill_delivery_flag") printsts: String?,
    ):Call<PrintStsUpdateResp>

    @Multipart
    @POST(TEST_REPORT_UPLOAD)
    fun uploadReport(
        @Header("Authorization") authtoken : String?,
        @Part("api_token") apitoken : RequestBody,
        @Part("booking_id") bookingid : RequestBody,
        @Part prescrImage : MultipartBody.Part,
    ):Call<UploadReportResponse>

}
