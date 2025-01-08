package com.example.connectus.ui.Fragments.home.users

import ChatAppViewModelFactory
import android.os.Bundle
import android.util.Log
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
import com.example.connectus.databinding.FragmentChatDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.MessageAdapter
import de.hdodenhof.circleimageview.CircleImageView

class ChatDialogFragment : Fragment() {
    private var _binding: FragmentChatDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: ChatDialogFragmentArgs

    private lateinit var adapter: MessageAdapter
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

        chatAppViewModel = ViewModelProvider(this, ChatAppViewModelFactory(requireActivity().application)).get(ChatAppViewModel::class.java)
        Log.d("ChatDialogFragment", "ViewModel инициализирован")

        textView = chatToolbar.findViewById(R.id.chatUserName)
        circleImageView = chatToolbar.findViewById(R.id.chatImageViewUser)
        backImageView = chatToolbar.findViewById(R.id.chatBackBtn)

        args = ChatDialogFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(args.users.imageUrl!!)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate().into(circleImageView)

        textView.text = args.users.name

        circleImageView.setOnClickListener {
            val action = ChatDialogFragmentDirections.actionChatDialogFragmentToFriendProfileFragment(args.users)
            findNavController().navigate(action)
        }
        backImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {
            args.users.lastName?.let { lastName ->
                args.users.employee?.let { employee ->
                    chatAppViewModel.sendMessage(
                        getUidLoggedIn(),
                        args.users.userId!!,
                        args.users.name!!,
                        args.users.imageUrl!!,
                        args.users.email!!,
                        lastName,
                        args.users.phone!!,
                        args.users.adress!!,
                        args.users.age!!,
                        employee
                    )
                }
            }
        }
        chatAppViewModel.getMessages(args.users.userId!!).observe(viewLifecycleOwner, Observer {
            Log.d("ChatDialogFragment", "Сообщения получены: ${it.size}")
            initRecyclerView(it)
        })
    }

    private fun showDeleteMessageDialog(message: Messages) {
        // Проверяем, что сообщение принадлежит текущему пользователю
        if (message.sender == getUidLoggedIn()) {
            Log.d("ChatDialogFragment", "Показ диалога для удаления сообщения: ${message.id}")
            AlertDialog.Builder(requireContext())
                .setTitle("Что вы хотите сделать?")
                .setPositiveButton("Удалить сообщение") { _, _ ->
                    val uniqueId = listOf(getUidLoggedIn(), args.users.userId!!).sorted().joinToString("")
                    chatAppViewModel.deleteMessage(uniqueId, message.id) // Используем реальный ID
                }
                .setNegativeButton("Отмена", null)
                .show()
        } else {
            // Сообщение не принадлежит пользователю, ничего не делаем
            Log.d("ChatDialogFragment", "Сообщение не принадлежит пользователю, удаление невозможно")
        }
    }


    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter { message ->
            showDeleteMessageDialog(message)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.messageRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        binding.messageRecyclerView.adapter = adapter

        Log.d("ChatDialogFragment", "RecyclerView инициализирован")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}