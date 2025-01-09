package com.example.connectus.ui.Fragments.home.chats

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.connectus.Utils.Companion.REQUEST_CODE_READ_EXTERNAL_STORAGE
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.Messages
import com.example.connectus.databinding.FragmentRecentDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.mvvm.ChatAppViewModelFactory
import com.example.connectus.ui.adapter.message.MessageAdapter
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

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            if (imageUri == null) {
                Log.e(
                    "ChatDialogFragment",
                    "Image URI is null: пользователь отменил выбор изображения"
                )
                return@registerForActivityResult
            }
            // Отправляем изображение
            chatAppViewModel.sendImage(
                requireContext(),
                getUidLoggedIn(),
                args.recentchats.friendid!!,
                imageUri
            )
            Log.d("ImagePicker", "Image URI: $imageUri")
        }

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

        val factory = ChatAppViewModelFactory(
            lifecycleOwner = viewLifecycleOwner
        )
        chatAppViewModel = ViewModelProvider(this, factory)[ChatAppViewModel::class.java]

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
    private fun pickFile() {
        pickImageLauncher.launch("image/*")
        //pickFileLauncher.launch("*/*")
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

        binding.addFileButton.setOnClickListener {
            pickFile()
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

    private fun showDeleteMessageDialog(message: Messages, isImage: Boolean) {
        Log.d("ChatDialogFragment", "showDeleteMessageDialog called: messageId=${message.id}, isImage=$isImage, fileUrl=${message.fileUrl}")

        if (message.sender == getUidLoggedIn()) {
            Log.d("ChatDialogFragment", "Показ диалога для удаления сообщения: ${message.id}")

            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle("Что вы хотите сделать?")

            if (isImage) {
                dialogBuilder.setNeutralButton("Посмотреть") { _, _ ->
                    showFullScreenImage(message.fileUrl)
                }
            }

            dialogBuilder.setPositiveButton("Удалить") { _, _ ->
                val uniqueId = listOf(getUidLoggedIn(), args.recentchats.friendid!!).sorted().joinToString("")
                chatAppViewModel.deleteMessage(uniqueId, message.id)
            }

            dialogBuilder.setNegativeButton("Отмена", null)
            dialogBuilder.show()
        } else {
            if (isImage) {
                Log.d("ChatDialogFragment", "Показ диалога для просмотра изображения: ${message.id}")

                val dialogBuilder = AlertDialog.Builder(requireContext())
                    .setTitle("Что вы хотите сделать?")

                dialogBuilder.setNeutralButton("Посмотреть") { _, _ ->
                    showFullScreenImage(message.fileUrl)
                }

                dialogBuilder.setNegativeButton("Отмена", null)
                dialogBuilder.show()
            } else {
                Log.d(
                    "ChatDialogFragment",
                    "Сообщение не принадлежит пользователю, удаление невозможно"
                )
            }
        }
    }

    private fun showFullScreenImage(imageUrl: String?) {
        if (imageUrl == null) return

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreen_image)
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        imageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter { message, isImage ->
            showDeleteMessageDialog(message, isImage)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.recentRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        binding.recentRecyclerView.adapter = adapter

        Log.d("ChatDialogFragment", "RecyclerView инициализирован")
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                // Проверяем, было ли предоставлено разрешение
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*")
                    // Если разрешение предоставлено, запускаем выбор файла
                    // pickFileLauncher.launch("*/*")
                } else {
                    // Если разрешение не предоставлено, показываем сообщение пользователю
                    Toast.makeText(
                        requireContext(),
                        "Разрешение на чтение файлов отклонено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
