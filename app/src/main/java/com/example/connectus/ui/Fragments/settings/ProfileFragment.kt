package com.example.connectus.ui.Fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connectus.SharedPrefs
import com.example.connectus.databinding.FragmentProfileBinding
import com.example.connectus.mvvm.ChatAppViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var userViewModel: ChatAppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Настройка тулбара


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        //binding.lifecycleOwner = viewLifecycleOwner
        val mySharedPrefs = SharedPrefs(requireContext())
        val name = mySharedPrefs.getValue("name") as String
        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {

            Glide.with(this)
                .load(it)
                .into(binding.profileImage)
            //Glide.with(requireActivity()).load(it).into(binding.profileImage)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
