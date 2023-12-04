package com.example.cowdetection.di

import androidx.lifecycle.ViewModel
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import com.example.cowdetection.presentation.viewmodel.BaseViewModelFactory
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@Component(modules = [MainScreenModule::class, UseCaseModule::class])
@ScreenScope
interface MainScreenComponent {

    fun viewModelFactory(): BaseViewModelFactory
}

@Module
abstract class MainScreenModule {

    @Binds
    @IntoMap
    @ViewModelKey(BaseViewModel::class)
    abstract fun baseViewModel(viewModel: BaseViewModel): ViewModel

}