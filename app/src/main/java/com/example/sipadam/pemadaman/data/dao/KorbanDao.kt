package com.example.sipadam.pemadaman.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sipadam.pemadaman.data.model.Korban

@Dao
interface KorbanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(korban: Korban)

    @Query("SELECT * FROM korban WHERE laporanId = :laporanId")
    fun getKorbanByLaporanLive(laporanId: Long): LiveData<List<Korban>>

    @Query("SELECT * FROM korban WHERE laporanId = :laporanId")
    suspend fun getKorbanByLaporan(laporanId: Long): List<Korban>

    @Delete
    suspend fun delete(korban: Korban)

    @Query("SELECT * FROM korban WHERE laporanId = :laporanId")
    suspend fun getByLaporanId(laporanId: Long): List<Korban>

    // ✅ Tambahan untuk hapus semua korban dari laporan tertentu
    @Query("DELETE FROM korban WHERE laporanId = :laporanId")
    suspend fun deleteByLaporan(laporanId: Long)

    // ✅ Tambahan untuk export Excel (ambil semua korban)
    @Query("SELECT * FROM korban ORDER BY id DESC")
    suspend fun getAll(): List<Korban>
}
