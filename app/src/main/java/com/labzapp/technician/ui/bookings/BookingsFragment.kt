package com.labzapp.technician.ui.bookings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.labzapp.technician.activities.MainViewModel
import com.labzapp.technician.adapters.MyBookAdapter
import com.labzapp.technician.databinding.BookingsFragmentBinding
import com.labzapp.technician.model.Bookings
import com.labzapp.technician.utils.toastz


class BookingsFragment : Fragment() {

    private var _binding: BookingsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingsadapter: MyBookAdapter
    private  lateinit var sharedViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BookingsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progBar: ProgressBar = binding.progressBar

        sharedViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        sharedViewModel.bookingsresp.observe(viewLifecycleOwner, {

            if(it.code == 200) {
                bookingsadapter.setBookList(it.bookings)
            }
            else{
                toastz(requireContext(),it.message)
                val bookings: List<Bookings> = ArrayList()
                bookingsadapter.setBookList(bookings)
            }

            progBar.visibility = View.GONE
        })

        bookingsadapter = MyBookAdapter(requireContext())
        binding.mybookRecyclerview.adapter = bookingsadapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}