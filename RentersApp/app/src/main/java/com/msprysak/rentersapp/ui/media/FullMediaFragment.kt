package com.msprysak.rentersapp.ui.media

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.adapters.ReportsPhotoAdapter
import com.msprysak.rentersapp.data.formatDate
import com.msprysak.rentersapp.data.model.Media
import com.msprysak.rentersapp.data.model.User
import com.msprysak.rentersapp.databinding.FragmentFullMediaBinding

class FullMediaFragment: BaseFragment() {

    private var _binding: FragmentFullMediaBinding? = null
    private val binding get() = _binding!!

    private val mediaViewModel by viewModels<MediaViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: ReportsPhotoAdapter
    private val selectedImages = MutableLiveData<List<Uri>>()

    private lateinit var media: Media
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullMediaBinding.inflate(inflater, container, false)

        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        media = arguments?.getParcelable("media")!!
        user = arguments?.getParcelable("user")!!

        setupUI()
        setupImagesRecyclerView()


    }

    private fun setupUI(){
        binding.mediaTitle.text = media.mediaTitle
        binding.mediaCreationDate.text = formatDate.formatDate(media.creationDate!!)
        binding.photoTakenDate.text = formatDate.formatDate(media.mediaDate!!)
        Glide.with(this)
            .load(user.profilePictureUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.reporterImage)
        binding.reporterName.text = user.username

        binding.deleteReportButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Czy na pewno chcesz trwale usunąć media ,,${media.mediaTitle}''?")
                .setPositiveButton(R.string.delete) { _, _ ->
                    Toast.makeText(requireContext(), "Usunięto", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.cancel){
                        dialog, _ -> dialog.dismiss()
                }
                .show()
        }

    }

    private fun setupImagesRecyclerView() {
        recyclerView = binding.imagesRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val uriList: List<Uri> = media.mediaImages.map { Uri.parse(it) }
        selectedImages.value = uriList

        photoAdapter = ReportsPhotoAdapter(selectedImages, false, {}) { image ->

            val action = FullMediaFragmentDirections.actionFullMediaFragmentToImageFullScreen(image.toString())
            findNavController().navigate(action)
        }
        recyclerView.adapter = photoAdapter
    }

}