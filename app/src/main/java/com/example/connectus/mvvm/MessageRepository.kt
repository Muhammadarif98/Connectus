package com.example.connectus.mvvm

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.Utils.Companion.supabase
import com.example.connectus.data.model.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.launch
import java.util.UUID

class MessageRepository(private val lifecycleOwner: LifecycleOwner) {

    private val firestore = FirebaseFirestore.getInstance()

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

    // Удаление сообщения из Firestore
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

    // Старый код для загрузки файла (закомментирован)
    /*
    fun uploadFile(
        context: Context,
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                // Логирование начала загрузки
                Log.d("UploadFile", "Начало загрузки файла: $fileUri")
                Toast.makeText(context, "Загрузка файла...", Toast.LENGTH_SHORT).show()

                // Чтение файла в ByteArray
                val byteArray = context.contentResolver.openInputStream(fileUri)?.use { it.readBytes() }
                    ?: run {
                        val errorMessage = "Не удалось открыть файл: InputStream равен null"
                        Log.e("UploadFile", errorMessage)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        onError(IllegalArgumentException(errorMessage))
                        return@launch
                    }

                // Логирование успешного чтения файла
                Log.d("UploadFile", "Файл успешно прочитан в ByteArray")
                Toast.makeText(context, "Файл прочитан успешно!", Toast.LENGTH_SHORT).show()

                // Определение MIME-типа файла
                val fileType = context.contentResolver.getType(fileUri) ?: getFileTypeFromUri(fileUri)
                Log.d("UploadFile", "Определен MIME-тип файла: $fileType")

                // Генерация уникального имени файла
                val fileName = "files/${UUID.randomUUID()}"
                Log.d("UploadFile", "Сгенерировано имя файла: $fileName")

                // Загрузка файла на Supabase
                val bucket = supabase.storage.from("connectus")
                Log.d("UploadFile", "Начало загрузки файла на Supabase...")
                Toast.makeText(context, "Загрузка на сервер...", Toast.LENGTH_SHORT).show()

                bucket.upload(
                    path = fileName,
                    data = byteArray,
                    options = {
                        contentType = ContentType.parse(fileType)
                        upsert = true // Перезаписать файл, если он уже существует
                    }
                )

                // Получаем публичный URL файла
                val fileUrl = bucket.publicUrl(fileName)
                Log.d("UploadFile", "Файл успешно загружен. URL: $fileUrl")
                Toast.makeText(context, "Файл загружен успешно!", Toast.LENGTH_SHORT).show()

                // Возвращаем URL через колбэк
                onSuccess(fileUrl)
            } catch (e: Exception) {
                // Логирование ошибки
                Log.e("UploadFile", "Ошибка загрузки файла", e)
                Toast.makeText(context, "Ошибка загрузки файла: ${e.message}", Toast.LENGTH_SHORT).show()

                // Возвращаем ошибку через колбэк
                onError(e)
            }
        }
    }
    */

    // Новый код для загрузки изображения
    fun uploadImage(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                // Логирование начала загрузки
                Log.d("UploadImage", "Начало загрузки изображения: $imageUri")
                Toast.makeText(context, "Загрузка изображения...", Toast.LENGTH_SHORT).show()

                // Чтение изображения в ByteArray
                val byteArray =
                    context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                        ?: run {
                            val errorMessage =
                                "Не удалось открыть изображение: InputStream равен null"
                            Log.e("UploadImage", errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            onError(IllegalArgumentException(errorMessage))
                            return@launch
                        }

                // Логирование успешного чтения изображения
                Log.d("UploadImage", "Изображение успешно прочитано в ByteArray")
                Toast.makeText(context, "Изображение прочитано успешно!", Toast.LENGTH_SHORT).show()

                // Генерация уникального имени файла
                val fileName = "images/${UUID.randomUUID()}.jpg" // Сохраняем как JPG
                Log.d("UploadImage", "Сгенерировано имя файла: $fileName")

                // Загрузка изображения на Supabase
                val bucket = supabase.storage.from("connectus")
                Log.d("UploadImage", "Начало загрузки изображения на Supabase...")
                Toast.makeText(context, "Загрузка на сервер...", Toast.LENGTH_SHORT).show()

                bucket.upload(
                    path = fileName,
                    data = byteArray,
                    options = {
                        contentType = ContentType.Image.JPEG // Указываем тип контента как JPEG
                        upsert = true // Перезаписать файл, если он уже существует
                    }
                )

                // Получаем публичный URL изображения
                val imageUrl = bucket.publicUrl(fileName)
                Log.d("UploadImage", "Изображение успешно загружено. URL: $imageUrl")
                Toast.makeText(context, "Изображение загружено успешно!", Toast.LENGTH_SHORT).show()

                // Возвращаем URL через колбэк
                onSuccess(imageUrl)
            } catch (e: Exception) {
                // Логирование ошибки
                Log.e("UploadImage", "Ошибка загрузки изображения", e)
                Toast.makeText(
                    context,
                    "Ошибка загрузки изображения: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                // Возвращаем ошибку через колбэк
                onError(e)
            }
        }
    }

    // Отправка сообщения с файлом
    fun sendMessageWithFile(message: Messages) {
        val uniqueId =
            listOf(message.sender ?: "", message.receiver ?: "").sorted().joinToString("")
        if (uniqueId.isEmpty()) {
            Log.e("FirestoreError", "Sender или Receiver равны null")
            return
        }
        firestore.collection("Messages")
            .document(uniqueId)
            .collection("chats")
            .add(message)
            .addOnSuccessListener {
                Log.d("Firestore", "Сообщение с файлом отправлено")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Ошибка отправки сообщения с файлом", e)
            }
    }
}