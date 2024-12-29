package com.example.connectus.ui.Fragments.home.chats

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
import com.example.connectus.Utils.Companion.getUiLoggedId
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

        chatAppViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]


        textView = chatToolbar.findViewById(R.id.recentUserName)
        circleImageView = chatToolbar.findViewById(R.id.recentImageViewUser)
        backImageView = chatToolbar.findViewById(R.id.recentBackBtn)

        args = RecentDialogFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(view.context)
            .load(args.recentchats.friendsimage!!)
            .placeholder(R.drawable.ic_profile)
            .dontAnimate().into(circleImageView);
        textView.text = args.recentchats.name

        backImageView.setOnClickListener {
            findNavController().popBackStack()
            // requireActivity().onBackPressed()
        }
        binding.sendBtn.setOnClickListener {

            chatAppViewModel.sendMessage(
                getUiLoggedId(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!
            )

        }
        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.recentRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.recentRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}