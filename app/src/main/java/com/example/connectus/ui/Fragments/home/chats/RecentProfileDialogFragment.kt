package com.example.connectus.ui.Fragments.home.chats

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.connectus.R
import com.example.connectus.Utils
import com.example.connectus.databinding.FragmentRecentProfileDialogBinding


class RecentProfileDialogFragment : Fragment() {

    private lateinit var args: RecentProfileDialogFragmentArgs

    private var _binding: FragmentRecentProfileDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        args = RecentProfileDialogFragmentArgs.fromBundle(requireArguments())

        Glide.with(view.context)
            .load(args.recentProfile.friendsimage!!)
            .placeholder(R.drawable.ic_profile)
            .into(binding.friendImageUrl)
        binding.friendImageUrl.setOnClickListener {
            showImageDialog(args.recentProfile.friendsimage!!)
        }

        binding.friendId.text = args.recentProfile.friendid
        binding.friendLastName.text = args.recentProfile.lastname
        binding.phone.text = args.recentProfile.phone
        binding.email.text = args.recentProfile.email
        binding.friendName.text = args.recentProfile.name
        binding.friendNameAgain.text = args.recentProfile.name

        binding.adress.text = args.recentProfile.friendAdress
        binding.age.text = args.recentProfile.friendAge
        binding.job.text = args.recentProfile.friendEmployee

        binding.btnCallFriend.setOnClickListener {
            makeCall(args.recentProfile.phone!!)
        }

        binding.btnSendSmsFriend.setOnClickListener {
            sendMessage(args.recentProfile.phone!!)
        }
        binding.chatBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun makeCall(phoneNumber: String) {
        if (!Utils.PermissionsHelper.hasPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
        ) {
            Utils.PermissionsHelper.requestPermission(
                requireActivity(),
                Manifest.permission.CALL_PHONE,
                2
            )
        } else {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
        }
    }

    private fun sendMessage(phoneNumber: String) {
        if (!Utils.PermissionsHelper.hasPermission(
                requireContext(),
                Manifest.permission.SEND_SMS
            )
        ) {
            Utils.PermissionsHelper.requestPermission(
                requireActivity(),
                Manifest.permission.SEND_SMS,
                3
            )
        } else {
            sendSms(phoneNumber)
        }
    }

    private fun sendSms(phoneNumber: String) {
        try {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra(
                "sms_body",
                "Привет! Это сообщение отправлено через приложение Connectus."
            )
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
