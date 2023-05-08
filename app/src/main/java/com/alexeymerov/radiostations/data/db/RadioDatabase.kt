package com.alexeymerov.radiostations.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.dao.StationDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity

@Database(entities = [CategoryEntity::class, StationEntity::class], version = 1, exportSchema = false)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun stationDao(): StationDao

    companion object {
        private const val DB_NAME = "radio_stations"

        fun buildDatabase(context: Context): RadioDatabase {
            return Room.databaseBuilder(context, RadioDatabase::class.java, DB_NAME)
//                .addMigrations() // in case some DB changes, don't forget to implement a migration logic
//                .fallbackToDestructiveMigration()
                .build()
        }
    }

}