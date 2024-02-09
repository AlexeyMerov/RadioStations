package com.alexeymerov.radiostations.core.database.di

import android.content.Context
import androidx.room.Room
import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.test.AndroidTest
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
class AndroidTestDatabaseModule {

    @Singleton
    @Provides
    fun provideInMemoryDb(@AndroidTest context: Context): RadioDatabase = Room
        .inMemoryDatabaseBuilder(context, RadioDatabase::class.java)
        .allowMainThreadQueries()
        .build()

}