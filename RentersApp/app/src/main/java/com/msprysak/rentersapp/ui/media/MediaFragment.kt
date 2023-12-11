package com.msprysak.rentersapp.ui.media

import ItemsDecorator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.MediaAdapter
import com.msprysak.rentersapp.data.interfaces.OnItemClick
import com.msprysak.rentersapp.data.model.Media
import com.msprysak.rentersapp.databinding.FragmentReportsBinding

class MediaFragment : BaseFragment(), OnItemClick{

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val mediaViewModel by viewModels<MediaViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var reportsAdapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        mediaViewModel.setupMediaListener()
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addReportButton.setOnClickListener {

            val navController = findNavController()
            navController.navigate(R.id.action_mediaFragment_to_addMediaFragment)
        }

        recyclerView = binding.reportsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        val itemDecoration = ItemsDecorator(requireContext(), R.dimen.item_space)
        recyclerView.addItemDecoration(itemDecoration)

        mediaViewModel.mediaList.observe(viewLifecycleOwner) {medialist ->
            reportsAdapter = MediaAdapter(medialist,this )
            recyclerView.adapter = reportsAdapter
            reportsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: Any) {

        val navController = findNavController()
        val action = MediaFragmentDirections.actionMediaFragmentToFullMediaFragment(item as Media)
        navController.navigate(action)
    }
}