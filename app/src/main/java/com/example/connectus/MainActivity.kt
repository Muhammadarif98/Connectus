package com.example.connectus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.connectus.databinding.ActivityMainBinding
import com.example.connectus.ui.Fragments.home.MainFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    init {
        FirebaseApp.initializeApp(this)
    }
    private lateinit var  auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    var token: String = ""

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentRunners: FragmentRunners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser!=null){
            firestore.collection("users")
                .document(Utils.getUidLoggedIn()).update("status", "Online")
        }
    }

    override fun onPause() {
        super.onPause()
        if (auth.currentUser!=null){
            firestore.collection("users")
                .document(Utils.getUidLoggedIn()).update("status", "Offline")
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            firestore.collection("users")
                .document(Utils.getUidLoggedIn()).update("status", "Online")
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment is MainFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}