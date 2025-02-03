package com.example.connectus.ui.Fragments.home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.connectus.R
import com.example.connectus.Utils.Companion.getUidLoggedIn
import com.example.connectus.data.model.RecentChats
import com.example.connectus.data.model.Users
import com.example.connectus.databinding.FragmentMainBinding
import com.example.connectus.ui.adapter.OnUserClickListener
import com.example.connectus.ui.adapter.ViewPagerAdapter
import com.example.connectus.ui.adapter.onChatClicked
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment(), OnUserClickListener, onChatClicked {

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
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
        }

        // Обработчик долгого нажатия для показа PopupMenu
        binding.toolbar.setOnLongClickListener { view ->
            showPopupMenu(view)
            true
        }

        mainFragmentBody()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        setOptionalIconsVisible(menu)
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
        val userId = getUidLoggedIn()
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("name")
                    binding.toolbar.title = userName
                } else {
                    Log.d("com.example.connectus.ui.Fragments.home.MainFragment", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("com.example.connectus.ui.Fragments.home.MainFragment", "get failed with ", exception)
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
    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        val action = MainFragmentDirections.actionMainFragmentToRecentDialogFragment(chatList)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        // Получение доступа к внутреннему представлению PopupMenu и установка фона
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menuPopupHelper = popup.get(popupMenu)
            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
            val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
            setForceIcons.invoke(menuPopupHelper, true)
            val backgroundDrawable = resources.getDrawable(R.drawable.menu_background, null)
            classPopupHelper.getDeclaredMethod("setBackgroundDrawable", Drawable::class.java)
                .invoke(menuPopupHelper, backgroundDrawable)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
        }
        popupMenu.show()
    }


    @SuppressLint("RestrictedApi")
    private fun setOptionalIconsVisible(menu: Menu) {
        if (menu is MenuBuilder) {
            val menuBuilder = menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                if (item.icon != null) {
                    item.icon = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                    } else {
                        object : InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0) {
                            override fun getIntrinsicWidth(): Int {
                                return intrinsicHeight + iconMarginPx + iconMarginPx
                            }
                        }
                    }
                }
            }
        } else {
            MenuCompat.setGroupDividerEnabled(menu, true)
        }
    }
}
