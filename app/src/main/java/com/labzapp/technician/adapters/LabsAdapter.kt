package com.labzapp.technician.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.labzapp.technician.R
import com.labzapp.technician.databinding.LabListItemBinding
import com.labzapp.technician.model.LabData
import com.labzapp.technician.ui.labs.LabDetailsFragment
import com.labzapp.technician.utils.districtz
import com.squareup.picasso.Picasso

class LabsAdapter( val context: Context) : RecyclerView.Adapter<LabsAdapter.LabViewHolder>() {

    private var labs: List<LabData> = ArrayList()

    val rupee = context.getString(R.string.rupee)

    var labLogoUrl: String? = null

    fun setLabList(labs: List<LabData>, logoUrl:String?){
        this.labs = labs
        this.labLogoUrl = logoUrl
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabViewHolder {
        val binding = LabListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return labs.size
    }

    override fun onBindViewHolder(holder: LabViewHolder, position: Int) {
        val labp = labs[position]
        holder.setData(labp, position)
    }

    inner class LabViewHolder(private val binding: LabListItemBinding) : RecyclerView.ViewHolder(binding.root){
        var currentLab: LabData? = null
        var currentPosition: Int = 0
        init {
            itemView.setOnClickListener {
                currentLab?.let {
                    val bundle = Bundle()
                    bundle.putString("param1", it.lab_id.toString())
                    val appCompatActivity = context as AppCompatActivity
                    val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
                    val openfragmt = LabDetailsFragment()
                    openfragmt.arguments = bundle
                    transaction.replace(R.id.activity_main_content_id, openfragmt)
                    transaction.addToBackStack(null)
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    transaction.commit()
                }
            }

        }
        @SuppressLint("SetTextI18n")
        fun setData(lab: LabData?, pos: Int) {
            lab?.let {
                binding.labTitle.text = it.lab_name
                binding.labDetails.text = it.lab_address
                binding.labDistrict.text = "District : " + it.district_name
                binding.labFooter.text =  "Service Charge : "+rupee+it.service_charge.toFloat().toString()
                binding.pinCode.text = "Pincode : "+ it.lab_pincode
                if(it.lab_logo != null) {
                    val labImageUrl = labLogoUrl +"/"+ (it.lab_logo)
                    Picasso.with(context).load(labImageUrl).fit().centerCrop()
                        .into(binding.labLogo)
                }else
                {
                    Picasso.with(context).load(R.drawable.no_lab).fit().centerCrop()
                        .into(binding.labLogo)
                }

            }
            this.currentLab = lab
            this.currentPosition = pos
        }
    }
}
