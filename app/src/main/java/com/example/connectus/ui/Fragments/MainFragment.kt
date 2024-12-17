package com.example.connectus.ui.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.connectus.R
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.databinding.FragmentMainBinding
import com.example.connectus.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        mainFragmentBody()

        return view
    }

    private fun mainFragmentBody() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userId = getUiLoggedId()
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                val userName = document.getString("name")
                binding.toolbar.apply {
                    title = userName
                    setNavigationOnClickListener {
                        findNavController().navigateUp()
                    }
                }
            } else {
                Log.d("YourActivity", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("YourActivity", "get failed with ", exception)
        }

        binding.loginOut.setOnClickListener {
            auth.signOut()
            val loginFragment = LoginInputFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, loginFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Инициализация ViewPager и TabLayout
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Чаты"
                1 -> "Пользователи"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


