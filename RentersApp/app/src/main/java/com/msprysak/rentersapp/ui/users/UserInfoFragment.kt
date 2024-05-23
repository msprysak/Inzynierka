package com.msprysak.rentersapp.ui.users

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentUserInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserInfoFragment: BaseFragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private lateinit var user: User
    private val usersViewModel by viewModels<UsersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = arguments?.getParcelable("user")!!
        setupUI()

        setupSavebutton()
        _binding!!.usernameText.addTextChangedListener(afterTextChangedListener)
        _binding!!.peselNipText.addTextChangedListener(afterTextChangedListener)
        _binding!!.emailAddressText.addTextChangedListener(afterTextChangedListener)
        _binding!!.phoneNumberEditText.addTextChangedListener(afterTextChangedListener)
        _binding!!.sellerStreet.addTextChangedListener(afterTextChangedListener)
        _binding!!.sellerPostalCode.addTextChangedListener(afterTextChangedListener)
        _binding!!.sellerTown.addTextChangedListener(afterTextChangedListener)

    }

    suspend fun updateUserInfo(sellerNameSurname: String, sellerNip: String, sellerStreet: String, sellerPostalCode: String, sellerCity: String) {
        val userInfo = com.msprysak.rentersapp.data.repositories.room.UserInfo(
            id = user.userId!!,
            userNameSurname = sellerNameSurname,
            userNipPesel = sellerNip,
            userStreet = sellerStreet,
            userPostalCode = sellerPostalCode,
            userCity = sellerCity,
            premisesId = usersViewModel.premisesRepository.premises.value!!.premisesId.toString()
        )
    }

    val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable) {
            _binding!!.saveButton.isEnabled = true
        }
    }
    private fun setupSavebutton() {
        val binding = _binding!!
        binding.saveButton.isEnabled = false
        binding.saveButton.setOnClickListener {
            val sellerNameSurname = binding.usernameText.text.toString()
            val sellerNip = binding.peselNipText.text.toString()
            val sellerStreet = binding.sellerStreet.text.toString()
            val sellerPostalCode = binding.sellerPostalCode.text.toString()
            val sellerCity = binding.sellerTown.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                updateUserInfo(sellerNameSurname, sellerNip, sellerStreet, sellerPostalCode, sellerCity)
            }
            binding.saveButton.isEnabled = false
        }
    }
    private fun setupUI() {
        val binding = _binding!!
        binding.usernameText.setText(user.username)
        binding.emailAddressText.setText(user.email)
        binding.phoneNumberEditText.setText(user.phoneNumber)
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .circleCrop()
                .into(binding.userImageView)
        }
    }
}