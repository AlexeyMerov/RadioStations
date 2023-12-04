package com.alexeymerov.radiostations.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.alexeymerov.radiostations.core.database.RadioDatabase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@HiltAndroidTest
class DatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RadioDatabase::class.java,
        emptyList()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        val databaseFirst = helper.createDatabase(TEST_DB, 1)
        databaseFirst.close()

        val databaseLast = Room
            .databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                RadioDatabase::class.java,
                TEST_DB
            )
            .addMigrations(*ALL_MIGRATIONS)
            .build()

        databaseLast.openHelper.writableDatabase.close()
    }

    companion object {
        private const val TEST_DB = "migration_test"
        private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
    }
}