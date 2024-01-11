package com.example.cowdetection.di

import androidx.lifecycle.ViewModel
import com.example.cowdetection.presentation.viewmodel.BaseViewModel
import com.example.cowdetection.presentation.viewmodel.BaseViewModelFactory
import com.example.cowdetection.utils.contentresolver.ContentResolverProvider
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.imageanalyzer.ImageAnalyzer
import com.example.cowdetection.utils.prepostprocessor.PrePostProcessor
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap

@Component(modules = [MainScreenModule::class])
@ScreenScope
interface MainScreenComponent {

    fun viewModelFactory(): BaseViewModelFactory

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun filePath(filePathProvider: FilePathProvider): Builder

        @BindsInstance
        fun imageAnalyzer(imageAnalyzer: ImageAnalyzer): Builder

        @BindsInstance
        fun prePostProcessor(prePostProcessor: PrePostProcessor): Builder

        @BindsInstance
        fun contentResolver(contentResolver: ContentResolverProvider): Builder

        fun build(): MainScreenComponent
    }
}

@Module
abstract class MainScreenModule {

    @Binds
    @IntoMap
    @ViewModelKey(BaseViewModel::class)
    abstract fun baseViewModel(viewModel: BaseViewModel): ViewModel

}