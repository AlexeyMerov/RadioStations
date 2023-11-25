package com.alexeymerov.radiostations.di

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.alexeymerov.radiostations.database.RadioDatabase
import com.alexeymerov.radiostations.database.dao.CategoryDao
import com.alexeymerov.radiostations.database.dao.MediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
@Named("AndroidTest")
class AndroidTestDatabaseModule {

    @Singleton
    @Provides
    @Named("AndroidTest")
    fun provideTestContext(): Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Singleton
    @Provides
    @Named("AndroidTest")
    fun provideInMemoryDb(@Named("AndroidTest") context: Context): RadioDatabase = Room
        .inMemoryDatabaseBuilder(context, RadioDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    @Singleton
    @Provides
    fun provideCategoryDao(@Named("AndroidTest") db: RadioDatabase): CategoryDao = db.categoryDao()

    @Singleton
    @Provides
    fun provideMediaDao(@Named("AndroidTest") db: RadioDatabase): MediaDao = db.mediaDao()

}