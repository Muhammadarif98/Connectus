package com.example.connectus.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentLoginBinding

class StartFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root



        binding.buttonLogin.setOnClickListener {
            // Код для открытия следующего фlayout.рагмента
            findNavController().navigate(R.id.action_startFragment_to_loginInputFragment)
//            val loginInputFragment = LoginInputFragment()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.nav_host_fragment, loginInputFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
