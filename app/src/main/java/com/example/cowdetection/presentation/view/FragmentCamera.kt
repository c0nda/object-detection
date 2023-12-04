package com.example.cowdetection.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cowdetection.R
import com.example.cowdetection.di.DaggerMainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel

class FragmentCamera : Fragment() {

    private val component by lazy { DaggerMainScreenComponent.create() }

    private val baseViewModel by activityViewModels<BaseViewModel> { component.viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseViewModel.currentImage.observe(viewLifecycleOwner) {
            if (it != null) {
                navigateToFragmentImage()
            }

        }
    }

    private fun navigateToFragmentImage() {
        val action = FragmentCameraDirections.actionFragmentCameraToFragmentImage()
        findNavController().navigate(action)
    }
}