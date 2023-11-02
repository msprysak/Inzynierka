package com.msprysak.rentersapp.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.BindUser
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentMenuBinding

class MenuFragment : BaseFragment(), BindUser {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private val menuViewModel by viewModels<MenuViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        return _binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuViewModel.userData.observe(viewLifecycleOwner) { user ->
            bindUserData(user)
        }

        val editProfileButton = binding.editProfileButton
        val addUsersTextView = binding.addUsersTextView
        val editHomeInfoTextView = binding.editHomeTextView
        val navController = findNavController()
        val currentDestinationId = navController.currentDestination?.id
        Log.d("CurrentDestination", "Current destination ID: $currentDestinationId")

        addUsersTextView.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToAddUsersFragment())

        }
        editProfileButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToProfileFragment())
        }
        editHomeInfoTextView.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToHomeFragment())
        }

    }
    override fun bindUserData(user: User) {
        binding.username.text = user.username
        if (!user.profilePictureUrl?.isEmpty()!!) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .circleCrop()
                .into(binding.profileImage)
        }
    }
}