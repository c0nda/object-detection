package com.example.cowdetection.di

import com.example.cowdetection.domain.usecase.TakePhotoUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideTakePhotoUseCase() = TakePhotoUseCase()
}