package com.example.connectus.ui.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.data.model.Messages
import com.example.connectus.databinding.FragmentChatDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.MessageAdapter
import de.hdodenhof.circleimageview.CircleImageView

class ChatDialogFragment : Fragment() {
    private var _binding: FragmentChatDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: ChatDialogFragmentArgs

    private lateinit var adapter : MessageAdapter
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chatToolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var backImageView: ImageView
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatToolbar = view.findViewById(R.id.toolBarChat)

        chatAppViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]


        textView = chatToolbar.findViewById(R.id.chatUserName)
        circleImageView = chatToolbar.findViewById(R.id.chatImageViewUser)
        backImageView = chatToolbar.findViewById(R.id.chatBackBtn)

        args = ChatDialogFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(view.context)
            .load(args.users.imageUrl!!)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate().into(circleImageView);
        textView.text = args.users.name

        backImageView.setOnClickListener {
            findNavController().popBackStack()
           // requireActivity().onBackPressed()
        }
        binding.sendBtn.setOnClickListener {

            chatAppViewModel.sendMessage(getUiLoggedId(), args.users.userId!!, args.users.name!!, args.users.imageUrl!!)

        }

        chatAppViewModel.getMessages(args.users.userId!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messageRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.messageRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
