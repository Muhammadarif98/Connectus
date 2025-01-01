package com.example.connectus.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val user_id: String? = "",
    val name: String? = "",
    val lastName: String? = "",
    val phone: String? = "",
    val email: String? = "",
    val password: String? = "",
    val confirmPassword: String? = "",
    val imageUrl: String? = "",
    val status: String? = ""
): java.io.Serializable

