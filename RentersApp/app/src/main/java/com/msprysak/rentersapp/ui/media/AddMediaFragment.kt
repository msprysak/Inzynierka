package com.msprysak.rentersapp.ui.media

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.ImageHelper
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.ReportsPhotoAdapter
import com.msprysak.rentersapp.interfaces.CallBack
import com.msprysak.rentersapp.databinding.FragmentAddMediaBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.min

class AddMediaFragment : BaseFragment() {

    private var _binding: FragmentAddMediaBinding? = null
    private val binding get() = _binding!!

    private val mediaViewModel by viewModels<MediaViewModel>()


    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: ReportsPhotoAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMediaBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateButton = binding.addDatesButton
        val saveButton = binding.saveButton
        val addImagesButton = binding.addImagesButton

        recyclerView = binding.imagesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        saveButton.setOnClickListener {

        }
        photoAdapter = ReportsPhotoAdapter(mediaViewModel.selectedImages, true, { image ->
            mediaViewModel.selectedImages.value?.let { images ->
                mediaViewModel.selectedImages.value = images.filter { it != image }
            }
            photoAdapter.updateList(mediaViewModel.selectedImages.value!!)
        }) {}
        recyclerView.adapter = photoAdapter

        mediaViewModel.media.observe(viewLifecycleOwner) {
            println(it)
            binding.mediaTitle.setText(it.mediaTitle)
            if (it.mediaDate != null) {
                showMediaDate(Date(it.mediaDate!!.time))
            }
            binding.startPaymentDate.text = (it.mediaDate.toString())
            if (mediaViewModel.media.value!!.mediaImages.isNotEmpty()) {

                mediaViewModel.selectedImages.value =
                    mediaViewModel.media.value!!.mediaImages.map { Uri.parse(it) }
            }

        }
        titleTextWatcher()

        saveButton.setOnClickListener{
            if (mediaViewModel.media.value!!.mediaImages.isNotEmpty()) {
                mediaViewModel.createNewMedia(
                    mediaViewModel.media.value!!,
                    mediaViewModel.selectedImages.value!!,
                    object : CallBack {
                        override fun onSuccess() {
                            Toast.makeText(
                                requireContext(),
                                "Dodano media",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(errorMessage: String) {
                            Toast.makeText(
                                requireContext(),
                                "Nie udało się dodać mediów",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }

        }

        addImagesButton.setOnClickListener {
            openGalleryForImages()
        }
        dateButton.setOnClickListener {
            datePicker()
        }
    }


    private fun titleTextWatcher() {
        binding.mediaTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Niepotrzebna implementacja
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Niepotrzebna implementacja

            }

            override fun afterTextChanged(s: Editable?) {
                mediaViewModel.setMediaTitle(s.toString())
            }
        })
    }

    private fun openGalleryForImages() {
        val galleryIntent = ImageHelper.getOpenGalleryIntent()
        galleryLauncher.launch(galleryIntent)
    }

    //     Definicja launcher'a do obsługi wyników z galerii
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handlePickedImages(result.data)
            }
        }

    private fun handlePickedImages(data: Intent?) {
        val maxImagesCount = 5
        val updatedList = mutableListOf<Uri>()

        if (data?.clipData != null) {
            val count = min(data.clipData!!.itemCount, maxImagesCount)
            for (i in 0 until count) {
                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                updatedList.add(imageUri)
            }
        } else if (data?.data != null) {
            updatedList.add(data.data!!)
        }

        // Ustaw nową wartość dla selectedImages
        mediaViewModel.selectedImages.value = updatedList
        mediaViewModel.media.value!!.mediaImages = updatedList.map { it.toString() }
        photoAdapter.updateList(mediaViewModel.selectedImages.value!!)
    }

    private fun datePicker() {
        val minDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(resources.getString(R.string.select_payment_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(minDate)
                    .setEnd(System.currentTimeMillis())
                    .setValidator(DateValidatorPointForward.now())
                    .build()
            )
            .build()

        datePicker.show(childFragmentManager, "date_range_picker")
        datePicker.addOnPositiveButtonClickListener {
            val startDate = datePicker.selection


            if (startDate != null) {

                mediaViewModel.setMediaDate(Date(startDate))


                showMediaDate(Date(startDate))
            }
        }


    }

    private fun showMediaDate(date: Date) {
        val formattedStartDate =
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        binding.startPaymentDate.text = formattedStartDate
        binding.startPaymentDate.visibility = View.VISIBLE
        binding.startPaymentDateLabel.visibility = View.VISIBLE
    }

}