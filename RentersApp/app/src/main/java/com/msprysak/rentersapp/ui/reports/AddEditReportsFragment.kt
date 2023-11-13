package com.msprysak.rentersapp.ui.reports

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.ImageHelper
import com.msprysak.rentersapp.adapters.ReportsPhotoAdapter
import com.msprysak.rentersapp.data.interfaces.CallBack
import com.msprysak.rentersapp.data.model.Reports
import com.msprysak.rentersapp.databinding.FragmentAddReportBinding
import kotlin.math.min

class AddEditReportsFragment : BaseFragment() {
    private var _binding: FragmentAddReportBinding? = null
    private val binding get() = _binding!!

    private val reportsViewModel by viewModels<ReportsViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: ReportsPhotoAdapter
    private val selectedImages = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReportBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addImagesButton = binding.btnAddImages
        val addReportButton = binding.btnAddReport
        val titleText = binding.reportTitle
        val descriptionText = binding.reportDescription

        recyclerView = binding.imagesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        photoAdapter = ReportsPhotoAdapter(selectedImages.toMutableList()) { image ->
            selectedImages.remove(image)
            photoAdapter.updateList(selectedImages)
        }
        recyclerView.adapter = photoAdapter

        addImagesButton.setOnClickListener {
            openGalleryForImages()
        }



        addReportButton.setOnClickListener {

            if (titleText.text.isNotBlank() || descriptionText.text.isNotBlank()) {
                val report = Reports(
                    reportTitle = titleText.text.toString(),
                    reportDescription = descriptionText.text.toString()
                )
                binding.btnAddReport.isEnabled = false
                reportsViewModel.createNewReport(report, selectedImages, object : CallBack {
                    override fun onSuccess() {
                        titleText.text.clear()
                        descriptionText.text.clear()
                        selectedImages.clear()
                        photoAdapter.updateList(selectedImages)
                        Toast.makeText(
                            requireContext(),
                            "Pomyślnie utworzono zgłoszenie",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnAddReport.isEnabled = true
                    }

                    override fun onFailure(errorMessage: String) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        binding.btnAddReport.isEnabled = true
                    }


                })
            } else {
                Toast.makeText(requireContext(), "Wypełnij wszystkie pola", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    private fun openGalleryForImages() {
        val galleryIntent = ImageHelper.getOpenGalleryIntent()
        galleryLauncher.launch(galleryIntent)
    }

    // Definicja launcher'a do obsługi wyników z galerii
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handlePickedImages(result.data)
            }
        }

    fun handlePickedImages(data: Intent?) {
        val maxImagesCount = 5
        if (data?.clipData != null) {
            val count = min(data.clipData!!.itemCount, maxImagesCount)
            for (i in 0 until count) {
                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                selectedImages.add(imageUri)
            }
        } else if (data?.data != null) {
            selectedImages.add(data.data!!)
        }

        photoAdapter.updateList(selectedImages)
    }
}




