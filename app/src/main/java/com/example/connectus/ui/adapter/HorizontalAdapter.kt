package com.example.connectus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectus.R

class HorizontalAdapter(
    private val items: Array<String>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)

        init {
            view.setOnClickListener {
                clickListener(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.icon.setImageResource(R.drawable.ic_camera)
                holder.title.text = "КАМЕРЫ"
            }

            1 -> {
                holder.icon.setImageResource(R.drawable.ic_gallery)
                holder.title.text = "ГАЛЕРЕИ"
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
