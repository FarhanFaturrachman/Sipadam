package com.example.sipadam.penyelamatan.gawatdarurat.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.KorbanLaporanGawatDarurat

@Dao
interface KorbanGawatDaruratDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(korban: KorbanLaporanGawatDarurat): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(korbans: List<KorbanLaporanGawatDarurat>): List<Long>

    @Query("SELECT * FROM korban_gawat_darurat WHERE laporanId = :laporanId")
    fun getKorbanByLaporan(laporanId: Long): LiveData<List<KorbanLaporanGawatDarurat>>

    @Query("SELECT * FROM korban_gawat_darurat WHERE laporanId = :laporanId")
    suspend fun getKorbanByLaporanNow(laporanId: Long): List<KorbanLaporanGawatDarurat>

    @Query("SELECT * FROM korban_gawat_darurat WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): KorbanLaporanGawatDarurat?

    @Update
    suspend fun update(korban: KorbanLaporanGawatDarurat)

    @Delete
    suspend fun delete(korban: KorbanLaporanGawatDarurat)

    @Query("DELETE FROM korban_gawat_darurat WHERE laporanId = :laporanId")
    suspend fun deleteByLaporanId(laporanId: Long)
}
