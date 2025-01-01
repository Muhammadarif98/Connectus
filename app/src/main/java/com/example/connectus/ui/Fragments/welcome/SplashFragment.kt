package com.example.connectus.ui.Fragments.welcome

import Utils.Companion.getUiLoggedId
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val user = getUiLoggedId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root

        val splashFragment = Runnable {
            findNavController().navigate(R.id.splashFragment)

        }
        val startFragment = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_startFragment)

        }
        val mainFragment = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)

        }
        if (user == null) {
            Handler().postDelayed(startFragment, 2000)
        } else {
            Handler().postDelayed(mainFragment, 500)
        }
        return view


    }
}