package com.example.connectus.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connectus.R
import com.example.connectus.data.model.Messages
import com.example.connectus.databinding.FragmentChatBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.MessageAdapter
import de.hdodenhof.circleimageview.CircleImageView


class ChatsFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var args : ChatsFragmentArgs

    private lateinit var chatAppViewModel : ChatAppViewModel
    private lateinit var toolbar: Toolbar
    lateinit var adapter : MessageAdapter
    private lateinit var circleImageView: CircleImageView
    private lateinit var textViewName : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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




    }

    private fun initRecyclerView(list: List<Messages>) {


        adapter = MessageAdapter()

        val layoutManager = LinearLayoutManager(context)

//        binding.messagesRecyclerView.layoutManager = layoutManager
//        layoutManager.stackFromEnd = true
//
//        adapter.setList(list)
//        adapter.notifyDataSetChanged()
//        binding.messagesRecyclerView.adapter = adapter



    }
}