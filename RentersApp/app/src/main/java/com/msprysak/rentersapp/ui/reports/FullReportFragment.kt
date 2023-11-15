package com.msprysak.rentersapp.ui.reports

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.ReportsPhotoAdapter
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentFullReportBinding
import java.util.Date
import java.util.Locale

class FullReportFragment: BaseFragment() {

    private var _binding: FragmentFullReportBinding? = null
    private val binding get() = _binding!!

    private val reportsViewModel by viewModels<ReportsViewModel>()


    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: ReportsPhotoAdapter
    private val selectedImages = mutableListOf<Uri>()


    private lateinit var report: Reports
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        report = arguments?.getParcelable("report")!!

        user = arguments?.getParcelable("user")!!

        println("FullReportFragment $report")
        println("FullReportFragment $user")



        _binding = FragmentFullReportBinding.inflate(inflater, container, false)
        return _binding!!.root


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reportTitle.text = report.reportTitle
        binding.reportDescription.text = report.reportDescription
        binding.reporterName.text = user.username
        binding.reporterPhoneNumber.text = user.phoneNumber

        val statusSpinner: Spinner = binding.statusSpinner
        val stringAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.report_status_string_array,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )

        stringAdapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)

        statusSpinner.adapter = stringAdapter


        val javaUtilDate = Date(report.reportDate!!.toDate().time)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        binding.reportDate.text = dateFormat.format(javaUtilDate)
        selectedImages.clear()
        val uriList: List<Uri> = report.reportImages.map { Uri.parse(it) }
        selectedImages.addAll(uriList)


        Glide.with(this)
            .load(user.profilePictureUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.reporterImage)

        recyclerView = binding.imagesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        photoAdapter = ReportsPhotoAdapter(selectedImages.toMutableList(), false,{}) { image ->
            val action = FullReportFragmentDirections.actionFullReportFragmentToImageFullScreen(image.toString())
                findNavController().navigate(action)
        }
        recyclerView.adapter = photoAdapter


        println("FullReportFragment onViewCreated")
    }


}