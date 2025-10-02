package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.KorbanLaporanPencarianPenyelamatan

@Dao
interface KorbanPencarianPenyelamatanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(korban: KorbanLaporanPencarianPenyelamatan): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(korbans: List<KorbanLaporanPencarianPenyelamatan>): List<Long>

    @Query("SELECT * FROM korban_pencarian_penyelamatan WHERE laporanId = :laporanId")
    fun getKorbanByLaporan(laporanId: Long): LiveData<List<KorbanLaporanPencarianPenyelamatan>>

    @Query("SELECT * FROM korban_pencarian_penyelamatan WHERE laporanId = :laporanId")
    suspend fun getKorbanByLaporanNow(laporanId: Long): List<KorbanLaporanPencarianPenyelamatan>

    @Query("SELECT * FROM korban_pencarian_penyelamatan WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): KorbanLaporanPencarianPenyelamatan?

    @Update
    suspend fun update(korban: KorbanLaporanPencarianPenyelamatan)

    @Delete
    suspend fun delete(korban: KorbanLaporanPencarianPenyelamatan)

    @Query("DELETE FROM korban_pencarian_penyelamatan WHERE laporanId = :laporanId")
    suspend fun deleteByLaporanId(laporanId: Long)
}
