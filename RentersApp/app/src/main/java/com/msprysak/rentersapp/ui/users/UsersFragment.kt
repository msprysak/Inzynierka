package com.msprysak.rentersapp.ui.users

import ItemsDecorator
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.TenantsAdapter
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentUsersListBinding
import com.msprysak.rentersapp.interfaces.OnItemClickListener

class UsersFragment : BaseFragment(), OnItemClickListener {
    private var _binding: FragmentUsersListBinding? = null

    private lateinit var tenantsAdapter: TenantsAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList: MutableList<User> = mutableListOf()
    private val binding get() = _binding!!
    private val usersViewModel by viewModels<UsersViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersListBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.usersRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)


        usersViewModel.fetchUsers().observe(viewLifecycleOwner) { users ->
            println("users: $users")

            usersList.clear()
            usersList.addAll(users)
            val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
            recyclerView.addItemDecoration(itemDecoration)
            tenantsAdapter = TenantsAdapter(
                usersList,
                this,
                usersViewModel.userRepository.user.value!!.houseRoles!!.entries.first().value
            )
            recyclerView.adapter = tenantsAdapter
            tenantsAdapter.notifyDataSetChanged()
        }
    }


    override fun onLandlordClick(item: Any, anchorView: View) {
        item as User
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.users_landlord_popup, popupMenu.menu)
        popupMenu.menu.findItem(R.id.delete).isVisible = !item.houseRoles!!.containsValue("landlord")
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {


                    AlertDialog.Builder(requireContext())
                        .setMessage("Czy na pewno chcesz usunąć użytkownika ,,${item.username}''?")
                        .setPositiveButton(R.string.delete) { _, _ ->
                            usersViewModel.deleteUser(item)
                            Toast.makeText(requireContext(), "Pomyślnie usunięto użytkownika.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton(R.string.cancel){
                                dialog, _ -> dialog.dismiss()
                        }
                        .show()
                    true
                }
                R.id.preview -> {
                    findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToUserInfoFragment(item))
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onTenantClick(item: Any, anchorView: View) {
        item as User
        val popupMenu = PopupMenu(requireContext(), anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.users_user_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.preview -> {
                    Toast.makeText(
                        requireContext(),
                        "Podgląd użytkownika",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}