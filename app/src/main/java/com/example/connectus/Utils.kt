package com.example.connectus

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object {

        val supabase = createSupabaseClient(
            supabaseUrl = "https://cpozuctgjtujueilydrs.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNwb3p1Y3RnanR1anVlaWx5ZHJzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzU1NTQ0ODIsImV4cCI6MjA1MTEzMDQ4Mn0.dPE0pHw8Daj3so6Ox03XTUwz6Eo5htet3K9P3GG-zN8"
        ) {
            install(Auth)
            install(Realtime)
            install(Storage)
            install(Postgrest)
        }


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
        const val CHANNEL_ID = "com.example.connectus"

        fun getUidLoggedIn(): String {
            if (auth.currentUser != null) {

                userId = auth.currentUser!!.uid

            }
            return userId
        }

        fun getUidFriendIn(): String {
            if (auth.currentUser != null) {
                userId = auth.uid!!
            }
            return userId
        }

        @SuppressLint("SimpleDateFormat")
        fun getTime(): String {
            val formatter = SimpleDateFormat("dd.MM, HH:mm:ss")
            val date: Date = Date(System.currentTimeMillis())
            val stringDate = formatter.format(date)

            return stringDate
        }

    }
}
//@SuppressLint("SimpleDateFormat")
//fun getTime(): String {
//    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val date: Date = Date(System.currentTimeMillis())
//    val stringdate = formatter.format(date)
//    return stringdate
//}