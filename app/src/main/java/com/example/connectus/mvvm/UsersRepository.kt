package com.example.connectus.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepository {
    private var fireStore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<Users>> {
        val users = MutableLiveData<List<Users>>()

        fireStore.collection("users").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("UsersRepo", "Error getting users: ", exception)
                return@addSnapshotListener
            }

            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->
                Log.d("UsersRepo", "Document data: ${document.data}")

                // Маппинг данных вручную
                val userId = document.getString("userId")
                val userName = document.getString("name")
                val userLastName = document.getString("lastName")
                val userPhone = document.getString("phone")
                val userEmail = document.getString("email")
                val userPassword = document.getString("password")
                val userConfirmPassword = document.getString("confirmPassword")
                val userImageUrl = document.getString("image")
                val status = document.getString("status")
                val adress = document.getString("adress")
                val age = document.getString("age")
                val employee = document.getString("employee")

                val user = Users(
                    userId = userId,
                    name = userName,
                    lastName = userLastName,
                    phone = userPhone,
                    email = userEmail,
                    password = userPassword,
                    confirmPassword = userConfirmPassword,
                    imageUrl = userImageUrl,
                    status = status,
                    adress = adress,
                    age = age,
                    employee = employee
                )

                Log.d("UsersRepo", "Loaded user: ${user.name}, ${user.imageUrl}")
                if (user.userId != getUidLoggedIn()) {
                    usersList.add(user)
                }
            }

            users.value = usersList
        }

        return users
    }
}