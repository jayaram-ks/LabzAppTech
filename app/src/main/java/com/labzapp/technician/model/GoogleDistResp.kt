package com.labzapp.technician.model

data class GoogleDistResp(val destination_addresses : List<String>, val origin_addresses : List<String>, val rows : List<Rows>, val status : String)

data class Rows (val elements : List<Elements>)

data class Elements (val distance : Distance, val duration : Duration, val status : String)

data class Duration (val text : String, val value : Int)

data class Distance (val text : String, val value : Int)