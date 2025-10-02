package com.example.sipadam.laporanlainnya.laporankegiatan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipadam.laporanlainnya.laporankegiatan.data.dao.KegiatanDao
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan

@Database(
    entities = [LaporanKegiatan::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabaseKegiatan : RoomDatabase() {

    abstract fun KegiatanDao(): KegiatanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseKegiatan? = null

        fun getDatabase(context: Context): AppDatabaseKegiatan {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseKegiatan::class.java,
                    "kegiatan_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
