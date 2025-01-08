package com.example.connectus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.connectus.R
import com.example.connectus.Utils.Companion.MESSAGE_LEFT
import com.example.connectus.Utils.Companion.MESSAGE_RIGHT
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages

class MessageAdapter(private val onItemClick: (Messages) -> Unit) : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == MESSAGE_RIGHT) {
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time ?: ""

        // Разрешить удаление только для правых айтемов
        if (getItemViewType(position) == MESSAGE_RIGHT) {
            holder.itemView.setOnClickListener {
                onItemClick(message)
            }
        } else {
            holder.itemView.setOnClickListener(null) // Отключить клик для левых айтемов
        }
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == getUidLoggedIn()) MESSAGE_RIGHT else MESSAGE_LEFT

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
}
class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
}