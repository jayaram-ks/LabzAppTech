package com.labzapp.technician.ui.bookings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.labzapp.technician.R
import com.labzapp.technician.activities.MainActivity
import com.labzapp.technician.adapters.BookTestRateAdapter
import com.labzapp.technician.databinding.FragmentBookingDetailsBinding
import com.labzapp.technician.model.BookStsUpdateResp
import com.labzapp.technician.model.PrintStsUpdateResp
import com.labzapp.technician.model.SinglePackResponse
import com.labzapp.technician.model.UploadReportResponse
import com.labzapp.technician.services.network.ApiService
import com.labzapp.technician.services.network.ServiceBuilder
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.utils.*
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BookingDetailsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBookingDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingDetailsViewModel

    private var touploadfile: File? = null
    private var packOrTest:String? = null
    private var packID:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        (activity as MainActivity?)?.setActionBarTitle("Booking Details")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookingDetailsViewModel::class.java)
        param1?.let { viewModel.bookDetails(it) }

        viewModel.bookdetresp.observe(viewLifecycleOwner, {
            val rupee = context?.getString(R.string.rupee) + " "
            if(it.code == 200){
                it.booking.let{
                    packOrTest = it.test_or_pack
                    packID = it.package_id
                    binding.patName.text = "Name : "+it.patient_name
                    binding.patAddress.text = "Address : "+it.patient_address
                    binding.patAgeGender.text = "Age : "+it.age +"  Gender : " + genderz[it.gender.toInt()]
                    binding.patPincodeDistrict.text = "Pincode : "+ it.patient_pincode + " District : " + districtz[it.patient_district.toInt()]
                    binding.patPhone.text = "Phone : "+it.mobile

                    val datFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                    val datb =  LocalDate.parse(it.booking_date , datFormat)
                    val bookdate = datb.dayOfMonth.toString() +" "+datb.month.toString()+" "+datb.year.toString()
                    if(it.pref_date != null) {
                        val datp = LocalDate.parse(it.pref_date, datFormat)
                        val prefdate = datp.dayOfMonth.toString() + " " + datp.month.toString() + " " + datp.year.toString()
                        binding.sampleCollDate.text = "Customer Preferred Date : $prefdate"
                    }
                    else{
                        binding.sampleCollDate.text = "Customer Preferred Date : -NA- "
                    }
                    if(it.tech_alloc_date_time != null) {

                        binding.officeAllocDate.text = "Office Allocated Date : " + it.tech_alloc_date_time
                    }
                    else{
                        binding.officeAllocDate.text = "Office Allocated Date : -NA-"
                    }

                    binding.bookId.text = "Booking ID : "+it.id.toString()
                    binding.bookingDate.text = "Booking Date : $bookdate"
                    binding.labAddress.text = "Address : " + it.lab_address
                    binding.labName.text ="Name : " + it.lab_name
                    binding.labPinDistrict.text = "Pincode : "+ it.lab_pincode
                    binding.testTotal.text = rupee +  it.booking_total
                    binding.serviceCharges.text = rupee +  it.service_charge.toFloat().toString()
                    binding.grandTotal.text = rupee + it.grand_total.toFloat().toString()

                    binding.bookStatus.text = "Booking Status : "+bookingzsts[it.booking_status.toInt()]
                    binding.printNeeded.text = "Printed Bill Needed : "+printzneed[it.paper_bill.toInt()]
                    binding.printGiven.text =  "Print Delivery Status : "+printzsts[it.paper_bill_status.toInt()]
                    if(it.distance_travel.toInt() > 0 ){
                        val distmeter = it.distance_travel.toFloat()
                        val distkmfloat : Float = (distmeter / 1000)
                        binding.distanceTravel.text = "Distance travelled : $distkmfloat km"
                    }
                    else{
                        binding.distanceTravel.text = "Distance travelled : Not Updated"
                    }


                    if(it.report_file == null){
                        binding.reportFile.text = "Report File : Not uploaded"
                        binding.viewReportFile.visibility = View.GONE
                    }else{
                        val rf = it
                        rf.let {  rf ->
                            binding.viewReportFile.visibility = View.VISIBLE
                            binding.reportFile.text = "Report File :  Uploaded"
                            binding.viewReportFile.setOnClickListener {
                                val labreporturl = rf.report_file
                                val bundle = Bundle()
                                bundle.putString("param1", labreporturl)
                                val appCompatActivity = context as AppCompatActivity
                                val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
                                val openfragmt = ReportWebView()
                                openfragmt.arguments = bundle
                                transaction.replace(R.id.activity_main_content_id, openfragmt)
                                transaction.addToBackStack(null)
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                transaction.commit()

                            }
                        }
                    }

                    when(it.booking_status.toInt()){

                        2 -> { //Tech Allocated
                            binding.btnDeclineBook.visibility = View.VISIBLE
                            binding.btnSampleColl.visibility = View.VISIBLE
                            binding.btnBookComplt.visibility = View.GONE
                        }

                        3 -> { //Sample Collected
                            binding.btnDeclineBook.visibility = View.GONE
                            binding.btnSampleColl.visibility = View.GONE
                            binding.btnBookComplt.visibility = View.VISIBLE
                        }

                        4 -> { //Completed
                            binding.btnDeclineBook.visibility = View.GONE
                            binding.btnSampleColl.visibility = View.GONE
                            binding.btnBookComplt.visibility = View.GONE
                        }
                    }


                }

                if(packOrTest == "2"){ //Package Book
                    binding.packDetCard.visibility = View.VISIBLE
                    binding.testDetCard.visibility = View.GONE
                    binding.testTotalTxt.text = "Package total"
                    if(packID != null) { fetchPackageDetails(packID.toString()) }
                }
                else {
                    binding.packDetCard.visibility = View.GONE
                    binding.testDetCard.visibility = View.VISIBLE
                    binding.testTotalTxt.text = "Tests total"

                    it.tests.let {
                        val layoutManager = LinearLayoutManager(activity)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL
                        binding.testListView.layoutManager = layoutManager
                        binding.testListView.adapter = BookTestRateAdapter(requireContext(), it)
                    }
                }

            }else{
                toastz(requireContext(), it.message)
            }
        })


        binding.btnDeclineBook.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Decline this Booking")
            builder.setMessage("Are you Sure?")
            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                updateBookSts("1") //Tech not Allocated
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.btnSampleColl.setOnClickListener {
            val bundle = Bundle()  //stsID:3
            bundle.putString("param1", param1)
            val transaction = parentFragmentManager.beginTransaction()
            val openfragmt = BookingPath()
            openfragmt.arguments = bundle
            transaction.replace(R.id.activity_main_content_id, openfragmt)
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        }

        binding.btnBookComplt.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Booking Completed")
            builder.setMessage("Are you Sure?")
            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                updateBookSts("4")  //Completed
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.btnPrintGivn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Print Given")
            builder.setMessage("Are you Sure?")
            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                updatePrintSts("2")
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.btnPrintNotg.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Print not Given")
            builder.setMessage("Are you Sure?")
            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                updatePrintSts("1")
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.btnUploadReport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            resultLauncher.launch(intent)
        }

    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            touploadfile = null

            data?.data?.also { documentUri ->
                requireActivity().contentResolver?.takePersistableUriPermission(
                    documentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val file = DocumentUtils.getFile(requireContext(),documentUri)//use pdf as file
                touploadfile = file
            }


            if( touploadfile != null){
                uploadReport()
            }
            else{
                toastz(requireContext(), "Invalid Image/No Image Selected")
            }
        }
    }

    object DocumentUtils {
        fun getFile(mContext: Context, documentUri: Uri): File {
            val inputStream = mContext?.contentResolver?.openInputStream(documentUri)
            var file: File
            inputStream.use { input ->
                file =
                    File(mContext?.cacheDir, System.currentTimeMillis().toString()+".pdf")
                FileOutputStream(file).use { output ->
                    val buffer =
                        ByteArray(4 * 1024) // or other buffer size
                    var read: Int = -1
                    while (input?.read(buffer).also {
                            if (it != null) {
                                read = it
                            }
                        } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
            }
            return file
        }
    }


    private fun uploadReport() {
        val authTokn: String? = "Bearer " + SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)

        val ApTokn: RequestBody? = apiTokn?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uploadFileReq = touploadfile?.asRequestBody("application/pdf".toMediaTypeOrNull())
        var bookID = param1.toString()
        val bookingID : RequestBody = bookID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if ((uploadFileReq != null)) {
            var uploadFileMulti = MultipartBody.Part.createFormData(
                "lab_report_file",
                touploadfile?.name,
                uploadFileReq
            )
            if(ApTokn != null) {
                val requestCall = apiService.uploadReport(authTokn, ApTokn, bookingID ,uploadFileMulti)
                view?.let{ snackzcolor(it,"Uploading Report....",binding.btnUploadReport.id,"#0000FF",50000) }
                requestCall.enqueue(object : Callback<UploadReportResponse> {
                    override fun onResponse(
                        call: Call<UploadReportResponse>,
                        response: Response<UploadReportResponse>
                    ) {
                        val resp = response.body()

                        if (resp?.code == 200) {
                            resp.let {
                                view?.let{ snackzsucc(it,resp?.message.toString(),binding.btnUploadReport.id) }
                                param1?.let { viewModel.bookDetails(it) }
                            }

                        } else {
                            if (resp != null) {
                                view?.let{ snackze(it,resp?.message.toString(),binding.btnUploadReport.id) }
                            }
                        }
                    }
                    override fun onFailure(call: Call<UploadReportResponse>, t: Throwable) {
                        view?.let{ snackze(it,t?.message.toString(),binding.btnUploadReport.id) }
                    }
                })
            }
        }
    }

    fun updateBookSts(stsID:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.changeBookingSts(authTokn,apiTokn,param1,stsID,"")
        requestCall.enqueue(object : Callback<BookStsUpdateResp> {
            override fun onResponse(call: Call<BookStsUpdateResp>, resp: Response<BookStsUpdateResp>) {
                resp.body().let {
                    if(!isAdded) return
                    if (it != null) {
                        if(it.code == 200){
                            toastz(requireContext(),it.message.toString())
                            if(stsID == "1") //Declined Booking
                            {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else{
                                param1?.let { viewModel.bookDetails(it) }
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

    fun updatePrintSts(pstsID:String) {
        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.changePrintgivenSts(authTokn,apiTokn,param1,pstsID)
        requestCall.enqueue(object : Callback<PrintStsUpdateResp> {
            override fun onResponse(call: Call<PrintStsUpdateResp>, resp: Response<PrintStsUpdateResp>) {
                resp.body().let {
                    if(!isAdded) return
                    if (it != null) {
                        if(it.code == 200){

                            toastz(requireContext(),it.message.toString())
                            param1?.let { viewModel.bookDetails(it) }
                        }
                        else
                        {
                            toastz(requireContext(),it.message.toString())
                        }
                    }
                }
            }
            override fun onFailure(call: Call<PrintStsUpdateResp>, t: Throwable) {
                toastz(requireContext(),t.message.toString())
            }
        })
    }


    private fun fetchPackageDetails(packID:String) {

        val authTokn: String? = "Bearer "+ SharedPrefManager.getInstance(requireContext()).authKey
        val apiTokn: String? = SharedPrefManager.getInstance(requireContext()).apiToken
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getSinglePackDetail(authTokn, apiTokn,packID)
        requestCall.enqueue(object : Callback<SinglePackResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<SinglePackResponse>, response: Response<SinglePackResponse>) {
                val resp = response.body()
                if (resp?.code == 200) {
                    resp.packdetails.let {
                        if (!isAdded) return
                        val rupee = requireContext().getString(R.string.rupee)
                        binding.packTitle.text ="Package : " +it.pack_name
                        binding.testsDetails.text = it.test_count +" Tests : "+ it.pack_tests

                        if( it.pack_image != "") {
                            Picasso.with(context).load(it.pack_image).fit().into(binding.packImg)
                        }
                        else
                        {
                            binding.packImg.visibility = View.GONE
                        }
                    }

                } else {
                    view?.let{ snackze(it,resp?.message.toString(),binding.labName.id) }
                }
            }

            override fun onFailure(call: Call<SinglePackResponse>, t: Throwable) {
                view?.let{ snackze(it,t.message.toString(),binding.labName.id) }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "BookDetailFragment"
    }
}