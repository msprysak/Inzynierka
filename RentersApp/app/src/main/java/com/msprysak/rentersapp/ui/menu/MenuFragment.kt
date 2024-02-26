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
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentMenuBinding
import com.msprysak.rentersapp.interfaces.BindUser

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

        val editProfileButton = binding.editProfileButton
        val addUsersTextView = binding.addUsersTextView
        val editHomeInfoTextView = binding.editHomeTextView
        val usersTextView = binding.usersTextView
        val reportsTextView = binding.reportsTextView
        val addHome = binding.addHomeCard
        val media = binding.mediaCard
        val contracts = binding.contractsCard
        val invoices = binding.invoicesCard

        menuViewModel.getUserData().observe(viewLifecycleOwner) { user ->
            bindUserData(user)
            if (user.houseRoles!!.containsValue("tenant")){
                addUsersTextView.visibility = View.GONE
                editHomeInfoTextView.text = resources.getString(R.string.show_home_info)
                addHome.visibility = View.GONE
            }
        }



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
        usersTextView.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToUsersFragment())
        }
        reportsTextView.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToReportsFragment())
        }
        addHome.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToAddPremisesFragment())
        }
        media.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToMediaFragment())
        }
        contracts.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToContractsFragment())
        }
        invoices.setOnClickListener{
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToInvoicesFragment())
        }




    }
    override fun bindUserData(user: User) {
        binding.username.text = user.username
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .circleCrop()
                .into(binding.profileImage)
        }
    }
}