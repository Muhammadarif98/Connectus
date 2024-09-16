package com.example.connectus

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.connectus.ui.Fragments.LoginInputFragment
import com.example.connectus.ui.Fragments.MainFragment
import com.example.connectus.ui.Fragments.SplashFragment
import com.example.connectus.ui.Fragments.StartFragment
import com.google.firebase.auth.FirebaseAuth

class FragmentRunners(private val activity: FragmentActivity, private val containerId: Int) {

    val loginFragment = Runnable {
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, LoginInputFragment())
            .commit()
    }
    val startFragment = Runnable {
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, StartFragment())
            .commit()
    }

    val splashFragment = Runnable {
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, SplashFragment())
            .commit()
    }

    val mainFragment = Runnable {
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, MainFragment())
            .commit()
    }
}

object DialogUtils {
    fun showExitDialog(context: Context, auth: FirebaseAuth, fragmentRunners: FragmentRunners) {
        AlertDialog.Builder(context)
            .setTitle("Выход")
            .setMessage("Вы хотите выйти?")
            .setPositiveButton("Да") { _, _ ->
                auth.signOut()
                android.os.Handler().postDelayed(fragmentRunners.loginFragment, 0)
            }
            .setNegativeButton("Нет", null)
            .show()
    }
}