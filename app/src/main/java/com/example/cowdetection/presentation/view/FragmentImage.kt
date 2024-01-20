package com.example.cowdetection.presentation.view

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cowdetection.R
import com.example.cowdetection.di.MainScreenComponent
import com.example.cowdetection.presentation.ResultView
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import com.example.cowdetection.utils.prepostprocessor.model.AnalysisResult
import kotlinx.coroutines.launch

class FragmentImage : Fragment() {

    private val component by lazy { MainScreenComponent.create() }

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
        val resultView = view.findViewById<ResultView>(R.id.resultView)

        baseViewModel.currentImage.observe(viewLifecycleOwner) {
            if (it == null) {
                navigateToFragmentCamera()
            } else {
                view.post {
                    var result: AnalysisResult? = null

                    val sourceBitmap = MediaStore.Images.Media.getBitmap(
                        context?.contentResolver,
                        it
                    )
                    val bitmap = baseViewModel.createScaledBitmap(
                        sourceBitmap,
                        fromCamera = baseViewModel.imageFromCamera.value!!
                    )

                    baseViewModel.setImageSource(fromCamera = null)
                    photo.setImageBitmap(bitmap)

                    try {
                        lifecycleScope.launch {
                            result = baseViewModel.analyzeImage(
                                bitmap,
                                view.width,
                                view.height,
                                sourceBitmap.width,
                                sourceBitmap.height
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("analyzer", e.printStackTrace().toString())
                    }

                    resultView.results = result
                    resultView.invalidate()
                    resultView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToFragmentCamera() {
        val action = FragmentImageDirections.actionFragmentImageToFragmentCamera()
        findNavController().navigate(action)
    }
}