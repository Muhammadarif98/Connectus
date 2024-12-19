package com.example.connectus.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectus.App
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.data.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String?>()
    val imageUrl = MutableLiveData<String?>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()

    init {
        getCurrentUser()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext

        firestore.collection("users").document(getUiLoggedId())
            .addSnapshotListener { value, error ->
                if (value != null && value.exists()) {
                    val userId = value.getString("userId")
                    val userName = value.getString("name")
                    val userLastName = value.getString("lastName")
                    val userPhone = value.getString("phone")
                    val userEmail = value.getString("email")
                    val userPassword = value.getString("password")
                    val userConfirmPassword = value.getString("confirmPassword")
                    val userImageUrl = value.getString("image")
                    val status = value.getString("status")

                    name.postValue(userName)
                    imageUrl.postValue(userImageUrl)

                    val mySharedPrefs = SharedPrefs(context)
                    if (userName != null) {
                        mySharedPrefs.setValue("name", userName)
                    }
                    if (userImageUrl != null) {
                        mySharedPrefs.setValue("imageUrl", userImageUrl)
                    }
                }
            }
    }
}
