package com.example.connectus

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object {
        @SuppressLint("StaticFieldLeak")
        val context = App.instance.applicationContext

        @SuppressLint("StaticFieldLeak")
        val firestore = FirebaseFirestore.getInstance()

        private val auth = FirebaseAuth.getInstance()
        private var userId: String = ""
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
        const val MESSAGE_RIGHT = 1
        const val MESSAGE_LEFT = 2
        const val CHANNEL_ID = "com.example.chatmessenger"

        fun getUiLoggedId(): String {
            if (auth.currentUser != null) {

                userId = auth.currentUser!!.uid

            }
            return userId
        }

        fun getTime(): String {
            val formatter = SimpleDateFormat("HH:mm:ss")
            val date: Date = Date(System.currentTimeMillis())
            val stringdate = formatter.format(date)

            return stringdate

        }
    }
}