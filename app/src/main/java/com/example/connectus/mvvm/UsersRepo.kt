package com.example.connectus.mvvm

import Utils.Companion.getUiLoggedId
import Utils.Companion.supabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.data.model.Users
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersRepo {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun getUsers(): LiveData<List<Users>> {
        val users = MutableLiveData<List<Users>>()
        coroutineScope.launch {
            try {
                val response = supabase.postgrest["users"].select(Columns.ALL).decodeList<Users>()
                if (response.isNotEmpty()) {
                    val usersList = mutableListOf<Users>()
                    response.forEach {
                        val user = Users(
                            user_id = it.user_id,
                            name = it.name,
                            lastName = it.lastName,
                            phone = it.phone,
                            email = it.email,
                            password = it.password,
                            confirmPassword = it.confirmPassword,
                            imageUrl = it.imageUrl,
                            status = it.status
                        )
                        Log.d("UsersRepo", "Loaded user: ${user.name}, ${user.imageUrl}")
                        if (user.user_id != getUiLoggedId()) {
                            usersList.add(user)
                        }
                    }
                    users.postValue(usersList)
                } else {
                    Log.e("UsersRepo", "Ошибка загрузки пользователей")
                }
            } catch (e: Exception) {
                Log.e("UsersRepo", "Exception: ${e.message}")
            }
        }

        return users
    }
}
// private var fireStore = FirebaseFirestore.getInstance()
/* fireStore.collection("users").addSnapshotListener { snapshot, exception ->
     if (exception != null) {
         Log.e("UsersRepo", "Error getting users: ", exception)
         return@addSnapshotListener
     }

     val usersList = mutableListOf<Users>()
     snapshot?.documents?.forEach { document ->
         Log.d("UsersRepo", "Document data: ${document.data}")

         val userId = document.getString("user_id")
         val userName = document.getString("name")
         val userLastName = document.getString("lastName")
         val userPhone = document.getString("phone")
         val userEmail = document.getString("email")
         val userPassword = document.getString("password")
         val userConfirmPassword = document.getString("confirmPassword")
         val userImageUrl = document.getString("image")
         val status = document.getString("status")

         val user = Users(
             user_id = userId,
             name = userName,
             lastName = userLastName,
             phone = userPhone,
             email = userEmail,
             password = userPassword,
             confirmPassword = userConfirmPassword,
             imageUrl = userImageUrl,
             status = status
         )

         Log.d("UsersRepo", "Loaded user: ${user.name}, ${user.imageUrl}")
         if (user.user_id != getUiLoggedId()) {
             usersList.add(user)
         }
     }

     users.value = usersList
 }*/





