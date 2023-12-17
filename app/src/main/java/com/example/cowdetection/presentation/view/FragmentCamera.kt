package com.example.cowdetection.presentation.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

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
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previewView = view.findViewById<PreviewView>(R.id.camera)

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture?.addListener({
            try {
                val cameraProvider = cameraProviderFuture?.get()
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageCapture = ImageCapture.Builder().build()
                baseViewModel.saveImageCapture(imageCapture)

                preview.setSurfaceProvider(previewView?.surfaceProvider)

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
}