package com.example.cowdetection.domain.usecase

import com.example.cowdetection.domain.repository.CameraRepository

class TakePhotoUseCase(private val cameraRepository: CameraRepository) {

    operator fun invoke() {
        return cameraRepository.takePhoto()
    }
    
}