package com.example.connectus.ui.Fragments.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils
import com.example.connectus.databinding.FragmentProfileBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.HorizontalAdapter
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ChatAppViewModel
    private lateinit var progressDialogUpdate: ProgressDialog
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://cpozuctgjtujueilydrs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNwb3p1Y3RnanR1anVlaWx5ZHJzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzU1NTQ0ODIsImV4cCI6MjA1MTEzMDQ4Mn0.dPE0pHw8Daj3so6Ox03XTUwz6Eo5htet3K9P3GG-zN8"
    ) {
        install(Auth)
        install(Realtime)
        install(Storage)
        install(Postgrest)
    }
    var uri: Uri? = null
    lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val view = binding.root
        progressDialogUpdate = ProgressDialog(requireContext())


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.lifecycleOwner = viewLifecycleOwner
        profileViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        binding.profileImage.setOnClickListener {
            showImageDialog(profileViewModel.imageUrl.value ?: "")
        }

        setupImageObserver()
        setupTextFields()
        setupSaveButton()
        setupProfileImageClick()
    }

    private fun setupImageObserver() {
        profileViewModel.imageUrl.observe(viewLifecycleOwner) { imageUrl ->
            val mySharedPrefs = SharedPrefs(requireContext())
            Glide.with(this)
                .load(imageUrl)
                .into(binding.profileImage)
            if (imageUrl != null) {
                loadImage(imageUrl)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupTextFields() {
        val textFields = listOf(
            binding.nameProfileET to profileViewModel.name,
            binding.lastNameProfileET to profileViewModel.lastName,
            binding.phoneProfileET to profileViewModel.phone,
            binding.adressProfileET to profileViewModel.adress,
            binding.ageProfileET to profileViewModel.age,
            binding.employeeProfileET to profileViewModel.employee
        )

        textFields.forEach { (editText, liveData) ->
            liveData.observe(viewLifecycleOwner) { value ->
                if (editText.text.toString() != value) {
                    editText.setText(value)
                }
            }

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (editText.hasFocus() && liveData.value != s.toString()) {
                        liveData.value = s.toString()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        profileViewModel.name.observe(viewLifecycleOwner) { name ->
            if (binding.namePr.text.toString() != name) {
                binding.namePr.text = name
                SharedPrefs(requireContext()).setValue("name", name ?: "")
            }
        }

        profileViewModel.email.observe(viewLifecycleOwner) { email ->
            if (binding.emailPr.text.toString() != email) {
                binding.emailPr.text = email
                SharedPrefs(requireContext()).setValue("email", email ?: "")
            }
        }

        if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            binding.btnVerificarion.text = "Верифицирован"
        }
    }


    private fun setupSaveButton() {
        binding.btnSaveProfile.setOnClickListener {
            try {
                Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
                Log.d("ProfileFragmentSaved", "Saved")
                profileViewModel.updateProfile()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                Log.d("ProfileFragmentError", "Error $e")
            }
        }
    }

    private fun showImageDialog(imageUrl: String) {
        val dialog = Dialog(
            requireContext(),
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
        dialog.setContentView(R.layout.dialog_fullscreen_image)
        val imageView =
            dialog.findViewById<ImageView>(R.id.fullscreen_image)
        Glide.with(this).load(imageUrl)
            .into(imageView)
        imageView.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setupProfileImageClick() {
        binding.statusOnlineProfile.setOnClickListener {
            val items = arrayOf("Камера", "Галерея")
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
            val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view)

            titleTextView.text = "Выбрать / Сделать\n" +
                    "изображение из"

            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.isNestedScrollingEnabled = false
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)

            val dialog = dialogBuilder.create()
            val adapter = HorizontalAdapter(items) { selectedItem ->
                when (selectedItem) {
                    "Камера" -> takePhotoWithCamera()
                    "Галерея" -> pickImageFromGallery()
                }
                dialog.dismiss() // Закрываем диалог
            }
            recyclerView.adapter = adapter


            dialog.show() // Показываем диалог
        }
    }


    private fun loadImage(imageUrl: String) {
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate()
            .into(binding.profileImage)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    uploadImageToSupabase(imageBitmap)
                }

                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToSupabase(imageBitmap)
                }
            }
        }
    }

    private fun uploadImageToSupabase(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        bitmap = imageBitmap!!
        binding.profileImage.setImageBitmap(imageBitmap)

        val fileName = "Photos/${UUID.randomUUID()}.jpg"
        val bucket = supabase.storage.from("connectus")

        progressDialogUpdate.show()
        progressDialogUpdate.setMessage("Загрузка...")

        lifecycleScope.launch {
            try {
                bucket.upload(fileName, data) { upsert = true }
                val imageUrl = bucket.publicUrl(fileName)
                profileViewModel.imageUrl.postValue(imageUrl)
                Log.d("ProfileFragment", "Image URL: $imageUrl")
                progressDialogUpdate.dismiss()
                Toast.makeText(context, "Картинка загружена успешно!", Toast.LENGTH_SHORT).show()
            } catch (exception: Exception) {
                Log.e("ProfileFragment", exception.message.toString())
                Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
