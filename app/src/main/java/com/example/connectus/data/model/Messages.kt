package com.example.connectus.data.model

data class Messages(
    val id: String = "",
    val sender: String? = "",
    val receiver: String? = "",
    val message: String? = "", // Текст сообщения
    val fileUrl: String? = "", // URL файла
    val fileType: String? = "", // Тип файла (image, audio, document и т.д.)
    val time: String? = ""
)