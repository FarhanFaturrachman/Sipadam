package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao.PencarianPenyelamatanDao
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao.KorbanPencarianPenyelamatanDao
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao.SaksiPencarianPenyelamatanDao
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.LaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.KorbanLaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.SaksiLaporanPencarianPenyelamatan

@Database(
    entities = [LaporanPencarianPenyelamatan::class, KorbanLaporanPencarianPenyelamatan::class, SaksiLaporanPencarianPenyelamatan::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabasePencarianPenyelamatan : RoomDatabase() {

    abstract fun laporanDao(): PencarianPenyelamatanDao
    abstract fun korbanDao(): KorbanPencarianPenyelamatanDao
    abstract fun saksiDao(): SaksiPencarianPenyelamatanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabasePencarianPenyelamatan? = null

        fun getDatabase(context: Context): AppDatabasePencarianPenyelamatan {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabasePencarianPenyelamatan::class.java,
                    "pencarian_penyelamatan_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
