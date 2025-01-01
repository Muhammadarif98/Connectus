package com.example.connectus.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecentChats(
    val friendid: String? = "",
    val friendsimage: String? = "",
    val time: String? = "",
    val name: String? = "",
    val sender: String? = "",
    val message: String? = "",
    val person: String? = "",
    val status: String? = "",
) : java.io.Serializable