package com.example.connectus.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.connectus.data.model.RecentChats


class ChatListRepo() {


   // val firestore = FirebaseFirestore.getInstance()


    fun getAllChatList(): LiveData<List<RecentChats>> {

        val mainChatList = MutableLiveData<List<RecentChats>>()


     /*   // SHOWING THE RECENT MESSAGED PERSON ON TOP
        firestore.collection("Conversation${getUiLoggedId()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->


                if (exception != null) {

                    return@addSnapshotListener
                }


                val chatlist = mutableListOf<RecentChats>()

                snapshot?.forEach { document ->

                    val chatlistmodel = document.toObject(RecentChats::class.java)


                    if (chatlistmodel.sender.equals(getUiLoggedId())) {

                        chatlistmodel.let {

                            chatlist.add(it)

                        }

                    }
                }
                mainChatList.value = chatlist

            }
*/
        return mainChatList

    }
}