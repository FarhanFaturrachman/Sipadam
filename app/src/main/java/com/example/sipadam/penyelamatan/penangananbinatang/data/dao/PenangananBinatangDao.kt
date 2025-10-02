package com.example.sipadam.penyelamatan.penangananbinatang.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang

@Dao
interface PenangananBinatangDao {

    @Query("SELECT * FROM laporan_penanganan_binatang ORDER BY id DESC")
    fun getAll(): LiveData<List<LaporanPenangananBinatang>>

    @Query("SELECT * FROM laporan_penanganan_binatang WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LaporanPenangananBinatang?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: LaporanPenangananBinatang): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laporans: List<LaporanPenangananBinatang>): List<Long>

    @Update
    suspend fun update(laporan: LaporanPenangananBinatang)

    @Delete
    suspend fun delete(laporan: LaporanPenangananBinatang)

    @Query("DELETE FROM laporan_penanganan_binatang")
    suspend fun deleteAll()
}
