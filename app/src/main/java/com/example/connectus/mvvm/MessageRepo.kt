package com.example.connectus.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessageRepo {

    val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendid: String): LiveData<List<Messages>> {
        val messages = MutableLiveData<List<Messages>>()
        val uniqueId = listOf(getUidLoggedIn(), friendid).sorted().joinToString(separator = "")

        firestore.collection("Messages")
            .document(uniqueId)
            .collection("chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("FirestoreError", "Ошибка при загрузке сообщений", exception)
                    return@addSnapshotListener
                }

                val messagesList = mutableListOf<Messages>()
                if (!snapshot!!.isEmpty) {
                    snapshot.documents.forEach { document ->
                        val messageModel = document.toObject(Messages::class.java)
                        messageModel?.let {
                            messagesList.add(it.copy(id = document.id)) // Сохраняем ID документа
                        }
                    }
                    messages.value = messagesList
                }
            }
        return messages
    }


    fun deleteMessage(uniqueId: String, messageId: String, onSuccess: () -> Unit) {
        firestore.collection("Messages")
            .document(uniqueId)
            .collection("chats")
            .document(messageId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Сообщение удалено")
                onSuccess() // Вызываем колбэк после успешного удаления
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка при удалении сообщения", e)
            }
    }
}