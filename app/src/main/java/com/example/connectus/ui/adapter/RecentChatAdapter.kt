package com.example.connectus.ui.adapter



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.data.model.RecentChats
import com.example.connectus.data.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<MyChatListHolder>() {

    var listOfChats = listOf<RecentChats>()
    var listOfUsers = listOf<Users>()
    private var listener: onChatClicked? = null
    var chatShitModal = RecentChats()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return MyChatListHolder(view)


    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {

        val chatlist = listOfChats[position]


        chatShitModal = chatlist


        holder.userName.text = chatlist.name


        val themessage = chatlist.message!!.split(" ").take(4).joinToString(" ")
        val makelastmessage = "${chatlist.person}: ${themessage} "

        holder.lastMessage.text = makelastmessage

        Glide.with(holder.itemView.context).load(chatlist.friendsimage).into(holder.imageView)

        holder.timeView.text = chatlist.time!!

        holder.itemView.setOnClickListener {
            listener?.getOnChatCLickedItem(position, chatlist)


        }


    }

    fun setList(list: List<RecentChats>) {
        this.listOfChats = list

    }
//    fun setListUser(list: List<Users>) {
//        this.listOfUsers = list
//
//    }
    fun setOnChatClickListener(listener: onChatClicked) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)


}


interface onChatClicked {
    fun getOnChatCLickedItem(position: Int, chatList: RecentChats)
}