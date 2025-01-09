package com.example.connectus.ui.adapter.message

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.connectus.R
import com.example.connectus.Utils.Companion.MESSAGE_LEFT
import com.example.connectus.Utils.Companion.MESSAGE_RIGHT
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MessageAdapter(private val onItemClick: (Messages, Boolean) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    private var listOfMessage = listOf<Messages>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MESSAGE_RIGHT -> {
                val view = inflater.inflate(R.layout.chatitemright, parent, false)
                MessageHolder(view)
            }
            MESSAGE_LEFT -> {
                val view = inflater.inflate(R.layout.chatitemleft, parent, false)
                MessageHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() = listOfMessage.size

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        when (message.fileType) {
            "image" -> {
                // Показываем изображение
                holder.messageText.visibility = View.GONE
                holder.fileView.visibility = View.VISIBLE
                holder.cardView.visibility = View.VISIBLE
                holder.downloadButton.visibility = View.VISIBLE

                // Загружаем изображение с помощью Glide
                Glide.with(holder.itemView.context)
                    .load(message.fileUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_prof)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Glide", "Failed to load image: ${e?.message}", e)
                            holder.itemView.post {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Failed to load image: ${e?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("Glide", "Image loaded successfully")
                            return false
                        }
                    })
                    .into(holder.fileView)
                holder.downloadButton.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        message.fileUrl?.let { it1 ->
                            downloadFileFromSupabase(
                                context = holder.itemView.context,
                                fileUrl = it1,
                                fileName = message.message ?: "image.jpg",
                                onSuccess = {
                                    Log.d("Download", "Файл успешно скачан")
                                },
                                onError = { e ->
                                    Log.e("Download", "Ошибка скачивания файла", e)
                                }
                            )
                        }
                    }
                }
                holder.itemView.setOnClickListener {
                    onItemClick(message, true) // Передаем true, если это изображение
                }
                Log.d("GlideImage", "Image URL: ${message.fileUrl}")
            }

            // Остальные типы файлов (аудио, видео, документы) остаются без изменений
            else -> {
                // Показываем текстовое сообщение
                holder.messageText.visibility = View.VISIBLE
                holder.fileView.visibility = View.GONE
                holder.cardView.visibility = View.GONE
                holder.downloadButton.visibility = View.GONE
                holder.messageText.text = message.message

                holder.itemView.setOnClickListener {
                    onItemClick(message, false) // Передаем false, если это текстовое сообщение
                }
            }
        }

        holder.timeOfSent.text = message.time ?: ""
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfMessage[position].sender == getUidLoggedIn()) MESSAGE_RIGHT else MESSAGE_LEFT
    }

    fun setList(newList: List<Messages>) {
        val diffUtil = MessagesDiffUtil(listOfMessage, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        listOfMessage = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(position: Int) {
        val newList = listOfMessage.toMutableList()
        newList.removeAt(position)
        setList(newList)
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.show_message)
        val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
        val fileView: ImageView = itemView.findViewById(R.id.fileView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
    }
}

// Метод для скачивания файла с помощью MediaStore
@RequiresApi(Build.VERSION_CODES.Q)
suspend fun downloadFileFromSupabase(
    context: Context,
    fileUrl: String,
    fileName: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = withContext(Dispatchers.IO) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        }

        if (uri != null) {
            withContext(Dispatchers.IO) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    val inputStream = URL(fileUrl).openStream()
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    inputStream.close()
                    outputStream.close()
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Файл успешно скачан: $fileName", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        } else {
            throw Exception("Не удалось создать Uri для загрузки файла")
        }
    } catch (e: Exception) {
        Log.e("DownloadError", "Ошибка скачивания файла", e)
        withContext(Dispatchers.Main) {
            onError(e)
        }
    }
}
