package com.example.cowdetection.di

import android.content.Context
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.filepath.FilePathProviderImpl
import com.example.cowdetection.utils.imageanalyzer.ImageAnalyzer
import com.example.cowdetection.utils.imageanalyzer.ImageAnalyzerImpl
import com.example.cowdetection.utils.prepostprocessor.PrePostProcessor
import com.example.cowdetection.utils.prepostprocessor.PrePostProcessorImpl
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun filePath(): FilePathProvider

    fun imageAnalyzer(): ImageAnalyzer

    fun prePostProcessor(): PrePostProcessor

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(context: Context): Builder

        fun build(): AppComponent
    }
}

@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindFilePathProvider(provider: FilePathProviderImpl): FilePathProvider

    @Binds
    @Singleton
    abstract fun bindImageAnalyzer(analyzer: ImageAnalyzerImpl): ImageAnalyzer

    @Binds
    @Singleton
    abstract fun bindPrePostProcessor(processor: PrePostProcessorImpl): PrePostProcessor
}