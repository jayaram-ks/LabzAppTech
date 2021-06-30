package com.labzapp.technician.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.labzapp.technician.activities.MainActivity
import com.labzapp.technician.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setActionBarTitle("My Profile")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.myDetails()

        viewModel.profiledet.observe(viewLifecycleOwner, {
            binding.techId.text = it.technician_id.toString()
            binding.userName.text = it.technician_name
            binding.userPhone.text = it.technician_mobile.toString()
            binding.userAddress.text = it.technician_address
            binding.userDistrict.text = it.district_name
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}