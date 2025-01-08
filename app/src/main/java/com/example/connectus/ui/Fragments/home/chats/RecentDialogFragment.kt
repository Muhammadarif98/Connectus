package com.example.connectus.ui.Fragments.home.chats

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

        // Инициализация ViewModel
        chatAppViewModel = ViewModelProvider(this, ChatAppViewModelFactory(requireActivity().application)).get(ChatAppViewModel::class.java)

        // Настройка UI
        setupUI()

        // Наблюдение за сообщениями
        observeMessages()

        // Обработка нажатий
        setupClickListeners()
    }

    private fun setupUI() {
        textView = chatToolbar.findViewById(R.id.recentUserName)
        circleImageView = chatToolbar.findViewById(R.id.recentImageViewUser)
        backImageView = chatToolbar.findViewById(R.id.recentBackBtn)

        args = RecentDialogFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Загрузка изображения и имени друга
        Glide.with(requireContext())
            .load(args.recentchats.friendsimage)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate()
            .into(circleImageView)
        textView.text = args.recentchats.name
    }

    private fun observeMessages() {
        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }

    private fun setupClickListeners() {
        circleImageView.setOnClickListener {
            val action = RecentDialogFragmentDirections
                .actionRecentDialogFragmentToRecentProfileDialogFragment(args.recentchats)
            findNavController().navigate(action)
        }

        backImageView.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        chatAppViewModel.sendMessage(
            getUidLoggedIn(),
            args.recentchats.friendid!!,
            args.recentchats.name!!,
            args.recentchats.friendsimage!!,
            args.recentchats.email!!,
            args.recentchats.lastname!!,
            args.recentchats.phone!!,
            args.recentchats.friendAdress!!,
            args.recentchats.friendAge!!,
            args.recentchats.friendEmployee!!
        )
    }

    private fun showDeleteMessageDialog(message: Messages) {
        // Проверяем, что сообщение принадлежит текущему пользователю
        if (message.sender == getUidLoggedIn()) {
            Log.d("ChatDialogFragment", "Показ диалога для удаления сообщения: ${message.id}")

            AlertDialog.Builder(requireContext())
                .setTitle("Что вы хотите сделать?")
                .setPositiveButton("Удалить сообщение") { _, _ ->
                    val uniqueId = listOf(getUidLoggedIn(), args.recentchats.friendid!!).sorted().joinToString("")
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
        binding.recentRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        binding.recentRecyclerView.adapter = adapter

        Log.d("ChatDialogFragment", "RecyclerView инициализирован")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}