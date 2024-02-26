package com.msprysak.rentersapp.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
import com.msprysak.rentersapp.R
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
        passwordEditText.hint = getString(R.string.password)
        val loginButton = binding.login
        loginButton.isEnabled = false

        forgotPassword()

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
                    emailEditText.text.toString()
                )
            }
        }
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)



        loginButton.setOnClickListener {
            setupLoginClick(emailEditText.text?.trim().toString(), passwordEditText.text?.trim().toString())
        }

    }

    private fun forgotPassword(){
        val textView = binding.forgotPasswordTextView

        val spannableString = SpannableStringBuilder(textView.text.toString())
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openForgotPasswordDialog(widget.context)
                Toast.makeText(requireContext(), resources.getString(R.string.forgot_password), Toast.LENGTH_LONG).show()
            }
        }

        spannableString.setSpan(clickableSpan, 0, textView.text.length, 0)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openForgotPasswordDialog(context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_forgot_password, null)
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.remind_password))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.reset_password)) { dialog, _ ->
                val email = view.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.emailEditText).text.toString()
                if (email.isEmpty() || email.isBlank()){
                    Toast.makeText(context, "Niepoprawny adres email!", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                } else{
                    fbAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(context, resources.getString(R.string.email_sent), Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    dialog.dismiss()
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun setupLoginClick(email:String, pass:String){

        fbAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {authRes ->
                if (authRes.user != null){
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