package com.example.connectus.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectus.App
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils
import com.example.connectus.Utils.Companion.getUidFriendIn
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages
import com.example.connectus.data.model.RecentChats
import com.example.connectus.data.model.Users
import com.example.connectus.notifications.entity.NotificationData
import com.example.connectus.notifications.entity.PushNotification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.Token
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("StaticFieldLeak")
class ChatAppViewModel() : ViewModel() {

    private val context: Context = App.instance.applicationContext
    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages


    val id = MutableLiveData<String?>()
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

    val loadImageUrl = MutableLiveData<String?>()


    val friendId = MutableLiveData<String?>()
    val friendName = MutableLiveData<String?>()
    val friendImageUrl = MutableLiveData<String?>()
    val friendLastName = MutableLiveData<String?>()
    val friendPhone = MutableLiveData<String?>()
    val friendEmail = MutableLiveData<String?>()
    val friendPassword = MutableLiveData<String?>()
    val friendConfirmPassword = MutableLiveData<String?>()
    val friendStatus = MutableLiveData<String?>()
    val friendAdress = MutableLiveData<String?>()
    val friendAge = MutableLiveData<String?>()
    val friendEmployee = MutableLiveData<String?>()

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
        getFriendUser()
        getCurrentUser()
        getRecentUsers()
    }

    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friend)
    }
    fun deleteMessage(uniqueId: String, messageId: String) {
        messageRepo.deleteMessage(uniqueId, messageId) {
            // После успешного удаления обновляем данные
            refreshMessages(uniqueId.split("").sorted().joinToString(""))
        }
    }

    fun refreshMessages(friendId: String) {
        // Обновляем LiveData, вызывая getMessages
        _messages.value = messageRepo.getMessages(friendId).value
    }


    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatlistRepo.getAllChatList()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext

        firestore.collection("users").document(getUidLoggedIn())
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
                    val userAdress = value.getString("adress")
                    val userAge = value.getString("age")
                    val userEmployee = value.getString("employee")

                    id.postValue(userId)
                    name.postValue(userName)
                    imageUrl.postValue(userImageUrl)
                    lastName.postValue(userLastName)
                    phone.postValue(userPhone)
                    email.postValue(userEmail)
                    password.postValue(userPassword)
                    confirmPassword.postValue(userConfirmPassword)
                    adress.postValue(userAdress)
                    age.postValue(userAge)
                    employee.postValue(userEmployee)
                    this@ChatAppViewModel.status.postValue(status)

                    val mySharedPrefs = SharedPrefs(context)
                    if (userId != null) {
                        mySharedPrefs.setValue("userId", userId)
                    }
                    if (userPhone != null) {
                        mySharedPrefs.setValue("phone", userPhone)
                    }
                    if (userName != null) {
                        mySharedPrefs.setValue("name", userName)
                    }
                    if (userImageUrl != null) {
                        mySharedPrefs.setValue("imageUrl", userImageUrl)
                    }
                    if (userEmail != null) {
                        mySharedPrefs.setValue("email", userEmail)
                    }
                    if (userAdress != null) {
                        mySharedPrefs.setValue("adress", userAdress)
                    }
                    if (userAge != null) {
                        mySharedPrefs.setValue("age", userAge)
                    }
                    if (userEmployee != null) {
                        mySharedPrefs.setValue("employee", userEmployee)
                    }

                }
            }
    }

    fun getFriendUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext
        firestore.collection("users").document(getUidFriendIn())
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
                    val userAdress = value.getString("adress")
                    val userAge = value.getString("age")
                    val userEmployee = value.getString("employee")

                    friendId.postValue(userId)
                    friendName.postValue(userName)
                    friendImageUrl.postValue(userImageUrl)
                    friendLastName.postValue(userLastName)
                    friendPhone.postValue(userPhone)
                    friendEmail.postValue(userEmail)
                    friendPassword.postValue(userPassword)
                    friendConfirmPassword.postValue(userConfirmPassword)
                    friendStatus.postValue(status)
                    friendAdress.postValue(userAdress)
                    friendAge.postValue(userAge)
                    friendEmployee.postValue(userEmployee)

                    val mySharedPrefs = SharedPrefs(context)

                    if (userId != null) {
                        mySharedPrefs.setValue("friendId", userId)
                    }
                    if (userPhone != null) {
                        mySharedPrefs.setValue("friendPhone", userPhone)
                    }
                    if (userName != null) {
                        mySharedPrefs.setValue("friendName", userName)
                    }
                    if (userImageUrl != null) {
                        mySharedPrefs.setValue("friendImageUrl", userImageUrl)
                    }
                    if (userEmail != null) {
                        mySharedPrefs.setValue("friendEmail", userEmail)
                    }

                }
            }
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext

        val hashMap = hashMapOf<String, Any>(
            "userId" to getUidLoggedIn(),
            "name" to name.value!!,
            "lastName" to lastName.value!!,
            "phone" to phone.value!!,
            "email" to email.value!!,
            "password" to password.value!!,
            "confirmPassword" to confirmPassword.value!!,
            "image" to imageUrl.value!!,
            "status" to status.value!!,
            "adress" to adress.value!!,
            "age" to age.value!!,
            "employee" to employee.value!!
        )
        firestore.collection("users").document(getUidLoggedIn()).update(hashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Обновлено", Toast.LENGTH_SHORT).show()
                    Log.d("ProfileUpdate", "Имя успешно обновлено!")
                } else {
                    Log.e("ProfileUpdate", "Не удалось обновить имя: ${task.exception?.message}")
                }
            }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>(
            "friendsid" to friendId.value!!,
            "friendsimage" to imageUrl.value!!,
            "name" to name.value!!,
            "lastname" to lastName.value!!,
            "phone" to phone.value!!,
            "email" to email.value!!,
            "time" to Utils.getTime(),
            "status" to status.value!!,
            "friendAdress" to friendAdress.value!!,
            "friendAge" to friendAge.value!!,
            "friendEmployee" to friendEmployee.value!!,
            "sender" to "you",
            "person" to name.value!!
        )


        // updating the chatlist and recent list message, image etc

        firestore.collection("Conversation${friendid}").document(getUidLoggedIn())
            .update(hashMapUpdate)

        firestore.collection("Conversation${getUidLoggedIn()}").document(friendid!!)
            .update("person", "you")


    }

    fun sendMessage(
        sender: String,
        receiver: String,
        friendname: String,
        friendimage: String,
        friendemail: String,
        friendlastname: String,
        friendphone: String,
        friendAdress: String,
        friendAge: String,
        friendEmployee: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val context = App.instance.applicationContext

        // Генерация рандомного ID для сообщения
        val messageId = UUID.randomUUID().toString()

        // Данные сообщения
        val hashMap = hashMapOf<String, Any>(
            "id" to messageId, // Добавляем рандомный ID
            "sender" to sender,
            "receiver" to receiver,
            "message" to message.value!!,
            "time" to Utils.getTime()
        )

        // Генерация уникального ID для чата
        val uniqueId = listOf(sender, receiver).sorted().joinToString(separator = "")

        // Сохранение данных в SharedPreferences
        val friendnamesplit = friendname.split("\\s".toRegex())[0]
        val mysharedPrefs = SharedPrefs(context)
        mysharedPrefs.setValue("friendid", receiver)
        mysharedPrefs.setValue("chatroomid", uniqueId)
        mysharedPrefs.setValue("friendname", friendnamesplit)
        mysharedPrefs.setValue("friendimage", friendimage)
        mysharedPrefs.setValue("friendemail", friendemail)
        mysharedPrefs.setValue("friendphone", friendphone)
        mysharedPrefs.setValue("friendlastname", friendlastname)
        mysharedPrefs.setValue("friendAdress", friendAdress)
        mysharedPrefs.setValue("friendAge", friendAge)
        mysharedPrefs.setValue("friendEmployee", friendEmployee)

        // Получение значений из SharedPreferences
        val adr = mysharedPrefs.getValue("friendAdress")
        val ageF = mysharedPrefs.getValue("friendAge")
        val emp = mysharedPrefs.getValue("friendEmployee")

        // Сохранение сообщения в Firestore
        firestore.collection("Messages")
            .document(uniqueId)
            .collection("chats")
            .document(messageId) // Используем рандомный ID как document ID
            .set(hashMap)
            .addOnCompleteListener { taskmessage ->
                if (taskmessage.isSuccessful) {
                    // Очистка поля сообщения после успешной отправки


                    // Обновление данных в коллекции Conversation
                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "email" to friendemail,
                        "phone" to friendphone,
                        "lastname" to friendlastname,
                        "status" to status.value!!,
                        "friendAdress" to adr!!,
                        "friendAge" to ageF!!,
                        "friendEmployee" to emp!!,
                        "person" to "Вы"
                    )

                    Log.d("setHashap", "Sending data: $setHashap")

                    // Обновление данных в коллекции Conversation
                    firestore.collection("Conversation${getUidLoggedIn()}")
                        .document(receiver)
                        .set(setHashap)

                    firestore.collection("Conversation${receiver}")
                        .document(getUidLoggedIn())
                        .update(
                            "message", message.value!!,
                            "time", Utils.getTime(),
                            "person", name.value!!,
                            "friendsimage", friendImageUrl.value!!,
                            "name", friendName.value!!,
                            "email", friendEmail.value!!,
                            "phone", friendPhone.value!!,
                            "password", friendPassword.value!!,
                            "confirmPassword", friendConfirmPassword.value!!,
                            "lastname", friendLastName.value!!,
                            "friendAdress", adr,
                            "friendAge", ageF,
                            "friendEmployee", emp
                        )
                    message.value = ""
                    // Отправка уведомления
                    firestore.collection("Tokens")
                        .document(receiver)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                val loggedInUsername = mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]
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
                        }
                } else {
                    Log.e("ChatAppViewModel", "Ошибка отправки сообщения", taskmessage.exception)
                }
            }
    }


    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            // val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {

            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }


}

/*
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
    val loadImageUrl = MutableLiveData<String?>()
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
    val type = MutableLiveData<String>()
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
                "time" to Utils.getTime(),
               // "type" to "text"
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
                            "message", message.value!!,
                            "time", Utils.getTime(),
                            "person", name.value!!,
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
                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION $error")
                                }
                            }
                            Log.e("ViewModel", token.toString())
                            if (taskmessage.isSuccessful) {
                                Log.d("SendMessage", "Message sent successfully")
                                message.value = ""
                            }

                        }
                }
        }

    fun sendFile(sender: String, receiver: String, friendName: String, friendImage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = App.instance.applicationContext
            val imageUrlValue = loadImageUrl.value
            if (imageUrlValue.isNullOrEmpty()) {
                Log.e("SendFile", "Image URL is empty!")
                return@launch
            }

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to imageUrlValue,  // Используем URL изображения в качестве сообщения
                "time" to Utils.getTime(),
                "type" to "image"  // Добавляем тип сообщения
            )
            Log.d("SendFile", "hashMap: $hashMap")  // Лог для проверки

            val uniqueId = listOf(sender, receiver).sorted().joinToString("")

            firestore.collection("Messages").document(uniqueId).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskMessage ->
                    if (taskMessage.isSuccessful) {
                        Log.d("SendFile", "Image message sent successfully: ${loadImageUrl.value}")
                        loadImageUrl.postValue("")  // Очищаем URL изображения после отправки
                    } else {
                        Log.e("SendFile", "Failed to send file: ${taskMessage.exception?.message}")
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


}
*/