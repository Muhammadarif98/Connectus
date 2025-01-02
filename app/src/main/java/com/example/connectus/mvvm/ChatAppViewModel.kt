package com.example.connectus.mvvm

import Utils
import Utils.Companion.getUiLoggedId
import Utils.Companion.supabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectus.App
import com.example.connectus.SharedPrefs
import com.example.connectus.data.model.Messages
import com.example.connectus.data.model.RecentChats
import com.example.connectus.data.model.Users
import com.example.connectus.notifications.entity.NotificationData
import com.example.connectus.notifications.entity.PushNotification
import com.example.connectus.notifications.entity.Token
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val name = MutableLiveData<String?>()
    val imageUrl = MutableLiveData<String?>()
    val message = MutableLiveData<String>()
    private var token: String? = null

    //   private val firestore = FirebaseFirestore.getInstance()
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
        try {
            val userId = getUiLoggedId()
            val response = supabase.postgrest["users"].select(Columns.ALL) {
                filter {
                    eq("user_id", userId)
                }
            }
            val user = response.let {
                Users(
                    user_id = "user_id" as String?,
                    name = "name" as String?,
                    lastName = "lastName" as String?,
                    phone = "phone" as String?,
                    email = "email" as String?,
                    password = "password" as String?,
                    confirmPassword = "confirmPassword" as String?,
                    imageUrl = "imageUrl" as String?,
                    status = "status" as String?
                )
            }

            user.let {
                name.postValue(it.name)
                imageUrl.postValue(it.imageUrl)

                val mySharedPrefs = SharedPrefs(context)
                if (it.name != null) {
                    mySharedPrefs.setValue("name", it.name)
                }
                if (it.imageUrl != null) {
                    mySharedPrefs.setValue("imageUrl", it.imageUrl)
                }
            }
        } catch (e: Exception) {
            Log.e("ChatAppViewModel_getCurrentUser", "Exception: ${e.message}")
        }

    }

//        firestore.collection("users").document(getUiLoggedId())
//            .addSnapshotListener { value, error ->
//                if (value != null && value.exists()) {
//                    val userId = value.getString("user_id")
//                    val userName = value.getString("name")
//                    val userLastName = value.getString("lastName")
//                    val userPhone = value.getString("phone")
//                    val userEmail = value.getString("email")
//                    val userPassword = value.getString("password")
//                    val userConfirmPassword = value.getString("confirmPassword")
//                    val userImageUrl = value.getString("image")
//                    val status = value.getString("status")
//
//                    name.postValue(userName)
//                    imageUrl.postValue(userImageUrl)
//
//                    val mySharedPrefs = SharedPrefs(context)
//                    if (userName != null) {
//                        mySharedPrefs.setValue("name", userName)
//                    }
//                    if (userImageUrl != null) {
//                        mySharedPrefs.setValue("imageUrl", userImageUrl)
//                    }
//                }
//            }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = App.instance.applicationContext
            val currentTime = Utils.getTime()
            val messageText = message.value!!

            Log.d("seM", "Sender: $sender, Receiver: $receiver, Friendname: $friendname, Friendimage: $friendimage, MessageText: $messageText, CurrentTime: $currentTime")

            val uniqueId = listOf(sender, receiver).sorted().joinToString(separator = "")
            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mySharedPrefs = SharedPrefs(context)
            mySharedPrefs.setValue("friendid", receiver)
            mySharedPrefs.setValue("chatroomid", uniqueId)
            mySharedPrefs.setValue("friendname", friendnamesplit)
            mySharedPrefs.setValue("friendimage", friendimage)

            try {
                // Сохраняем сообщение в таблицу "Messages"
                val messageData = mapOf(
                    "chatroom_id" to uniqueId,
                    "message_id" to currentTime,
                    "sender" to sender,
                    "receiver" to receiver,
                    "message" to messageText,
                    "time" to currentTime
                )
                val messageResponse = supabase.from("Messages").insert(messageData)
                Log.d("Message", "MessageResponse: $messageResponse")

                if (messageResponse.data != null) {
                    val conversationData = mapOf(
                        "friendid" to receiver,
                        "time" to currentTime,
                        "sender" to getUiLoggedId(),
                        "message" to messageText,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    // Обновляем таблицу "Conversation" для отправителя
                    val senderConversationResponse = supabase.from("Conversationsender")
                        .upsert(conversationData) {
                            filter { eq("friendid", receiver) }
                        }
                    Log.d("sendMessage", "SenderConversationResponse: $senderConversationResponse")

                    // Обновляем таблицу "Conversation" для получателя
                    val receiverConversationData = mapOf(
                        "friendid" to getUiLoggedId(),
                        "message" to messageText,
                        "time" to currentTime,
                        "person" to name.value!!
                    )
                    val receiverConversationResponse = supabase.from("Conversationsender")
                        .upsert(receiverConversationData) {
                            filter { eq("friendid", getUiLoggedId()) }
                        }
                    Log.d("sendMessage", "ReceiverConversationResponse: $receiverConversationResponse")

                    val snapshot = supabase.from("Tokens").select() {
                        filter {
                            eq("id", receiver)
                        }
                    }
                    Log.d("sendM", "Snapshot: $snapshot")

                    val tok = messageResponse.let {
                        Token(
                            token = "token" as String?
                        )
                    }
                    val token = tok.token!!
                    val loggedInUsername =
                        mySharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                    if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                        PushNotification(
                            NotificationData(loggedInUsername, message.value!!),
                            token
                        ).also {
                            // sendNotification(it)
                        }
                        Log.d("sendMessage", "PushNotification sent")
                    } else {
                        Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                    }

                    Log.d("ViewModel", token.toString())
                    if (messageResponse.data != null) {
                        message.value = ""
                    }
                    message.value = ""
                    if (senderConversationResponse.data == null || receiverConversationResponse.data == null) {
                        Log.e(
                            "ChatAppViewModel",
                            "Ошибка обновления данных разговора ${senderConversationResponse.data}, ${receiverConversationResponse.data}"
                        )
                    }

                } else {
                    Log.e("ChatAppViewModel", "Ошибка отправки сообщения: ${messageResponse.data}")
                }
            } catch (e: Exception) {
                Log.e("ChatAppViewModel", "Exception: ${e.message}")
            }
        }


    /* fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
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
                 }

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
  }*/


    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friend)
    }


    fun getRecentUsers(): LiveData<List<RecentChats>> {

        return chatlistRepo.getAllChatList()

    }



    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
            try {
               // val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {

                Log.e("ViewModelError", e.toString())
                 //showToast(e.message.toString())
            }
      }


    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = App.instance.applicationContext

        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        /*  firestore.collection("Users").document(getUiLoggedId()).update(hashMapUser)
              .addOnCompleteListener {

                  if (it.isSuccessful) {

                      Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()


                  }

              }
  */

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>(
            "friendsimage" to imageUrl.value!!,
            "name" to name.value!!,
            "person" to name.value!!
        )


        // updating the chatlist and recent list message, image etc

        /* firestore.collection("Conversation${friendid}").document(getUiLoggedId())
              .update(hashMapUpdate)

          firestore.collection("Conversation${getUiLoggedId()}").document(friendid!!)
              .update("person", "you")

  */
    }
}
