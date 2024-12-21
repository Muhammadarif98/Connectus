package com.example.connectus.ui.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserSelected(position: Int, users: Users) {
        navigationListener?.onUserSelected(position, users)
    }
}


//        val bundle = Bundle().apply {
//            putParcelable("user", users)
//        }
//
//        Log.d("ChoseUsersFragment", "Selected user: ${users.name}")
//
//
//        val chatDialogFragment = ChatDialogFragment().apply {
//            arguments = bundle
//        }
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.container, chatDialogFragment) // Заменяем текущий фрагмент
//        transaction.addToBackStack(null) // Добавляем в стек возврата
//        transaction.commit() // Завершаем транзакцию