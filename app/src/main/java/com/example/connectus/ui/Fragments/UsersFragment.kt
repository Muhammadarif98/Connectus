package com.example.connectus.ui.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectus.R
import com.example.connectus.data.model.Users
import com.example.connectus.databinding.FragmentUsersBinding
import com.example.connectus.mvvm.ChatAppViewModel
import com.example.connectus.ui.adapter.OnUserClickListener
import com.example.connectus.ui.adapter.UserAdapter

class UsersFragment : Fragment(), OnUserClickListener {

    lateinit var usersRecyclerView: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        //binding.lifecycleOwner = viewLifecycleOwner

        userAdapter = UserAdapter()
        usersRecyclerView = view.findViewById(R.id.rvRecentUsers)

        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        usersRecyclerView.layoutManager = layoutManagerUsers

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {

            userAdapter.setUserList(it)
            userAdapter.setOnUserClickListener(this)
            usersRecyclerView.adapter = userAdapter
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserSelected(position: Int, users: Users) {

    }
}
