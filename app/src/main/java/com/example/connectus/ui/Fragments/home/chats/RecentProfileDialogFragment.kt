package com.example.connectus.ui.Fragments.home.chats

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.connectus.R
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

      //  binding.friendId.text = args.recentProfile.userId
        binding.friendName.text = args.recentProfile.name
        binding.friendNameAgain.text = args.recentProfile.name

        binding.friendImageUrl.setOnClickListener {
            showImageDialog(args.recentProfile.friendsimage!!)
        }

       // binding.email.text = args.recentProfile.email
       // binding.friendId.text = args.recentProfile.userId
      //  binding.lastName.text = args.recentProfile.lastName
//         binding.phone.text = args.recentProfile.phone
        // binding.job.text = args.usersProfile.employee
        //  binding.age.text = args.usersProfile.age
        // binding.adress.text = args.usersProfile.adress

        //profileViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

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