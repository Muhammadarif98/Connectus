package com.example.connectus.ui.Fragments.settings

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.SharedPrefs
import com.example.connectus.Utils
import com.example.connectus.databinding.FragmentProfileBinding
import com.example.connectus.mvvm.ChatAppViewModel
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
        inflater: LayoutInflater, container: ViewGroup?,
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
        profileViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            val mySharedPrefs = SharedPrefs(requireContext())
            val name = mySharedPrefs.getValue("name") as String
            Glide.with(this)
                .load(it)
                .into(binding.profileImage)
            if (it != null) {
                loadImage(it)
            }
        })
        binding.namePr.text = SharedPrefs(requireContext()).getValue("name").toString()
        binding.emailPr.text = SharedPrefs(requireContext()).getValue("email").toString()
        binding.nameProfileET.setText(profileViewModel.name.value)
        binding.lastNameProfileET.setText(profileViewModel.lastName.value)
        binding.phoneProfileET.setText(profileViewModel.phone.value)
        binding.adressProfileET.setText(profileViewModel.adress.value)
        binding.ageProfileET.setText(profileViewModel.adress.value)
        binding.employeeProfileET.setText(profileViewModel.employee.value)

        binding.btnSaveProfile.setOnClickListener {
            try {
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                Log.d("ProfileFragmentSaved", "Saved")
                profileViewModel.updateProfile()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                Log.d("ProfileFragmentError", "Error $e")
            }
        }

        binding.profileImage.setOnClickListener {
            val options = arrayOf<CharSequence>("Выбрать фото", "Выбрать из галереи", "Отмена")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Выберите способ загрузки фото профиля")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Выбрать фото" -> {
                        takePhotoWithCamera()
                    }
                    options[item] == "Выбрать из галереи" -> {
                        pickImageFromGallery()
                    }
                    options[item] == "Отмена" -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.ic_profile).dontAnimate()
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
                val result = bucket.upload(fileName, data) { upsert = true }
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





    override fun onResume() {
        super.onResume()
        profileViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                loadImage(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
