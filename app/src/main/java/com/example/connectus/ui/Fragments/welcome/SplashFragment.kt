package com.example.connectus.ui.Fragments.welcome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Задержка для показа splash-экрана
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAndNavigate()
        }, 2000) // Задержка 2 секунды

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun checkUserAndNavigate() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // Пользователь не вошёл, переходим на экран приветствия
            findNavController().navigate(R.id.action_splashFragment_to_startFragment)
        } else {
            // Пользователь вошёл, проверяем, подтверждён ли email
            if (currentUser.isEmailVerified) {
                // Email подтверждён, переходим на главный экран
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            } else {
                // Email не подтверждён, переходим на экран подтверждения email
                findNavController().navigate(R.id.action_splashFragment_to_OTPVerificationFragment)
            }
        }
    }
}