package com.msprysak.rentersapp.ui.reports

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.databinding.FragmentPhotoFullScreenBinding

class ImageFullScreen: BaseFragment() {

    private var _binding: FragmentPhotoFullScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoFullScreenBinding.inflate(inflater, container, false)
        hideBottomNavigationView()
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  imageUrl = arguments?.getString("imageUri")
        Glide.with(this)
            .load(Uri.parse(imageUrl))
            .apply(RequestOptions().fitCenter())
            .into(binding.photoFullScreen)
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
    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
        _binding = null
    }
}