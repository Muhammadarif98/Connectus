package com.example.connectus.ui.Fragments.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentOTPVerificationBinding


class OTPVerificationFragment : Fragment() {
    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPVerificationBinding.inflate(inflater, container, false)
        val view = binding.root



        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(R.id.action_OTPVerificationFragment_to_mainFragment)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }


}