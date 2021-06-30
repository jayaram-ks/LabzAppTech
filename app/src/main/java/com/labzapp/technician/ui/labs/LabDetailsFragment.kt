package com.labzapp.technician.ui.labs

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.labzapp.technician.databinding.FragmentLabDetailsBinding
import com.labzapp.technician.utils.maps.PermissionUtils
import com.labzapp.technician.utils.toastz
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LabDetailsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback  {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLabDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LabDetailsViewModel
    private lateinit var locUpdatVM: LocationUpdateViewModel

    private lateinit var locationManager: LocationManager
    var gpsStatus = false
    var intentgps: Intent? = null
    private var permissionDenied = false
    private var lastKnownLocation: Location? = null
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val DEF_LOCATION = LatLng(9.9312, 76.2673)
    val ZOOM_LEVEL = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        (activity as MainActivity?)?.setActionBarTitle("Lab Details")
        lastKnownLocation?.latitude  = DEF_LOCATION.latitude
        lastKnownLocation?.longitude = DEF_LOCATION.longitude
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLabDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_labdet) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        viewModel = ViewModelProvider(this).get(LabDetailsViewModel::class.java)

        param1?.let { viewModel.labDetails(it) }

        viewModel.labsdetresp.observe(viewLifecycleOwner, {
            if(it.code == 200){

                it.data.let { lb ->
                    val rupee = context?.getString(R.string.rupee)
                    binding.labTitle.text = lb.lab_name
                    binding.labDetails.text = lb.lab_address
                    binding.labDistrict.text = "District : " + lb.district_name
                    val labphone = if(lb.lab_phone == null ) { "-----"} else { lb.lab_phone }
                    binding.labFooter.text = "Service Charge : " + rupee + lb.service_charge.toFloat().toString()+"    Phone : "+labphone
                    binding.pinCode.text = "Pincode : " + lb.lab_pincode

                    if (lb.lab_logo != null) {
                        Picasso.with(context).load(lb.lab_logo).fit().centerCrop()
                            .into(binding.labLogo)
                    } else {
                        Picasso.with(context).load(R.drawable.no_lab).fit().centerCrop()
                            .into(binding.labLogo)
                    }

                    val clatitude  = lb.lab_latitude
                    val clongitude   = lb.lab_longitude
                    map.clear()
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(clatitude,clongitude), ZOOM_LEVEL))

                    setCameraIdleListener(map)

                    val geocoder = Geocoder(requireContext())
                    val list = geocoder.getFromLocation(clatitude, clongitude, 1)
                    var fullAddress = "Location address not Available"
                    if(list.size > 0)
                    {
                        fullAddress = list[0].getAddressLine(0)
                    }
                    binding.locationAddress.text = fullAddress
                    binding.usrLat.text = clatitude.toString()
                    binding.usrLong.text = clongitude.toString()
                }
            }
            else{
                toastz(requireContext(), it.message)
            }
        })

        locUpdatVM = ViewModelProvider(this).get(LocationUpdateViewModel::class.java)

        binding.locationsaveBtn.setOnClickListener { confirmLocUpdate() }

    }

    private fun confirmLocUpdate(){

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Location as shown on map?")
        //builder.setMessage("Are you Sure?")
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            val lab_id = param1.toString()
            val latitud = binding.usrLat.text.toString()
            val longitud = binding.usrLong.text.toString()
            if((lab_id != "") and (latitud != "") and (longitud != "")){
                locUpdatVM.updateLoc(lab_id,latitud,longitud)
            }

        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        const val TAG = "LabDetailsFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setOnMyLocationButtonClickListener(this)
        with(map.uiSettings) {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = true

        }

        enableMyLocation()

    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            checkGpsStatus()
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(
                activity as AppCompatActivity, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
        }
        // [END maps_check_location_permission]
    }


    override fun onMyLocationButtonClick(): Boolean {
        getDeviceLocation()
        return true
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
                checkGpsStatus()
            }
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(childFragmentManager, "dialog")
    }

    private fun setCameraIdleListener(map: GoogleMap) {
        map.setOnCameraIdleListener(object : GoogleMap.OnCameraIdleListener {
            override fun onCameraIdle() {
                val actualLatLng: LatLng = map.cameraPosition.target
                val geocoder = Geocoder(requireActivity())
                val list = geocoder.getFromLocation(actualLatLng!!.latitude, actualLatLng!!.longitude, 1)
                binding.usrLat.text = actualLatLng!!.latitude.toString()
                binding.usrLong.text = actualLatLng!!.longitude.toString()
                var fullAddress = "Location address not Available"
                if(list.size > 0)
                {
                    fullAddress = list[0].getAddressLine(0)
                }
                binding.locationAddress.text = fullAddress
            }

        })
    }


    private fun checkGpsStatus() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {

        } else {
            toastz(requireContext(),"Please enable Location service(GPS) on your Device")
            lifecycleScope.launch {
                delay(2000)
                gpsStatus()
            }
        }
    }

    private fun gpsStatus() {
        intentgps = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intentgps);
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
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude), ZOOM_LEVEL))
                            setCameraIdleListener(map)
                        }

                    } else {
                        Log.d("Dloc", "Current location is null. Using defaults.")
                        Log.e("Dloc", "Exception: %s", task.exception)
                        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(DEF_LOCATION, ZOOM_LEVEL))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

}