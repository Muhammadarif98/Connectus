package com.example.connectus.ui.Fragments.home.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages
import com.example.connectus.databinding.FragmentRecentDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.MessageAdapter
import de.hdodenhof.circleimageview.CircleImageView


class RecentDialogFragment : Fragment() {

    private var _binding: FragmentRecentDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: RecentDialogFragmentArgs

    private lateinit var adapter: MessageAdapter
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chatToolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var backImageView: ImageView
    private lateinit var textView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatToolbar = view.findViewById(R.id.recenttoolBarChat)

        chatAppViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]


        textView = chatToolbar.findViewById(R.id.recentUserName)
        circleImageView = chatToolbar.findViewById(R.id.recentImageViewUser)
        backImageView = chatToolbar.findViewById(R.id.recentBackBtn)

        args = RecentDialogFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(view.context)
            .load(args.recentchats.friendsimage!!)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate().into(circleImageView);
        textView.text = args.recentchats.name

        circleImageView.setOnClickListener {
            val action = RecentDialogFragmentDirections.actionRecentDialogFragmentToRecentProfileDialogFragment(args.recentchats)
            findNavController().navigate(action)
        }

        backImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {

            chatAppViewModel.sendMessage(
                getUidLoggedIn(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!,
                args.recentchats.email!!,
                args.recentchats.lastname!! ,
                args.recentchats.phone!!,
                args.recentchats.friendAdress!!,
                args.recentchats.friendAge!!,
                args.recentchats.friendEmployee!!,
            )

        }
        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }

    private fun showDeleteMessageDialog(message: Messages) {
        AlertDialog.Builder(requireContext())
            .setTitle("Что вы хотите сделать?")
            .setPositiveButton("Удалить сообщение") { _, _ ->
                chatAppViewModel.deleteMessage(args.recentchats.friendid!!, message.id!!)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter(){message ->
            showDeleteMessageDialog(message)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.recentRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.recentRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}