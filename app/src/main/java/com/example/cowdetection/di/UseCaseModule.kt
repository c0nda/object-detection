package com.example.cowdetection.di

import com.example.cowdetection.domain.repository.CameraRepository
import com.example.cowdetection.domain.repository.GalleryRepository
import com.example.cowdetection.domain.usecase.ChoosePhotoFromGalleryUseCase
import com.example.cowdetection.domain.usecase.TakePhotoUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideChoosePhotoFromGalleryUseCase(galleryRepository: GalleryRepository) =
        ChoosePhotoFromGalleryUseCase(galleryRepository)

    @Provides
    fun provideTakePhotoUseCase(cameraRepository: CameraRepository) =
        TakePhotoUseCase(cameraRepository)
}