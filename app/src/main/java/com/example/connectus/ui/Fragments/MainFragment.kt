package com.example.connectus.ui.Fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.connectus.R
import com.example.connectus.Utils.Companion.getUiLoggedId
import com.example.connectus.data.model.Users
import com.example.connectus.databinding.FragmentMainBinding
import com.example.connectus.ui.adapter.OnUserClickListener
import com.example.connectus.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment(), OnUserClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
        }

        mainFragmentBody()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val loginFragment = Runnable {
            findNavController().navigate(R.id.action_mainFragment_to_startFragment)
        }

        return when (item.itemId) {
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                true
            }
            R.id.action_about -> {
                findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)
                true
            }
            R.id.action_logout -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Выход")
                    .setMessage("Вы хотите выйти?")
                    .setPositiveButton("Да") { _, _ ->
                        auth.signOut()
                        Handler().postDelayed(loginFragment, 0)
                    }
                    .setNegativeButton("Нет", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mainFragmentBody() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userId = getUiLoggedId()
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name")
                    binding.toolbar.title = userName
                } else {
                    Log.d("MainFragment", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("MainFragment", "get failed with ", exception)
            }

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

    override fun onUserSelected(position: Int, users: Users) {
        val action = MainFragmentDirections.actionMainFragmentToChatDialogFragment(users)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            menuInflater.inflate(R.menu.menu_main, menu)
            setOnMenuItemClickListener { item ->
                onOptionsItemSelected(item)
            }
            show()
        }
    }
}

//            activity?.supportFragmentManager?.beginTransaction()
//                ?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                ?.replace(R.id.nav_host_fragment, LoginInputFragment())
//                ?.commit()
//                val aboutFragment = AboutFragment()
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.nav_host_fragment, aboutFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//                auth.signOut()
//                val loginFragment = LoginInputFragment()
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.container, loginFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//                val profileFragment = ProfileFragment()
////                val transaction = requireActivity().supportFragmentManager.beginTransaction()
////                transaction.replace(R.id.nav_host_fragment, profileFragment)
////                transaction.addToBackStack(null)
////                transaction.commit()