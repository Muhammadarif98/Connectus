package com.example.connectus

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.connectus.databinding.ActivityMainBinding
import com.example.connectus.ui.Fragments.MainFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    init {
        FirebaseApp.initializeApp(this)
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentRunners: FragmentRunners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentRunners = FragmentRunners(this, binding.container.id)

        if (auth.currentUser == null) {
            Handler().postDelayed(fragmentRunners.splashFragment, 500)
            Handler().postDelayed(fragmentRunners.startFragment, 2000)
        } else {
            Handler().postDelayed(fragmentRunners.mainFragment, 500)
        }
    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is MainFragment) {
            finish()
            // DialogUtils.showExitDialog(this, auth, fragmentRunners)
        } else {
            super.onBackPressed()
        }
    }
}
