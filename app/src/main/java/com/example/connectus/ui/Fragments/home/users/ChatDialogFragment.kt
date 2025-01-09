package com.example.connectus.ui.Fragments.home.users

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
import com.example.connectus.databinding.FragmentChatDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.mvvm.ChatAppViewModelFactory
import com.example.connectus.ui.adapter.message.MessageAdapter
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

    // Регистрируем ActivityResultLauncher для выбора файла
    /* private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { fileUri ->
         if (fileUri == null) {
             Log.e("ChatDialogFragment", "File URI is null: пользователь отменил выбор файла")
             return@registerForActivityResult
         }
         val fileType = requireContext().contentResolver.getType(fileUri) ?: getFileTypeFromUri(fileUri)
         chatAppViewModel.sendFile(
             requireContext(),
             getUidLoggedIn(),
             args.users.userId!!,
             fileUri,
             fileType
         )
         Log.d("FilePicker", "File URI: $fileUri")
     }

     private fun getFileTypeFromUri(uri: Uri): String {
         val fileName = uri.lastPathSegment ?: return "application/octet-stream"
         return when {
             fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
             fileName.endsWith(".png") -> "image/png"
             fileName.endsWith(".pdf") -> "application/pdf"
             fileName.endsWith(".mp3") -> "audio/mpeg"
             else -> "application/octet-stream"
         }
     }*/
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
                args.users.userId!!,
                imageUri
            )
            Log.d("ImagePicker", "Image URI: $imageUri")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chat_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatToolbar = view.findViewById(R.id.toolBarChat)


        val factory = ChatAppViewModelFactory(
            lifecycleOwner = viewLifecycleOwner
        )

        chatAppViewModel = ViewModelProvider(this, factory)[ChatAppViewModel::class.java]

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
            val action =
                ChatDialogFragmentDirections.actionChatDialogFragmentToFriendProfileFragment(args.users)
            findNavController().navigate(action)
        }
        backImageView.setOnClickListener {
            findNavController().popBackStack()
        }

        // Обработка нажатия на кнопку отправки текстового сообщения
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

        // Обработка нажатия на кнопку отправки файла
        binding.addFileButton.setOnClickListener {
            pickFile()
        }

        // Наблюдаем за сообщениями
        chatAppViewModel.getMessages(args.users.userId!!).observe(viewLifecycleOwner, Observer {
            Log.d("ChatDialogFragment", "Сообщения получены: ${it.size}")
            initRecyclerView(it)
        })
    }

    // Открываем диалог выбора файла
    private fun pickFile() {
        pickImageLauncher.launch("image/*")
        //pickFileLauncher.launch("*/*")
    }

    // Показ диалога удаления сообщения
    private fun showDeleteMessageDialog(message: Messages, isImage: Boolean) {
        if (message.sender == getUidLoggedIn()) {
            Log.d("ChatDialogFragment", "Показ диалога для удаления сообщения: ${message.id}")

            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle("Что вы хотите сделать?")

            // Если это изображение, добавляем действие "Посмотреть"
            if (isImage) {
                dialogBuilder.setNeutralButton("Посмотреть") { _, _ ->
                    // Открываем изображение на полный экран
                    showFullScreenImage(message.fileUrl)
                }
            }

            // Добавляем действие "Удалить"
            dialogBuilder.setPositiveButton("Удалить") { _, _ ->
                val uniqueId =
                    listOf(getUidLoggedIn(), args.users.userId!!).sorted().joinToString("")
                chatAppViewModel.deleteMessage(uniqueId, message.id)
            }

            // Добавляем действие "Отмена"
            dialogBuilder.setNegativeButton("Отмена", null)

            // Показываем диалог
            dialogBuilder.show()
        } else {
            Log.d(
                "ChatDialogFragment",
                "Сообщение не принадлежит пользователю, удаление невозможно"
            )
        }
    }

    private fun showFullScreenImage(imageUrl: String?) {
        if (imageUrl == null) {
            Log.e("ShowFullScreenImage", "Image URL is null")
            return
        }

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_fullscreen_image)
        Log.d("ShowFullScreenImage", "Showing full screen image with URL: $imageUrl")
        val imageView = dialog.findViewById<ImageView>(R.id.fullscreen_image)
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        imageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // Инициализация RecyclerView
    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter { message, isImage ->
            showDeleteMessageDialog(message, isImage)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.messageRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        binding.messageRecyclerView.adapter = adapter

        Log.d("ChatDialogFragment", "RecyclerView инициализирован")
    }

    // Обработка результата запроса разрешений
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