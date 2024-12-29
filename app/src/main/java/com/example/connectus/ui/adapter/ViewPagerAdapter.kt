package com.example.connectus.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.connectus.ui.Fragments.home.chats.ChatsFragment
import com.example.connectus.ui.Fragments.home.users.UsersFragment


class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> UsersFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}