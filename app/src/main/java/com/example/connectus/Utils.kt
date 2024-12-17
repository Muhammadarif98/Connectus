package com.example.connectus

import com.google.firebase.auth.FirebaseAuth

class Utils {
    companion object {

        private val auth = FirebaseAuth.getInstance()
        private var userId: String = ""


        fun getUiLoggedId(): String {

            if (auth.currentUser != null) {

                userId = auth.currentUser!!.uid


            }

            return userId
        }
    }
}