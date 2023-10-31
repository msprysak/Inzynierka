package com.msprysak.rentersapp.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.data.FirebaseRepository
import com.msprysak.rentersapp.data.RepositorySingleton
import com.msprysak.rentersapp.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment() {


    private val  LOG_DEBUG = "LoginFragment"
    private val fbAuth = FirebaseAuth.getInstance()

//    private lateinit var loginViewModel: LoginViewModel
    private val loginViewModel by viewModels<LoginViewModel>()
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val repository = RepositorySingleton.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText = binding.email
        val passwordEditText = binding.password
        val loginButton = binding.login
        loginButton.isEnabled = false

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.emailError?.let {
                    emailEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)



        loginButton.setOnClickListener {
            setupLoginClick(emailEditText.text?.trim().toString(), passwordEditText.text?.trim().toString())
        }

    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome) + model.displayName
//        // TODO : initiate successful logged in experience
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
//    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun setupLoginClick(email:String, pass:String){

        fbAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {authRes ->
                if (authRes.user != null){
                    repository.fetchUserData()
                    println("LoginClick: ${repository.sharedUserData.value?.userId}")
                    // TODO:
                    //  JESLI UZYTKOWNIK NIE MA DOMU TO PRZEKIERUJ DO CREATEHOMEFRAGMENT
                    //  JEŚLI UŻYTKOWNIK JEST PRZYPISANY DO DOMU TO PRZEKIERUJ DO MAINACTIVITY
                    startApp()
                }
            }
            .addOnFailureListener { exc ->
                Toast.makeText(requireContext(), exc.message.toString(), Toast.LENGTH_LONG).show()
                Log.d(LOG_DEBUG, "setupLoginClick: ${exc.message}")
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}