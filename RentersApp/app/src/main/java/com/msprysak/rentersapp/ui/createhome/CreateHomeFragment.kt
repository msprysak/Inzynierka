package com.msprysak.rentersapp.ui.createhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentCreateHomeBinding

class CreateHomeFragment: BaseFragment() {

    private val LOG_DEBUG = "CreateHomeFragment"
    private val createHomeViewModel by viewModels<CreateHomeViewModel>()

    private var _binding: FragmentCreateHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentCreateHomeBinding.inflate(inflater, container, false)

        createHomeViewModel.getUserData().observe(viewLifecycleOwner){ user ->
            bindUserData(user)}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createHomeButton = binding.btnCreateHome
        val joinHomeButton = binding.btnJoinHome

        createHomeButton.setOnClickListener {
            val createHomeDialog = AddHomeDialogFragment()
            createHomeDialog.show(childFragmentManager, "CreateHomeDialogFragment")

        }

        joinHomeButton.setOnClickListener {
            val enterCodeDialog = EnterHomeCodeDialogFragment()
            enterCodeDialog.show(childFragmentManager, "EnterHomeCodeDialogFragment")
        }


    }

    private fun bindUserData(user: User){
        binding.welcomeTextView.text = getString(R.string.welcome_message, user.username)
    }

}