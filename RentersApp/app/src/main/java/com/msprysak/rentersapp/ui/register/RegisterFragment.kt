package com.msprysak.rentersapp.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.databinding.FragmentRegisterBinding
import com.msprysak.rentersapp.util.createClickableSpan


class RegisterFragment : BaseFragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val LOG_DEBUG = "RegisterFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        auth = Firebase.auth


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)


        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText
        val confirmPasswordEditText = binding.confirmPasswordEditText
        val registerButton = binding.registerButton
        val loginText =  binding.accountSetTextView

        registerButton.isEnabled = false
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_warning)
        icon?.setBounds(0,0, icon.intrinsicWidth, icon.intrinsicHeight)

//        RegisterViewModel.registerFormState.observe observes the LiveData object in the RegisterViewModel class.
        registerViewModel.registerFormState.observe(viewLifecycleOwner
        ) { registerFormState ->
            if (registerFormState == null) {
                return@observe
            }
            registerButton.isEnabled = registerFormState.isDataValid
            registerFormState.emailAddressError?.let {
                binding.emailEditText.setError(getString(it),icon)
            }
            registerFormState.passwordError?.let {
                binding.passwordEditText.setError(getString(it),icon)
            }
            registerFormState.confirmPasswordError?.let {
                binding.confirmPasswordEditText.setError(getString(it),icon)
            }
        }
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registerViewModel.registerDataChanged(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    confirmPasswordEditText.text.toString()
                )
            }
        }
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener)

        registerButton.setOnClickListener {
            signupClicked(emailEditText.text.toString(), passwordEditText.text.toString())
        }
        createClickableSpan(loginText, "Zaloguj się!", RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())

    }

    fun signupClicked(email: String, password: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener { authResult ->
                if (authResult.user != null) {
                    startApp()
                    Log.d(LOG_DEBUG, "Successfully created user with uid: ${authResult.user?.uid}")
                }
            }
            .addOnFailureListener{exc ->
                Log.d(LOG_DEBUG, "Failed to create user: ${exc.message}")
            }
    }
}