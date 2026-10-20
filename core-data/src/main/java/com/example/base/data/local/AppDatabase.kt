package com.example.base.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Bump [version] and add a `Migration` (or an `AutoMigration`) for every schema change.
 * Exported schemas are written to `core-data/schemas/` — commit them.
 */
@Database(entities = [TodoEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        const val NAME = "app.db"
    }
}
