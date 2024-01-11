package com.example.cowdetection.presentation.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cowdetection.DI
import com.example.cowdetection.R
import com.example.cowdetection.di.DaggerMainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutionException

class FragmentCamera : Fragment() {

    companion object {
        const val REQUESTED_CODE_PERMISSION = 101
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    private val component by lazy {
        DaggerMainScreenComponent.builder()
            .filePath(DI.appComponent.filePath())
            .imageAnalyzer(DI.appComponent.imageAnalyzer())
            .prePostProcessor(DI.appComponent.prePostProcessor())
            .contentResolver(DI.appComponent.contentResolver())
            .build()
    }

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

        val previewView = view.findViewById<PreviewView>(R.id.camera)
        val flashButton = view.findViewById<ImageButton>(R.id.ibFlash)

        startCamera(previewView)

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUESTED_CODE_PERMISSION
            )
        }

        baseViewModel.currentImage.observe(viewLifecycleOwner) {
            if (it != null) {
                navigateToFragmentImage()
            }
        }

        flashButton.setOnClickListener {
            when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode = ImageCapture.FLASH_MODE_ON
                    flashButton.setImageResource(R.drawable.ic_flash_on)
                }

                ImageCapture.FLASH_MODE_ON -> {
                    flashMode = ImageCapture.FLASH_MODE_OFF
                    flashButton.setImageResource(R.drawable.ic_flash_off)
                }
            }

            val imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .setTargetResolution(Size(640, 640))
                .build()

            baseViewModel.saveImageCapture(imageCapture)
            startCamera(previewView)
        }
    }

    private fun navigateToFragmentImage() {
        val action = FragmentCameraDirections.actionFragmentCameraToFragmentImage()
        findNavController().navigate(action)
    }

    private fun checkPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startCamera(previewView: PreviewView?) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture?.addListener({
            val cameraProvider = cameraProviderFuture?.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageCapture = baseViewModel.getImageCapture()!!

            preview.setSurfaceProvider(previewView?.surfaceProvider)

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }
}