package com.alexeymerov.radiostations.core.filestore.di


import com.alexeymerov.radiostations.core.filestore.AppFileStore
import com.alexeymerov.radiostations.core.filestore.AppFileStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppFileStoreModule {

    @Binds
    @Singleton
    abstract fun bindFileStore(store: AppFileStoreImpl): AppFileStore

}