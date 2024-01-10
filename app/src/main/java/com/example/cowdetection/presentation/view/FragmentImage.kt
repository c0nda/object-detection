package com.example.cowdetection.presentation.view

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cowdetection.DI
import com.example.cowdetection.R
import com.example.cowdetection.di.DaggerMainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class FragmentImage : Fragment() {

    private val component by lazy {
        DaggerMainScreenComponent.builder()
            .filePath(DI.appComponent.filePath())
            .imageAnalyzer(DI.appComponent.imageAnalyzer())
            .build()
    }

    private val baseViewModel by activityViewModels<BaseViewModel> { component.viewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = view.findViewById<ImageView>(R.id.image)
        val information = view.findViewById<TextView>(R.id.ivInformation)

        baseViewModel.currentImage.observe(viewLifecycleOwner) {
            if (it == null) {
                navigateToFragmentCamera()
            } else {
                photo.setImageURI(baseViewModel.currentImage.value)
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    baseViewModel.currentImage.value
                )
                try {
                    lifecycleScope.launch {
                        val result = baseViewModel.analyzeBitmap(bitmap)
                        information.text = result.toString()
                    }
                } catch (e: Exception) {
                    Log.e("analyzer", e.printStackTrace().toString())
                }
            }
        }
    }

    private fun navigateToFragmentCamera() {
        val action = FragmentImageDirections.actionFragmentImageToFragmentCamera()
        findNavController().navigate(action)
    }
}