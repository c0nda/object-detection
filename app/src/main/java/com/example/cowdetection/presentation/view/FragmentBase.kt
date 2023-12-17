package com.example.cowdetection.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cowdetection.DI
import com.example.cowdetection.R
import com.example.cowdetection.di.DaggerMainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentBase : Fragment() {

    companion object {
        const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    }

    private val component by lazy {
        DaggerMainScreenComponent.builder()
            .filePath(DI.appComponent.filePath())
            .imageAnalyzer(DI.appComponent.imageAnalyzer())
            .build()
    }

    private val baseViewModel by activityViewModels<BaseViewModel> { component.viewModelFactory() }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                baseViewModel.saveImageUri(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val choosePhoto = view.findViewById<ImageButton>(R.id.choosePhoto)
        val closeApp = view.findViewById<ImageButton>(R.id.closeApp)
        val takePhoto = view.findViewById<ImageButton>(R.id.takePhoto)

        closeApp.setOnClickListener {
            activity?.finish()
        }

        choosePhoto.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            resultLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        takePhoto.setOnClickListener {
            if (baseViewModel.currentImage.value == null) {
                val imageCapture = baseViewModel.imageCapture.value!!
                val photoFile = File(
                    baseViewModel.getOutputDirectory(),
                    SimpleDateFormat(
                        FILE_NAME_FORMAT,
                        Locale.getDefault()
                    ).format(System.currentTimeMillis()) + ".jpg"
                )

                val outputOption = ImageCapture
                    .OutputFileOptions
                    .Builder(photoFile)
                    .build()

                imageCapture.takePicture(
                    outputOption, ContextCompat.getMainExecutor(requireContext()),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val savedUri = Uri.fromFile(photoFile)
                            baseViewModel.saveImageUri(savedUri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )

                takePhoto.setImageResource(R.drawable.ic_camera)
                takePhoto.setBackgroundResource(R.drawable.ic_circle_button_big_black)
            } else {
                baseViewModel.saveImageUri(null)

                takePhoto.setImageResource(R.drawable.ic_photo_button)
                takePhoto.setBackgroundResource(R.drawable.ic_circle_button_big)
            }
        }
    }
}