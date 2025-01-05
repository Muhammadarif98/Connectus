package com.example.connectus.ui.Fragments.home.users

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
            .dontAnimate().into(circleImageView)

        textView.text = args.users.name

        circleImageView.setOnClickListener {
            val action = ChatDialogFragmentDirections.actionChatDialogFragmentToFriendProfileFragment(args.users)
            findNavController().navigate(action)
        }
        backImageView.setOnClickListener {
            findNavController().popBackStack()
            // requireActivity().onBackPressed()
        }
        binding.sendBtn.setOnClickListener {

            args.users.lastName?.let { it1 ->
                args.users.employee?.let { it2 ->
                    chatAppViewModel.sendMessage(
                        getUidLoggedIn(),
                        args.users.userId!!,
                        args.users.name!!,
                        args.users.imageUrl!!,
                        args.users.email!!,
                        it1,
                        args.users.phone!!,
                        args.users.adress!!,
                        args.users.age!!,
                        it2
                    )
                }
            }

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


/*package com.example.connectus.ui.Fragments.home.users

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.Utils
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.Utils.Companion.supabase
import com.example.connectus.data.model.Messages
import com.example.connectus.databinding.FragmentChatDialogBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.MessageAdapter
import de.hdodenhof.circleimageview.CircleImageView
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class ChatDialogFragment : Fragment() {
    private var _binding: FragmentChatDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var args: ChatDialogFragmentArgs
    private lateinit var progressDialogUpdate: ProgressDialog
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
        progressDialogUpdate = ProgressDialog(requireContext())
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

        circleImageView.setOnClickListener {
            findNavController().navigate(R.id.action_chatDialogFragment_to_profileDialogFragment)
        }

        backImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.addFile.setOnClickListener {
            pickImageFromGallery()
        }



        binding.sendBtn.setOnClickListener {
            chatAppViewModel.sendMessage(getUiLoggedId(), args.users.userId!!, args.users.name!!, args.users.imageUrl!!)
            //chatAppViewModel.sendFile(getUiLoggedId(), args.users.userId!!, args.users.name!!, args.users.imageUrl!!)

        }
        chatAppViewModel.getMessages(args.users.userId!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        uploadImageToSupabase(it)
                    }
                }
            }
        }
    }

    private fun uploadImageToSupabase(imageUri: Uri) {
        val context = requireContext()
        val contentResolver = context.contentResolver
        val imageStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val fileName = "Photos/${UUID.randomUUID()}.jpg"
        val bucket = supabase.storage.from("connectus")
        progressDialogUpdate.show()
        progressDialogUpdate.setMessage("Загрузка...")
        lifecycleScope.launch {
            try {
                val result = bucket.upload(fileName, data) { upsert = true }
                val imageUrl = bucket.publicUrl(fileName)
                chatAppViewModel.imageUrl.postValue(imageUrl)
                Log.d("ProfileFragment", "Image URL: $imageUrl")
                progressDialogUpdate.dismiss()
                Toast.makeText(context, "Картинка загружена успешно!", Toast.LENGTH_SHORT).show()
            } catch (exception: Exception) {
                Log.e("ProfileFragment", exception.message.toString())
                Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messageRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        binding.messageRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/