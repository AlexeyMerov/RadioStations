package com.alexeymerov.radiostations.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 2, exportSchema = false)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DB_NAME = "radio_stations"

        fun buildDatabase(context: Context): RadioDatabase {
            return Room.databaseBuilder(context, RadioDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }

}

/**
 * Caution: To keep your migration logic functioning as expected, use full queries instead of referencing constants that represent the queries.
 * */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE category ADD COLUMN childCount INTEGER")
    }
}