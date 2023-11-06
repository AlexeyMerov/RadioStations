package com.alexeymerov.radiostations.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity


@Database(entities = [CategoryEntity::class], version = 4)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DB_NAME = "radio_stations"

        fun buildDatabase(context: Context): RadioDatabase {
            return Room.databaseBuilder(context, RadioDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
        }
    }
}

/**
 * Caution: To keep your migration logic functioning as expected, use full queries instead of referencing constants that represent the queries.
 * */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE category ADD COLUMN childCount INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE category ADD COLUMN isFavorite INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tempTableName = "temp_category"
        val tableName = "category"

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS $tempTableName (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "position INTEGER NOT NULL, " +
                "url TEXT NOT NULL, " +
                "parentUrl TEXT NOT NULL, " +
                "text TEXT NOT NULL, " +
                "image TEXT NOT NULL, " +
                "currentTrack TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "childCount INTEGER, " +
                "isFavorite INTEGER NOT NULL)"
        )

        database.execSQL(
            "INSERT INTO $tempTableName (id, position, url, parentUrl, text, image, currentTrack, type, childCount, isFavorite) " +
                "SELECT (parentUrl || '##' || text) as id, position, url, parentUrl, text, image, currentTrack, type, childCount, isFavorite " +
                "FROM $tableName"
        )

        database.execSQL("DROP TABLE $tableName")
        database.execSQL("ALTER TABLE $tempTableName RENAME TO $tableName")
    }
}
