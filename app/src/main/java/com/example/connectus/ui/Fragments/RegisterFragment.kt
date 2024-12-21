package com.example.connectus.ui.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.connectus.R
import com.example.connectus.databinding.FragmentRegisterBinding
import com.example.connectus.mvvm.RegisterViewModel

class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var progressDialogSignUp: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        progressDialogSignUp = ProgressDialog(requireContext())

        binding.termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSign.isEnabled = isChecked
            if (!isChecked) {
                Toast.makeText(requireContext(), "Поставьте галочку", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSign.setOnClickListener {
            val name = binding.nameRegisterET.text.toString()
            val lastName = binding.lastNameRegisterET.text.toString()
            val phone = binding.phoneRegisterET.text.toString()
            val email = binding.editTextLogin.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.confirmPasswordET.text.toString()

            viewModel.signUpUser(name, lastName, phone, email, password, confirmPassword)
            signIn()

        }
        binding.loginButtonTV.setOnClickListener {
            findNavController().popBackStack()
           // requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun signIn() {
        progressDialogSignUp.show()
        progressDialogSignUp.setMessage("Регистрация пользователя...")
        viewModel.registrationStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            if (status == "Регистрация прошла успешно") {
                progressDialogSignUp.dismiss()
                findNavController().navigate(R.id.action_registerFragment_to_loginInputFragment)
//                val loginInputFragment = LoginInputFragment()
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.nav_host_fragment, loginInputFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
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
            if (error.toString() == "true") {
                progressDialogSignUp.dismiss()
            }

            error.observe(viewLifecycleOwner) { editText.error = it }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
