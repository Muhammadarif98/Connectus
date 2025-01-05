package com.example.connectus.ui.Fragments.home.users

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connectus.R
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
        binding.friendId.text = args.usersProfile.userId
        binding.lastName.text = args.usersProfile.lastName
        binding.phone.text = args.usersProfile.phone
        // binding.job.text = args.usersProfile.employee
        //  binding.age.text = args.usersProfile.age
        // binding.adress.text = args.usersProfile.adress

        profileViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

//        profileViewModel.friendName.observe(viewLifecycleOwner) {
//            binding.friendName.text = it
//            binding.friendNameAgain.text = it
//        }
//        profileViewModel.friendEmail.observe(viewLifecycleOwner) { binding.email.text = it }
//        profileViewModel.friendId.observe(viewLifecycleOwner) { binding.friendId.text = it }
//        profileViewModel.friendLastName.observe(viewLifecycleOwner) { binding.lastName.text = it }
//        profileViewModel.friendEmployee.observe(viewLifecycleOwner) { binding.job.text = it }
//        profileViewModel.friendPhone.observe(viewLifecycleOwner) { binding.phone.text = it }
//        profileViewModel.friendAge.observe(viewLifecycleOwner) { binding.age.text = it }
//        profileViewModel.friendAdress.observe(viewLifecycleOwner) { binding.adress.text = it }
//          profileViewModel.loginMethod.observe(viewLifecycleOwner) { binding.loginMethod.text = it }


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
