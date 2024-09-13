package com.example.connectus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText

class LoginInputFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_input_fragment, container, false)

        val editTextLogin = view.findViewById<TextInputEditText>(R.id.editTextLogin)

        editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = editTextLogin.text.toString()
                if (!isValidEmail(email)) {
                    editTextLogin.error = "Неверный формат email адреса"
                } else {
                    editTextLogin.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })

        return view
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z]+"
        return email.matches(emailPattern.toRegex())
    }

}
