package com.example.connectus.ui.Fragments.home.users

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.Utils
import com.example.connectus.databinding.FragmentFriendProfileBinding
import com.example.connectus.mvvm.ChatAppViewModel

class FriendProfileFragment : Fragment() {

    private lateinit var args: FriendProfileFragmentArgs

    private var _binding: FragmentFriendProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ChatAppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        args = FriendProfileFragmentArgs.fromBundle(requireArguments())

        Glide.with(view.context)
            .load(args.usersProfile.imageUrl!!)
            .placeholder(R.drawable.ic_profile)
            .into(binding.friendImageUrl)

        binding.friendImageUrl.setOnClickListener {
            showImageDialog(args.usersProfile.imageUrl!!)
        }
        binding.friendId.text = args.usersProfile.userId
        binding.friendName.text = args.usersProfile.name
        binding.friendNameAgain.text = args.usersProfile.name
        binding.email.text = args.usersProfile.email
        binding.lastName.text = args.usersProfile.lastName
        binding.phone.text = args.usersProfile.phone
        binding.job.text = args.usersProfile.employee
        binding.age.text = args.usersProfile.age
        binding.adress.text = args.usersProfile.adress

        binding.chatBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.btnCallFriend.setOnClickListener {
            makeCall(args.usersProfile.phone!!)
        }

        binding.btnSendSmsFriend.setOnClickListener {
            sendMessage(args.usersProfile.phone!!)
        }

        profileViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

    }
    private fun makeCall(phoneNumber: String) {
        if (!Utils.PermissionsHelper.hasPermission(requireContext(), Manifest.permission.CALL_PHONE)) {
            Utils.PermissionsHelper.requestPermission(requireActivity(), Manifest.permission.CALL_PHONE, 2)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
        }
    }

    private fun sendMessage(phoneNumber: String) {
        if (!Utils.PermissionsHelper.hasPermission(requireContext(), Manifest.permission.SEND_SMS)) {
            Utils.PermissionsHelper.requestPermission(requireActivity(), Manifest.permission.SEND_SMS, 3)
        } else {
           sendSms(phoneNumber)
        }
    }

    private fun sendSms(phoneNumber: String) {
        try {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body",
                "Привет! Это сообщение отправлено через приложение Connectus.")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Ошибка открытия приложения для SMS: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
