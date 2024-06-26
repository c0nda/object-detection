package com.example.cowdetection.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cowdetection.R
import com.example.cowdetection.di.MainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentBase : Fragment() {

    companion object {
        const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private val component by lazy { MainScreenComponent.create() }

    private val baseViewModel by activityViewModels<BaseViewModel> { component.viewModelFactory() }

    private val resultLauncherGalleryPick =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it != null) {
                val photoUri = it.data?.data
                baseViewModel.saveImageUri(photoUri)
                val takePhoto = view?.findViewById<ImageButton>(R.id.takePhoto)
                takePhoto?.setImageResource(R.drawable.ic_camera)
                takePhoto?.setBackgroundResource(R.drawable.ic_circle_button_big_black)
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
            baseViewModel.setImageSource(fromCamera = false)
                val photoPickerIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncherGalleryPick.launch(photoPickerIntent)
        }

        takePhoto.setOnClickListener {
            if (baseViewModel.currentImage.value == null) {
                val imageCapture = baseViewModel.imageCapture.value!!
                baseViewModel.setImageSource(fromCamera = true)
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