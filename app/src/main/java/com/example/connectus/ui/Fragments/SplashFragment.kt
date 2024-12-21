package com.example.connectus.ui.Fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val splashFragment = Runnable {
            findNavController().navigate(R.id.splashFragment)

        }
        val startFragment = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_startFragment)

        }
        val mainFragment = Runnable {
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)

        }

        if (auth.currentUser == null) {

            Handler().postDelayed(startFragment, 2000)
        } else {
            Handler().postDelayed(mainFragment, 500)
        }


        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}