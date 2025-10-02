package com.example.sipadam.penyelamatan.penangananbinatang.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.KorbanLaporanPenangananBinatang

@Dao
interface KorbanPenangananBinatangDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(korban: KorbanLaporanPenangananBinatang): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(korbans: List<KorbanLaporanPenangananBinatang>): List<Long>

    @Query("SELECT * FROM korban_penanganan_binatang WHERE laporanId = :laporanId")
    fun getKorbanByLaporan(laporanId: Long): LiveData<List<KorbanLaporanPenangananBinatang>>

    @Query("SELECT * FROM korban_penanganan_binatang WHERE laporanId = :laporanId")
    suspend fun getKorbanByLaporanNow(laporanId: Long): List<KorbanLaporanPenangananBinatang>

    @Query("SELECT * FROM korban_penanganan_binatang WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): KorbanLaporanPenangananBinatang?

    @Update
    suspend fun update(korban: KorbanLaporanPenangananBinatang)

    @Delete
    suspend fun delete(korban: KorbanLaporanPenangananBinatang)

    @Query("DELETE FROM korban_penanganan_binatang WHERE laporanId = :laporanId")
    suspend fun deleteByLaporanId(laporanId: Long)
}
