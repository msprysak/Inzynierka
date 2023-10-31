package com.msprysak.rentersapp.ui.createhome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.FirebaseRepository
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

//        To próba jeszcze tego nie uzyawlem
        binding.welcomeTextView.text = getString(R.string.welcome_message, createHomeViewModel.userData.value?.username)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createHomeButton = binding.btnCreateHome
        val joinHomeButton = binding.btnJoinHome

//        createHomeViewModel.userData.observe(viewLifecycleOwner) { user ->
//            binding.welcomeTextView.text = getString(R.string.welcome_message, user.username)
//        }



        // bind button click
        createHomeButton.setOnClickListener {
            val createHomeDialog = AddHomeDialogFragment()

            createHomeDialog.show(childFragmentManager, "CreateHomeDialogFragment")

            Toast.makeText(context, "Create Home Button Clicked", Toast.LENGTH_SHORT).show()
        }
        joinHomeButton.setOnClickListener{
            Toast.makeText(context, "Join Home Button Clicked", Toast.LENGTH_SHORT).show()
        }

//        createHomeButton.setOnClickListener()

    }

    private fun bindUserData(user: User){
        Log.d(LOG_DEBUG, user.toString())
    }

}