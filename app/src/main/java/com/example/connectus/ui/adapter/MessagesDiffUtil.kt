package com.example.connectus.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.connectus.data.model.Messages

class MessagesDiffUtil(
    private val oldList: List<Messages>,
    private val newList: List<Messages>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}