package com.example.connectus.ui.Fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.connectus.R
import com.example.connectus.mvvm.RegisterViewModel
import com.example.connectus.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSign.setOnClickListener {
            val name = binding.nameRegisterET.text.toString()
            val lastName = binding.lastNameRegisterET.text.toString()
            val phone = binding.phoneRegisterET.text.toString()
            val email = binding.editTextLogin.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.confirmPasswordET.text.toString()

            viewModel.signUpUser(name, lastName, phone, email, password, confirmPassword)
        }
        binding.loginButtonTV.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSign.isEnabled = isChecked
            if (!isChecked) {
                Toast.makeText(requireContext(), "Поставьте галочку", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registrationStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            if (status == "Регистрация прошла успешно") {
                val loginInputFragment = LoginInputFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, loginInputFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        listOf(
            viewModel.nameError to binding.nameRegisterET,
            viewModel.lastNameError to binding.lastNameRegisterET,
            viewModel.phoneError to binding.phoneRegisterET,
            viewModel.emailError to binding.editTextLogin,
            viewModel.passwordError to binding.editTextPassword,
            viewModel.confirmPasswordError to binding.confirmPasswordET
        ).forEach { (error, editText) ->
            error.observe(viewLifecycleOwner) { editText.error = it }
        }

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
