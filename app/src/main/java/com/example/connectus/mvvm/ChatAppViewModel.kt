package com.example.connectus.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
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
import com.example.connectus.notifications.entity.Token
import com.example.connectus.notifications.network.RetrofitInstance
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

@SuppressLint("StaticFieldLeak")
class ChatAppViewModel @JvmOverloads constructor(
    private val lifecycleOwner: LifecycleOwner? = null
) : ViewModel() {

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
    val messageRepository = lifecycleOwner?.let { MessageRepository(it) }
    val chatlistRepository = ChatListRepository()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val usersRepository = UsersRepository()

    init {
        getFriendUser()
        getCurrentUser()
        getRecentUsers()
    }

    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepository!!.getMessages(friend)
    }

    fun deleteMessage(uniqueId: String, messageId: String) {
        messageRepository!!.deleteMessage(uniqueId, messageId) {
            // После успешного удаления обновляем данные
            refreshMessages(uniqueId.split("").sorted().joinToString(""))
        }
    }

    fun refreshMessages(friendId: String) {
        _messages.value = messageRepository!!.getMessages(friendId).value
    }


    fun getUsers(): LiveData<List<Users>> {
        return usersRepository.getUsers()
    }

    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatlistRepository.getAllChatList()
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

                    // Отправка уведомления
                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val tokenObject = value.toObject(Token::class.java)
                                token = tokenObject?.token!!
                                val loggedInUsername =
                                    mysharedPrefs.getValue("name")!!.split("\\s".toRegex())[0]
                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(loggedInUsername, message.value!!), token!!
                                    ).also {
                                        sendNotification(it)
                                        Log.d("SendViewModel", "Notification sent successfully $it")
                                    }
                                } else {
                                    Log.e("ViewModelNoToken", "NO TOKEN, NO NOTIFICATION")
                                }
                            } else {
                                Log.e(
                                    "ChatAppViewModel",
                                    "Ошибка отправки сообщения",
                                    taskmessage.exception
                                )
                            }
                            if (taskmessage.isSuccessful) {
                                message.value = ""
                            }

                        }
                }
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAGToken", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result

                    // Log and toast

                    // Log.d("TAGToken", "$token")
                    //Toast.makeText(context, "$token", Toast.LENGTH_SHORT).show()
                })
            }
    }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            Log.d("SendNotification", "Notification sent successfully: $response")
        } catch (e: CancellationException) {
            Log.e("ViewModelError", "Coroutine was cancelled: ${e.message}")
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Ошибка отправки уведомления, ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun sendImage(
        context: Context,
        sender: String,
        receiver: String,
        imageUri: Uri
    ) = viewModelScope.launch {
        messageRepository?.uploadImage(
            context,
            imageUri,
            onSuccess = { imageUrl ->
                val message = Messages(
                    sender = sender,
                    receiver = receiver,
                    fileUrl = imageUrl,
                    fileType = "image", // Указываем тип файла как изображение
                    time = Utils.getTime()
                )
                messageRepository.sendMessageWithFile(message)
                Toast.makeText(context, "Изображение отправлено", Toast.LENGTH_SHORT)
                    .show()
                Log.d("SendImage", "Image sent successfully: $imageUrl")
            },
            onError = { e ->
                Log.e("SendImageError", "Ошибка загрузки изображения", e)
                Toast.makeText(
                    context,
                    "Ошибка отправки изображения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}