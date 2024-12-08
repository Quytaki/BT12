package com.example.bt12.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bt12.model.Category
import com.example.bt12.model.Transaction

@Database(entities = [Transaction::class, Category::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_database"
                )
                    .fallbackToDestructiveMigration() // Thêm destructive migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
