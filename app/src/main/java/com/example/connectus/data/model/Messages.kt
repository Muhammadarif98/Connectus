package com.example.connectus.data.model

data class Messages(
    val chatroom_id: String? = "",
    val message_id: String? = "",
    val sender : String? = "",
    val receiver: String? = "",
    val message: String? = "",
    val time: String? = "",

    ) {
    val id : String get() = "$sender-$receiver-$message-$time"
}