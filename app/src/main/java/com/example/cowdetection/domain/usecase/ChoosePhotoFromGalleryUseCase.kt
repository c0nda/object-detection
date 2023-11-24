package com.example.cowdetection.domain.usecase

import com.example.cowdetection.domain.repository.GalleryRepository

class ChoosePhotoFromGalleryUseCase(private val galleryRepository: GalleryRepository) {

    operator fun invoke() {
        return galleryRepository.choosePhotoFromGallery()
    }
}