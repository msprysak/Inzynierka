package com.msprysak.rentersapp.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.databinding.FragmentRegisterBinding
import com.msprysak.rentersapp.utils.createClickableSpan


class RegisterFragment : BaseFragment() {

    private val registerViewModel by viewModels<RegisterViewModel>()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val LOG_DEBUG = "RegisterFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        auth = Firebase.auth


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val usernameEditText = binding.username
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        val confirmPasswordEditText = binding.confirmPasswordEditText
        val registerButton = binding.registerButton
        val loginText =  binding.accountSetTextView

        registerButton.isEnabled = false

        registerViewModel.registerFormState.observe(viewLifecycleOwner
        ) { registerFormState ->
            if (registerFormState == null) {
                return@observe
            }
            registerButton.isEnabled = true

            registerFormState.usernameError?.let {
                binding.usernameTextInputLayout.helperText = getString(it)
            }
            registerFormState.emailAddressError?.let {
                binding.emailTextInputLayout.helperText = getString(it)
            }
            registerFormState.passwordError?.let {
                binding.passwordTextInputLayout.helperText = getString(it)
            }
            registerFormState.confirmPasswordError?.let {
                binding.confirmPasswordTextInputLayout.helperText = getString(it)
            }
            registerFormState.signupError?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
            if (registerFormState.signupSuccess != null)
                registerFormState.signupSuccess?.let {
                    if (it) {
                        startApp()
                        Toast.makeText(context, R.string.register_success, Toast.LENGTH_SHORT).show()
                    }
                }
            registerFormState.signupSuccess?.let {
                if (it) {
                    startApp()
                    Toast.makeText(context, R.string.register_success, Toast.LENGTH_SHORT).show()
                }
            }

        }



        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val username = usernameEditText.text.toString()
                binding.usernameTextInputLayout.isHelperTextEnabled = false
                val email = emailEditText.text.toString()
                binding.emailTextInputLayout.isHelperTextEnabled = false
                val password = passwordEditText.text.toString()
                binding.passwordTextInputLayout.isHelperTextEnabled = false
                val confirmPassword = confirmPasswordEditText.text.toString()
                binding.confirmPasswordTextInputLayout.isHelperTextEnabled = false
                registerViewModel.usernameDataChanged(username)
                registerViewModel.emailDataChanged(email)
                registerViewModel.passwordDataChanged(password)
                registerViewModel.confirmPasswordDataChanged(password, confirmPassword)
            }
        }

        usernameEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener)

        registerButton.setOnClickListener {
            registerViewModel.signupClicked(usernameEditText.text.toString(),emailEditText.text.toString(), passwordEditText.text.toString())
        }


        createClickableSpan(loginText, "Zaloguj się!", RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())

    }


}