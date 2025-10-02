package com.example.sipadam.penyelamatan.penangananbinatang.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipadam.penyelamatan.penangananbinatang.data.dao.PenangananBinatangDao
import com.example.sipadam.penyelamatan.penangananbinatang.data.dao.KorbanPenangananBinatangDao
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.KorbanLaporanPenangananBinatang

@Database(
    entities = [LaporanPenangananBinatang::class, KorbanLaporanPenangananBinatang::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabasePenangananBinatang : RoomDatabase() {

    abstract fun laporanDao(): PenangananBinatangDao
    abstract fun korbanDao(): KorbanPenangananBinatangDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabasePenangananBinatang? = null

        fun getDatabase(context: Context): AppDatabasePenangananBinatang {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabasePenangananBinatang::class.java,
                    "penanganan_binatang_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
