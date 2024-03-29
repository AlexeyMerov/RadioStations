package com.alexeymerov.radiostations.core.data.di


import com.alexeymerov.radiostations.core.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.core.data.mapper.category.CategoryMapperImpl
import com.alexeymerov.radiostations.core.data.mapper.country.CountryMapper
import com.alexeymerov.radiostations.core.data.mapper.country.CountryMapperImpl
import com.alexeymerov.radiostations.core.data.mapper.geocoder.LocationGeocoder
import com.alexeymerov.radiostations.core.data.mapper.geocoder.LocationGeocoderImp
import com.alexeymerov.radiostations.core.data.mapper.media.MediaMapper
import com.alexeymerov.radiostations.core.data.mapper.media.MediaMapperImp
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepository
import com.alexeymerov.radiostations.core.data.repository.audio.MediaRepositoryImpl
import com.alexeymerov.radiostations.core.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.core.data.repository.category.CategoryRepositoryImpl
import com.alexeymerov.radiostations.core.data.repository.country.CountryRepository
import com.alexeymerov.radiostations.core.data.repository.country.CountryRepositoryImpl
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [RepositoryModule.Mapper::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(repository: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(repository: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindCountryRepository(repository: CountryRepositoryImpl): CountryRepository

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Mapper {

        @Binds
        @Singleton
        abstract fun bindResponseMapper(mapper: ResponseMapperImpl): ResponseMapper

        @Binds
        @Singleton
        abstract fun bindCategoryMapper(mapper: CategoryMapperImpl): CategoryMapper

        @Binds
        @Singleton
        abstract fun bindMediaMapper(mapper: MediaMapperImp): MediaMapper

        @Binds
        @Singleton
        abstract fun bindCountryMapper(mapper: CountryMapperImpl): CountryMapper

        @Binds
        @Singleton
        abstract fun bindLocationGeocoder(mapper: LocationGeocoderImp): LocationGeocoder
    }

}