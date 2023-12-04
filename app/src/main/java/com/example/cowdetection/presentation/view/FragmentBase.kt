package com.example.cowdetection.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cowdetection.R
import com.example.cowdetection.di.DaggerMainScreenComponent
import com.example.cowdetection.presentation.viewmodel.BaseViewModel

class FragmentBase : Fragment() {

    private val component by lazy { DaggerMainScreenComponent.create() }

    private val baseViewModel by activityViewModels<BaseViewModel> { component.viewModelFactory() }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            baseViewModel.save(uri)
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
    }
}