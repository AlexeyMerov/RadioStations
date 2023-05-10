package com.alexeymerov.radiostations.di

import com.alexeymerov.radiostations.R
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class UiModule {

    @Provides
    @FragmentScoped
    fun provideGlideRequestOptions() = RequestOptions()
        .transform(RoundedCorners(8))
        .placeholder(R.drawable.full_image)
        .error(R.drawable.full_image)
}