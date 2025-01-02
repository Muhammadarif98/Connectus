package com.example.connectus.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectus.App
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.data.model.Messages
import com.example.connectus.data.model.RecentChats
import com.example.connectus.data.model.Users
import com.example.connectus.notifications.entity.NotificationData
import com.example.connectus.notifications.entity.PushNotification
import com.example.connectus.notifications.entity.Token
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String?>()
    val imageUrl = MutableLiveData<String?>()
    val lastName = MutableLiveData<String?>()
    val phone = MutableLiveData<String?>()
    val email = MutableLiveData<String?>()
    val password = MutableLiveData<String?>()
    val confirmPassword = MutableLiveData<String?>()
    val status = MutableLiveData<String?>()
    val adress = MutableLiveData<String?>()
    val age = MutableLiveData<String?>()
    val employee = MutableLiveData<String?>()

    val message = MutableLiveData<String>()
    private var token: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    val messageRepo = MessageRepo()
    val chatlistRepo = ChatListRepo()
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val usersRepo = UsersRepo()

    init {
        getCurrentUser()
        getRecentUsers()
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
                    lastName.postValue(userLastName)
                    phone.postValue(userPhone)
                    email.postValue(userEmail)
                    password.postValue(userPassword)
                    confirmPassword.postValue(userConfirmPassword)
                    this@ChatAppViewModel.status.postValue(status)

                    val mySharedPrefs = SharedPrefs(context)
                    when {
                        userName != null -> mySharedPrefs.setValue("name", userName)
                        userImageUrl != null -> mySharedPrefs.setValue("imageUrl", userImageUrl)
                        userEmail != null -> mySharedPrefs.setValue("email", userEmail)
                        status != null -> mySharedPrefs.setValue("status", status)
                        userPhone != null -> mySharedPrefs.setValue("phone", userPhone)
                        userLastName != null -> mySharedPrefs.setValue("lastName", userLastName)
                        userPassword != null -> mySharedPrefs.setValue("password", userPassword)
                        userConfirmPassword != null -> mySharedPrefs.setValue("confirmPassword",
                            userConfirmPassword)

                    }

                }
            }
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = App.instance.applicationContext
            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->
                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to getUiLoggedId(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )


                    firestore.collection("Conversation${getUiLoggedId()}").document(receiver)
                        .set(setHashap)

                    firestore.collection("Conversation${receiver}").document(getUiLoggedId())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )
                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                token = tokenObject?.token!!
                                val loggedInUsername =
                                    mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(loggedInUsername, message.value!!), token!!
                                    ).also {
                                        //sendNotification(it)
                                    }
                                } else {
                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                                }
                            }
                            Log.e("ViewModel", token.toString())
                            if (taskmessage.isSuccessful) {
                                message.value = ""
                            }
                        }
                }
        }


    fun getMessages(friend: String): LiveData<List<Messages>> {

        return messageRepo.getMessages(friend)
    }


    fun getRecentUsers(): LiveData<List<RecentChats>> {

        return chatlistRepo.getAllChatList()

    }


    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            //   val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {

            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }


    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext


      //  progressDialogUpdate.setMessage("Обновление данных...")
        val documentId = getUiLoggedId()
        val hashMapUser = hashMapOf<String, Any>(
            "userId" to documentId,
            "name" to name.value!!,
            "lastName" to lastName.value!!,
            "phone" to phone.value!!,
            "email" to email.value!!,
            "password" to password.value!!,
            "confirmPassword" to confirmPassword.value!!,
            "image" to imageUrl.value!!,
            "status" to status.value!!
        )

        // Проверка существования документа
        firestore.collection("users").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Документ существует, выполняем обновление
                    firestore.collection("users").document(documentId).update(hashMapUser)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                               // progressDialogUpdate.dismiss()
                                Log.d("СhangeProfileFragment", "Profile updated successfully!")
                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                            } else {
                               // progressDialogUpdate.dismiss()
                                Log.e("СhangeProfileFragment", "Failed to update profile ${it.exception?.message}")
                            }
                        }
                } else {
                   // progressDialogUpdate.dismiss()
                    Log.e("СhangeProfileFragment", "Document not found: $documentId")
                    Toast.makeText(context, "Profile not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("СhangeProfileFragment", "Error fetching document: $exception")
                Toast.makeText(context, "Error fetching document", Toast.LENGTH_SHORT).show()
            }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")





//        val hashMapUpdate = hashMapOf<String, Any>(
//            "friendid" to friendid!!,
//            "friendsimage" to imageUrl.value!!,
//            "time" to Utils.getTime(),
//            "name" to name.value!!,
//            "sender" to getUiLoggedId(),
//            "message" to message.value!!,
//            "person" to name.value!!,
//            "status" to status.value!!
//        )
//        // updating the chatlist and recent list message, image etc
//
//        firestore.collection("Conversation${friendid}").document(getUiLoggedId())
//            .update(hashMapUpdate)
//
//        firestore.collection("Conversation${getUiLoggedId()}").document(friendid!!)
//            .update("person", "you")


    }

    fun sendFile(uiLoggedId: String, userId: String, name: String, imageUrl: String) {

    }
}