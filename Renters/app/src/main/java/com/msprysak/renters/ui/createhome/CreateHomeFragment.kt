package com.msprysak.rentersapp.ui.createhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.msprysak.rentersapp.BaseFragment
import com.msprysak.rentersapp.databinding.FragmentCreateHomeBinding

class CreateHomeFragment: BaseFragment() {

    private lateinit var createHomeViewModel: CreateHomeViewModel
    private var _binding: FragmentCreateHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentCreateHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}