package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.LaporanPencarianPenyelamatan

@Dao
interface PencarianPenyelamatanDao {

    @Query("SELECT * FROM laporan_pencarian_penyelamatan ORDER BY id DESC")
    fun getAll(): LiveData<List<LaporanPencarianPenyelamatan>>

    @Query("SELECT * FROM laporan_pencarian_penyelamatan WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LaporanPencarianPenyelamatan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: LaporanPencarianPenyelamatan): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laporans: List<LaporanPencarianPenyelamatan>): List<Long>

    @Update
    suspend fun update(laporan: LaporanPencarianPenyelamatan)

    @Delete
    suspend fun delete(laporan: LaporanPencarianPenyelamatan)

    @Query("DELETE FROM laporan_pencarian_penyelamatan")
    suspend fun deleteAll()
}
