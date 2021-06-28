package com.labzapp.technician.ui.labs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.labzapp.technician.R
import com.labzapp.technician.adapters.LabsAdapter
import com.labzapp.technician.databinding.LabsFragmentBinding
import com.labzapp.technician.model.Bookings
import com.labzapp.technician.model.LabData
import com.labzapp.technician.model.LabDetail
import com.labzapp.technician.utils.toastz

class LabsFragment : Fragment() {

    companion object {
        fun newInstance() = LabsFragment()
    }

    private var _binding:LabsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var labsadapter: LabsAdapter
    private lateinit var viewModel: LabsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LabsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LabsViewModel::class.java)

        viewModel.allocLabs()

        viewModel.labsresp.observe(viewLifecycleOwner, {
            if(it.code == 200) {
                labsadapter.setLabList(it.labs,it.logopath)
            }else{
                toastz(requireContext(),it.message)
                val labs: List<LabData> = ArrayList()
                labsadapter.setLabList(labs,"")
            }
            val progBar: ProgressBar = binding.progressBar
            progBar.visibility = View.GONE
        })

        labsadapter = LabsAdapter(requireContext())
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.labsRecyclerview.layoutManager = layoutManager
        binding.labsRecyclerview.adapter = labsadapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}