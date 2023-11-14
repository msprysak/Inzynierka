package com.msprysak.rentersapp.ui.reports

import ItemsDecorator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.ReportsAdapter
import com.msprysak.rentersapp.data.interfaces.OnReportItemClickListener
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.databinding.FragmentReportsBinding

class ReportsFragment : BaseFragment(), OnReportItemClickListener {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val reportsViewModel by viewModels<ReportsViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportsAdapter: ReportsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        val addReportButton = binding.addReportButton
        reportsViewModel.setupObserver()

        recyclerView = binding.reportsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
        recyclerView.addItemDecoration(itemDecoration)

        reportsViewModel.getReports().observe(viewLifecycleOwner) { reports ->
            reportsAdapter = ReportsAdapter(reports)
            recyclerView.adapter = reportsAdapter
            println(reports)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.addEditReportsFragment) {
                hideBottomNavigationView()
            } else {
                showBottomNavigationView()
            }
        }

        addReportButton.setOnClickListener {
            navController.navigate(ReportsFragmentDirections.actionReportsFragmentToAddEditReportsFragment())
        }
    }

    private fun hideBottomNavigationView() {
        val bottomNavigation =
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigation?.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        val bottomNavigation =
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigation?.visibility = View.VISIBLE
    }

    override fun onItemClick(reports: Reports) {

    }

}

