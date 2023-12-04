package com.alexeymerov.radiostations.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.database.dao.MediaDao
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.MediaEntity


@Database(
    version = 5,
    entities = [
        CategoryEntity::class,
        MediaEntity::class
    ]
)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun mediaDao(): MediaDao

    companion object {
        private const val DB_NAME = "radio_stations"

        fun buildDatabase(context: Context): RadioDatabase {
            return Room.databaseBuilder(context, RadioDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
        }
    }
}

/**
 * Caution: To keep your migration logic functioning as expected, use full queries instead of referencing constants that represent the queries.
 * */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE category ADD COLUMN childCount INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE category ADD COLUMN isFavorite INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val tempTableName = "temp_category"
        val tableName = "category"

        db.execSQL(
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

        db.execSQL(
            "INSERT INTO $tempTableName (id, position, url, parentUrl, text, image, currentTrack, type, childCount, isFavorite) " +
                "SELECT (parentUrl || '##' || text) as id, position, url, parentUrl, text, image, currentTrack, type, childCount, isFavorite " +
                "FROM $tableName"
        )

        db.execSQL("DROP TABLE $tableName")
        db.execSQL("ALTER TABLE $tempTableName RENAME TO $tableName")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS media (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "url TEXT NOT NULL, " +
                "directMediaUrl TEXT NOT NULL, " +
                "imageUrl TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "subtitle TEXT NOT NULL)"
        )
    }
}