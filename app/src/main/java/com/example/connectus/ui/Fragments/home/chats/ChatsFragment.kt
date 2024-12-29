package com.example.connectus.ui.Fragments.home.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

   /* private lateinit var args : ChatsFragmentArgs
    private lateinit var chatAppViewModel : ChatAppViewModel
    private lateinit var toolbar: Toolbar
    lateinit var adapter : MessageAdapter
    private lateinit var circleImageView: CircleImageView
    private lateinit var textViewName : TextView
    */
    private var navigationListener: onChatClicked? = null
    private lateinit var chatsRecyclerView: RecyclerView
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
        chatsRecyclerView= view.findViewById(R.id.rvRecentChats)
        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        chatsRecyclerView.layoutManager = layoutManagerUsers

        chatViewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
            chatAdapter.setList(it)
            chatAdapter.setOnChatClickListener(this)
            chatsRecyclerView.adapter = chatAdapter
        })


    }

    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        navigationListener?.getOnChatCLickedItem(position, chatList)
    }
}

//        toolbar = view.findViewById(R.id.toolBarChat)
//        circleImageView = toolbar.findViewById(R.id.chatImageViewUser)
//        textViewName = toolbar.findViewById(R.id.chatUserName)
//
//
//        args = ChatsFragmentArgs.fromBundle(requireArguments())
//
//        chatAppViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
//
//        binding.viewModel = chatAppViewModel
//        binding.lifecycleOwner = viewLifecycleOwner
//        Glide.with(view.getContext())
//            .load(args.recentchats.friendsimage!!)
//            .placeholder(R.drawable.ic_profile)
//            .dontAnimate().into(circleImageView);
//        textViewName.setText(args.recentchats.name)
        //textViewStatus.setText(args.users.status)





//        binding.sendBtn.setOnClickListener {
//
//            chatAppViewModel.sendMessage(getUiLoggedId(), args.recentchats.friendid!!, args.recentchats.name!!, args.recentchats.friendsimage!!)
//        }


//        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
//
//            initRecyclerView(it)
//
//        })





//    private fun initRecyclerView(list: List<Messages>) {
//
//
//       // adapter = MessageAdapter()
//
//      //  val layoutManager = LinearLayoutManager(context)
//
////        binding.messagesRecyclerView.layoutManager = layoutManager
////        layoutManager.stackFromEnd = true
////
////        adapter.setList(list)
////        adapter.notifyDataSetChanged()
////        binding.messagesRecyclerView.adapter = adapter
//
//
//
//    }

