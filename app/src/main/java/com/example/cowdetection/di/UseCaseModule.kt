package com.example.cowdetection.di

import com.example.cowdetection.domain.usecase.ChoosePhotoFromGalleryUseCase
import com.example.cowdetection.domain.usecase.TakePhotoUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideChoosePhotoFromGalleryUseCase() = ChoosePhotoFromGalleryUseCase()

    @Provides
    fun provideTakePhotoUseCase() = TakePhotoUseCase()
}