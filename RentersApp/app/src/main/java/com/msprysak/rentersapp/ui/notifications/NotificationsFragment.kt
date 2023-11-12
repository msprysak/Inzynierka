package com.msprysak.rentersapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.NotificationAdapter
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.interfaces.OnItemClickListener
import com.msprysak.rentersapp.data.model.Request
import com.msprysak.rentersapp.databinding.FragmentNotificationsListBinding

class NotificationsFragment : BaseFragment(), OnItemClickListener {

    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private var _binding: FragmentNotificationsListBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Any>
    private lateinit var notificationAdapter: NotificationAdapter

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsListBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        notificationsViewModel.getJoinRequests()

        recyclerView = binding.notificationsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        println("Adapter set on RecyclerView")

        notificationsViewModel.getJoinRequests().observe(
            viewLifecycleOwner
        ) { joinRequests ->
            if (joinRequests != null) {
                println("joinRequests: $joinRequests")
                dataList = joinRequests as ArrayList<Any>
                notificationAdapter = NotificationAdapter(dataList, this)
                recyclerView.adapter = notificationAdapter
                notificationAdapter.notifyDataSetChanged()
            }
        }


    }

    override fun onLandlordClick(item: Any, anchorView: View) {
        val anchorView: View = requireView().findViewById(R.id.popupMenu)
        val popupMenu = PopupMenu(this.context, anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.notifications_add_user_popup, popupMenu.menu)

        if (item is Request) {
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
//                    Zaakceptuj prośbę o dołączenie
                    R.id.accept -> {
                         notificationsViewModel.acceptJoinRequest(item, object: CallBack {
                             override fun onSuccess() {
                                 Toast.makeText(requireContext(), "Prośba o dołączenie została zaakceptowana", Toast.LENGTH_SHORT).show()
                             }

                             override fun onFailure(errorMessage: String) {
                                 Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                             }

                         })
                        true
                    }
//                    Odrzuć prośbę o dołączenie

                    R.id.decline -> {
                         notificationsViewModel.rejectJoinRequest(item, object: CallBack {
                             override fun onSuccess() {
                                 Toast.makeText(requireContext(), "Prośba o dołączenie została odrzucona", Toast.LENGTH_SHORT).show()
                             }

                             override fun onFailure(errorMessage: String) {
                                 Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                             }

                         })
                        true
                    }

                    else -> false
                }
            }

            // Pokaż PopupMenu obok przycisku
            popupMenu.show()
        }
    }


    override fun onTenantClick(item: Any, anchorView: View) {
        TODO("Not yet implemented")
    }
}