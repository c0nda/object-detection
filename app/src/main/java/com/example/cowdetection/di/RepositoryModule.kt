package com.example.cowdetection.di

import com.example.cowdetection.data.repository.CameraRepositoryImpl
import com.example.cowdetection.data.repository.GalleryRepositoryImpl
import com.example.cowdetection.domain.repository.CameraRepository
import com.example.cowdetection.domain.repository.GalleryRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindCameraRepository(cameraRepositoryImpl: CameraRepositoryImpl): CameraRepository

    @Binds
    fun bindGalleryRepository(galleryRepositoryImpl: GalleryRepositoryImpl): GalleryRepository
}