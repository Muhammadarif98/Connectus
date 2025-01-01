package com.example.connectus.mvvm

import Utils.Companion.getUiLoggedId
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.data.model.Messages

class MessageRepo {

    //val firestore = FirebaseFirestore.getInstance()




    fun getMessages(friendid: String): LiveData<List<Messages>> {

        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(getUiLoggedId(), friendid).sorted()
        uniqueId.joinToString(separator = "")




       /* firestore.collection("Messages").document(uniqueId.toString()).collection("chats").orderBy("time", Query.Direction.ASCENDING)
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
*/
        return messages


    }



}