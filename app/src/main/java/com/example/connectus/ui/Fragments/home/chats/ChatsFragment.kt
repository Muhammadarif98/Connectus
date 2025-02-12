package com.example.connectus.ui.Fragments.home.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectus.R
import com.example.connectus.data.model.RecentChats
import com.example.connectus.databinding.FragmentChatBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.RecentChatAdapter
import com.example.connectus.ui.adapter.onChatClicked

class ChatsFragment : Fragment(), onChatClicked {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var navigationListener: onChatClicked? = null
    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var chatAdapter: RecentChatAdapter
    private lateinit var chatViewModel: ChatAppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationListener = parentFragment as? onChatClicked
        chatViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        chatAdapter = RecentChatAdapter()
        chatsRecyclerView = view.findViewById(R.id.rvRecentChats)
        emptyView = view.findViewById(R.id.empty_view)
        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        chatsRecyclerView.layoutManager = layoutManagerUsers

        chatViewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
            chatAdapter.setList(it)
            chatAdapter.setOnChatClickListener(this)
            chatsRecyclerView.adapter = chatAdapter

            if (it.isEmpty()) {
                chatsRecyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                chatsRecyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })
    }

    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        navigationListener?.getOnChatCLickedItem(position, chatList)
    }
}
