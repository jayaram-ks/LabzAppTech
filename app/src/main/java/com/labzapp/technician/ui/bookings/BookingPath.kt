package com.labzapp.technician.ui.bookings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.labzapp.technician.R
import com.labzapp.technician.activities.MainActivity
import com.labzapp.technician.databinding.FragmentBookingPathBinding
import com.labzapp.technician.model.BookStsUpdateResp
import com.labzapp.technician.model.BookingDetailsforPathResp
import com.labzapp.technician.model.GoogleDistResp
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.ui.labs.LabDetailsFragment
import com.labzapp.technician.utils.MAP_WEB_API_KEY
import com.labzapp.technician.utils.districtz
import com.labzapp.technician.utils.maps.PermissionUtils
import com.labzapp.technician.utils.toastz
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class BookingPath : Fragment(),  OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback  {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBookingPathBinding? = null
    private val binding get() = _binding!!
    private var disvalmetre: Int = 0

    private lateinit var locationManager: LocationManager
    var gpsStatus = false
    private var permissionDenied = false
    private var lastKnownLocation: Location? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
   // val DEF_LOCATION = LatLng(9.9312, 76.2673)
    val ZOOM_LEVEL = 17f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        (activity as MainActivity?)?.setActionBarTitle("Confirm Sample Collection")
       // lastKnownLocation?.latitude  = DEF_LOCATION.latitude
       // lastKnownLocation?.longitude = DEF_LOCATION.longitude
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingPathBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_labdet) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        param1?.let { bookingDetails(it) }
        disvalmetre
        binding.pathsaveBtn.visibility = View.GONE

        binding.pathsaveBtn.setOnClickListener {
            if(disvalmetre > 0)
            {
                updateBookStswithdist("3",disvalmetre.toString())
            }
            else{
                toastz(requireContext(),"Distance is invalid")
            }
        }
    }

    private fun calculateDistance(){

        if(!isAdded) return
        val lablat = binding.labLat.text.toString()
        val lablong = binding.labLong.text.toString()
        val custlat = binding.usrLat.text.toString()
        val custlong = binding.usrLong.text.toString()

        if(lablat.isEmpty() or lablong.isEmpty() or custlat.isEmpty() or custlong.isEmpty())
        {
            lifecycleScope.launch {
                delay(2000)
                calculateDistance()
            }
            return
        }


        val StartPoint = "$custlat, $custlong"
        val EndPoint = "$lablat , $lablong"

        val GOOGLBASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/"
        val mlogger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        // Create OkHttp Client
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(mlogger)

        // Create Retrofit Builder
        val rbuilder = Retrofit.Builder().baseUrl(GOOGLBASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())

        // Create Retrofit Instance
        val retrofit = rbuilder.build()

        fun <T> buildService(serviceType: Class<T>): T {
            return retrofit.create(serviceType)
        }

        //val google_api_key: String = getString(R.string.google_maps_key)
        val mapiService = buildService(MapApiService::class.java)
        val maprequestCall = mapiService.getDistance(StartPoint,EndPoint,"driving",MAP_WEB_API_KEY)
        maprequestCall.enqueue(object : Callback<GoogleDistResp> {
            override fun onResponse(call: Call<GoogleDistResp>, resp: Response<GoogleDistResp>) {
                if(!isAdded) return
                val disResponse = resp.body()
                disResponse?.let {
                    val respsts = it.status
                    if(respsts == "OK") {
                        val diststs = it.rows[0].elements[0].status
                        if(diststs == "OK") {
                            val dist = it.rows[0].elements[0].distance
                            val distextkm = dist.text
                            disvalmetre = dist.value
                            binding.distanceText.text = distextkm

                            if(disvalmetre > 0){
                                binding.pathsaveBtn.visibility = View.VISIBLE
                            }

                        }else{
                            toastz(requireContext(),"Could not get distance.Please try Later")
                        }
                    }
                    else{
                        toastz(requireContext(),"Could not calculate distance.Please try Later")
                    }
                }

            }
            override fun onFailure(call: Call<GoogleDistResp>, t: Throwable) {
                toastz(requireContext(),t.message.toString())
            }
        })
    }

    fun updateBookStswithdist(stsID:String,dist:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.changeBookingSts(authTokn,apiTokn,param1,stsID,dist)
        requestCall.enqueue(object : Callback<BookStsUpdateResp> {
            override fun onResponse(call: Call<BookStsUpdateResp>, resp: Response<BookStsUpdateResp>) {
                resp.body().let {
                    if(!isAdded) return
                    if (it != null) {
                        if(it.code == 200){
                            toastz(requireContext(),it.message.toString())
                            if(stsID == "3") //Sample Collected
                            {
                               parentFragmentManager.popBackStackImmediate()
                            } else{

                            }
                        }
                        else
                        {
                            toastz(requireContext(),it.message.toString())
                        }
                    }
                }
            }
            override fun onFailure(call: Call<BookStsUpdateResp>, t: Throwable) {
                toastz(requireContext(),t.message.toString())
            }
        })
    }

    fun bookingDetails(bookID:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.bookDetailsForPath(authTokn,apiTokn,bookID)
        requestCall.enqueue(object : Callback<BookingDetailsforPathResp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<BookingDetailsforPathResp>, resp: Response<BookingDetailsforPathResp>) {
                if(!isAdded) return
                resp.body().let {
                    if (it != null) {
                       if(it.code == 200){
                           val lb = it.booking
                           binding.labTitle.text = "Lab : " + lb.lab_name
                           binding.labDetails.text = "Address: " + lb.lab_address
                           binding.pinCode.text = "District : " + districtz[lb.lab_district.toInt()]+"   Pincode : " + lb.lab_pincode
                           binding.labLat.text  = lb.lab_latitude.toString()
                           binding.labLong.text   = lb.lab_longitude.toString()

                       } else{
                            toastz(requireContext(),it.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<BookingDetailsforPathResp>, t: Throwable) {
                toastz(requireContext(),t.message.toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        with(map.uiSettings) {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = false
            isScrollGesturesEnabled = false
            isScrollGesturesEnabledDuringRotateOrZoom = false
            isZoomGesturesEnabled = false

        }
        checkGpsStatus()
        enableMyLocation()
        getDeviceLocation()

    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(
                activity as AppCompatActivity, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }

    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.


        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
            // [END_EXCLUDE]
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResume() {
        super.onResume()

        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
        else{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true

                lifecycleScope.launch {
                    delay(3000)
                    checkGpsStatus()
                    getDeviceLocation()
                }
            }


        }

    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(childFragmentManager, "dialog")
    }

    private fun checkGpsStatus() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {


        } else {
           // toastz(requireContext(),"Please enable Location service(GPS) on your Device")
            lifecycleScope.launch {
                delay(1500)
                gpsStatus()
            }
        }
    }

    private fun gpsStatus() {
        val intentgps = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {

        }
        resultLauncher.launch(intentgps)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        lifecycleScope.launch {
            delay(3000)
            getDeviceLocation()
        }



    }


    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!permissionDenied) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if(task.result != null) {
                            binding.usrLat.text = lastKnownLocation!!.latitude.toString()
                            binding.usrLong.text = lastKnownLocation!!.longitude.toString()

                            val geocoder = Geocoder(requireContext())
                            val list = geocoder.getFromLocation(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude, 1)
                            var fullAddress = "Location address not Available"
                            if(list.size > 0)
                            {
                                fullAddress = list[0].getAddressLine(0)
                            }
                            binding.locationAddress.text = fullAddress

                        }
                        else {
                            binding.locationAddress.text = "Location not available."
                        }
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude), ZOOM_LEVEL))

                        }
                        calculateDistance()
                    } else {
                        Log.d("Dloc", "Current location is null. Using defaults.")
                        Log.e("Dloc", "Exception: %s", task.exception)
                       // toastz(requireContext(),"Cannot get Device Location")
                       // map?.animateCamera(CameraUpdateFactory.newLatLngZoom(DEF_LOCATION, ZOOM_LEVEL))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LabDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "BookPathFrag"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    interface MapApiService {
        @GET("json")
        fun getDistance(
            @Query("origins") origins: String?,
            @Query("destinations") destinations: String?,
            @Query("mode") mode: String?,
            @Query("key") key: String
        ):Call<GoogleDistResp>
    }

}