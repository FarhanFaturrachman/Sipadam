package com.example.sipadam.penyelamatan.gawatdarurat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipadam.penyelamatan.gawatdarurat.data.dao.GawatDaruratDao
import com.example.sipadam.penyelamatan.gawatdarurat.data.dao.KorbanGawatDaruratDao
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.KorbanLaporanGawatDarurat

@Database(
    entities = [LaporanGawatDarurat::class, KorbanLaporanGawatDarurat::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabaseGawatDarurat : RoomDatabase() {

    abstract fun laporanDao(): GawatDaruratDao
    abstract fun korbanDao(): KorbanGawatDaruratDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseGawatDarurat? = null

        fun getDatabase(context: Context): AppDatabaseGawatDarurat {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseGawatDarurat::class.java,
                    "gawat_darurat_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
