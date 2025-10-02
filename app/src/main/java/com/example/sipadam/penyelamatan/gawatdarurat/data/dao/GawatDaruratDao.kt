package com.example.sipadam.penyelamatan.gawatdarurat.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat

@Dao
interface GawatDaruratDao {

    @Query("SELECT * FROM laporan_gawat_darurat ORDER BY id DESC")
    fun getAll(): LiveData<List<LaporanGawatDarurat>>

    @Query("SELECT * FROM laporan_gawat_darurat WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LaporanGawatDarurat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: LaporanGawatDarurat): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laporans: List<LaporanGawatDarurat>): List<Long>

    @Update
    suspend fun update(laporan: LaporanGawatDarurat)

    @Delete
    suspend fun delete(laporan: LaporanGawatDarurat)

    @Query("DELETE FROM laporan_gawat_darurat")
    suspend fun deleteAll()
}
