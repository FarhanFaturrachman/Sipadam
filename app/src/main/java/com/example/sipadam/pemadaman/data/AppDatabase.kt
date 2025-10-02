package com.example.sipadam.pemadaman.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipadam.pemadaman.data.dao.KorbanDao
import com.example.sipadam.pemadaman.data.dao.LaporanDao
import com.example.sipadam.pemadaman.data.model.Korban
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran

@Database(
    entities = [LaporanKebakaran::class, Korban::class],
    version = 4, // Naikkan versinya
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun laporanDao(): LaporanDao
    abstract fun korbanDao(): KorbanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "laporan_db"
                )
                    .fallbackToDestructiveMigration() // Hapus data lama jika schema berubah
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}