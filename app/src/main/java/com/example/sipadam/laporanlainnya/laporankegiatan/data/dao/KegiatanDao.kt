package com.example.sipadam.laporanlainnya.laporankegiatan.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan

@Dao
interface KegiatanDao {

    @Query("SELECT * FROM laporan_kegiatan ORDER BY id DESC")
    fun getAll(): LiveData<List<LaporanKegiatan>>

    @Query("SELECT * FROM laporan_kegiatan WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LaporanKegiatan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: LaporanKegiatan): Long   // ✅ return ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laporans: List<LaporanKegiatan>): List<Long>

    @Update
    suspend fun update(laporan: LaporanKegiatan)

    @Delete
    suspend fun delete(laporan: LaporanKegiatan)

    @Query("DELETE FROM laporan_kegiatan")
    suspend fun deleteAll()
}
