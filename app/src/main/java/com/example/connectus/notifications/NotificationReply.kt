package com.example.connectus.notifications

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.connectus.R
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils

import com.google.firebase.firestore.FirebaseFirestore

private const val CHANNEL_ID = "my_channel"

class NotificationReply : BroadcastReceiver() {

    val firestore = FirebaseFirestore.getInstance()

    override fun onReceive(context: Context?, intent: Intent?) {


        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        if (remoteInput != null) {
            val repliedText = remoteInput.getString("KEY_REPLY_TEXT")
            val mysharedPrefs = SharedPrefs(context)
            val friendid = mysharedPrefs.getValue("friendid")
            val chatroomid = mysharedPrefs.getValue("chatroomid")
            val friendname = mysharedPrefs.getValue("friendname")
            val friendimage = mysharedPrefs.getValue("friendimage")


            val hashMap = hashMapOf<String, Any>(
                "sender" to Utils.getUidLoggedIn(),
                "time" to Utils.getTime(),
                "receiver" to friendid!!,
                "message" to repliedText!!
            )

            firestore.collection("Messages").document(chatroomid!!)
                .collection("chats").document(Utils.getTime()).set(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                    }
                }

            val setHashap = hashMapOf<String, Any>(
                "friendid" to friendid,
                "time" to Utils.getTime(),
                "sender" to Utils.getUidLoggedIn(),
                "message" to repliedText,
                "friendsimage" to friendimage!!,
                "name" to friendname!!,
                "person" to "Вы",
            )

            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid)
                .set(setHashap)

            val updateHashMap =
                hashMapOf<String, Any>(
                    "message" to repliedText,
                    "time" to Utils.getTime(),
                    "person" to friendname!!
                )

            firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn())
                .update(updateHashMap)

            val sharedCustomPref = SharedPrefs(context)
            val replyid: Int = sharedCustomPref.getIntValue("values", 0)

            val repliedNotification =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_sms)
                    .setContentText("Ответили на сообщение").build()
            notificationManager.notify(replyid, repliedNotification)
        }
    }
}