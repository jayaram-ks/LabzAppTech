package com.labzapp.technician.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.labzapp.technician.R
import com.labzapp.technician.databinding.BookingDetailsTestlistItemBinding
import com.labzapp.technician.model.BookedTests

class BookTestRateAdapter(val context: Context, private val testrates: ArrayList<BookedTests>) : RecyclerView.Adapter<BookTestRateAdapter.TestRateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestRateViewHolder {
        val binding = BookingDetailsTestlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestRateViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return testrates.size
    }

    override fun onBindViewHolder(holder: TestRateViewHolder, position: Int) {
        val testratep = testrates[position]
        holder.setData(testratep, position)
    }

    inner class TestRateViewHolder(private val binding: BookingDetailsTestlistItemBinding) : RecyclerView.ViewHolder(binding.root){

        var currentTestRate: BookedTests? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {

            }
        }

        @SuppressLint("SetTextI18n")
        fun setData(testrate: BookedTests?, pos: Int) {
            testrate?.let {
                binding.testzTitle.text = it.test_name
                binding.testzRate.text = context.getString(R.string.rupee)+" "+it.booking_test_rate.toString()
            }
            this.currentTestRate = testrate
            this.currentPosition = pos
        }
    }
}