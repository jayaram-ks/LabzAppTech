package com.labzapp.technician.utils

const val API_BASEURL = "http://labzapp.in/api/v1/technician/"
const val LOGIN_TECHNICIAN = "verifyauthcode"
const val LABS_ALLOCATED = "assignedlabs"
const val LAB_DETAILS = "singlelabdetail"
const val MY_BOOKINGS = "getbookingsbystatus"
const val MY_BOOKINGS_PRINT = "paperbillist"
const val MY_PROFILE = "myprofile"
const val UPDATE_LAB_LOCATION = "updatelablocation"
const val BOOKING_DETAILS = "bookingdetails"
const val CHANGE_BOOK_STS = "changebookingstatus"
const val CHANGE_PRINTGIVEN_STS = "changepaperbillstatus"
const val TEST_REPORT_UPLOAD = "uploadreport"
const val BOOKING_DETAILS_FOR_PATH = "bookingdetforpath"



val districtz = linkedMapOf(0 to "--Select District--", 1 to "Thiruvananthapuram", 2 to "Kollam", 3 to "Pathanamthitta", 4 to "Alappuzha", 5 to "Kottayam", 6 to "Idukki",
    7 to "Ernakulam", 8 to "Thrissur", 9 to "Palakkad", 10 to "Malappuram", 11 to "Kozhikode", 12 to "Wayanad", 13 to "Kannur", 14 to "Kasaragod")
val genderz = linkedMapOf(1 to "Male", 2 to "Female")

val bookingzsts = linkedMapOf(1 to "Technician not allocated",2 to "Technician allocated",3 to "Sample collected",4 to "Completed")

val printzsts = linkedMapOf(1 to "Bill/Print NOT given to customer",2 to "Bill/Print given to customer")

val printzneed = linkedMapOf(1 to "No",2 to "Yes")
