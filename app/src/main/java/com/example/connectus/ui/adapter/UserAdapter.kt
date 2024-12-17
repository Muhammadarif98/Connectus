package com.example.connectus.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.data.model.Users

class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<Users>()
    private var listener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listOfUsers[position]
        val name = users.userName
        val image = users.userImageUrl

        Log.d("UserAdapter", "Binding user: $name, image: $image") // Логирование данных

        holder.profileName.text = name
        Glide.with(holder.itemView.context)
            .load(image)
            .into(holder.imageProfile)

        if (users.status == "online") {
            holder.statusImageView.setImageResource(R.drawable.online)
        } else {
            holder.statusImageView.setImageResource(R.drawable.offline)
        }

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(users: List<Users>) {
        this.listOfUsers = users
        notifyDataSetChanged()
    }

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile : ImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView : ImageView = itemView.findViewById(R.id.statusOnline)
    val time : TextView = itemView.findViewById(R.id.recentUserTextTime)
}


interface OnUserClickListener {
    fun onUserSelected(position: Int, users: Users)
}