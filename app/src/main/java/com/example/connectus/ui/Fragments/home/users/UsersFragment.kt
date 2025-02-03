package com.example.connectus.ui.Fragments.home.users

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
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

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userViewModel: ChatAppViewModel

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private var navigationListener: OnUserClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

        userAdapter = UserAdapter()
        usersRecyclerView = view.findViewById(R.id.rvRecentUsers)
        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        usersRecyclerView.layoutManager = layoutManagerUsers

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {
            userAdapter.setUserList(it)
            userAdapter.setOnUserClickListener(this)
            usersRecyclerView.adapter = userAdapter
        })

        navigationListener = parentFragment as? OnUserClickListener

        // Обработка изменения текста в EditText
        val searchUserEditText = view.findViewById<EditText>(R.id.search_user)
        searchUserEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    usersRecyclerView.visibility = View.GONE
                } else {
                    userAdapter.filter(s.toString())
                    usersRecyclerView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserSelected(position: Int, users: Users) {
        navigationListener?.onUserSelected(position, users)
    }
}
