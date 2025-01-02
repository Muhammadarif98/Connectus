package com.example.connectus.mvvm

import Utils.Companion.getUiLoggedId
import Utils.Companion.supabase
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.data.model.Messages
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageRepo {

    //val firestore = FirebaseFirestore.getInstance()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun getMessages(friendid: String): LiveData<List<Messages>> {
        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(getUiLoggedId(), friendid).sorted().joinToString(separator = "")

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val messageResponse = supabase.from("Messages")
                    .select(Columns.ALL){
                        filter {
                            eq("chatroom_id", uniqueId)

                        }
                    }


                if (messageResponse.data != null) {
                    val messagesList = messageResponse.data.map {
                        Messages(
                            chatroom_id = "chatroom_id",
                            message_id = "message_id",
                            sender = "sender",
                            receiver = "receiver",
                            message = "message",
                            time = "time"
                        )
                    }.filter { message ->
                        (message.sender == getUiLoggedId() && message.receiver == friendid) ||
                                (message.sender == friendid && message.receiver == getUiLoggedId())
                    }

                    messages.postValue(messagesList)
                }
            } catch (e: Exception) {
                Log.e("MessageRepo", "Exception: ${e.message}")
            }
        }

        return messages
    }



  /*  fun getMessages(friendid: String): LiveData<List<Messages>> {

        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(getUiLoggedId(), friendid).sorted()
        uniqueId.joinToString(separator = "")

        firestore.collection("Messages").document(uniqueId.toString()).collection("chats").orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val messagesList = mutableListOf<Messages>()
                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Messages::class.java)
                        if (messageModel!!.sender.equals(getUiLoggedId()) && messageModel.receiver.equals(
                                friendid
                            ) ||
                            messageModel.sender.equals(friendid) && messageModel.receiver.equals(
                                getUiLoggedId()
                            )
                        ) {
                            messageModel.let {
                                messagesList.add(it!!)
                            }
                        }
                    }
                    messages.value = messagesList
                }
            }
        return messages
    }*/
}