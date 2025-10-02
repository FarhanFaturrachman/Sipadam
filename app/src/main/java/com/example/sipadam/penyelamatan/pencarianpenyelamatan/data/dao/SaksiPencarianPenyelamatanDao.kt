package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.dao

import androidx.room.*
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.SaksiLaporanPencarianPenyelamatan

@Dao
interface SaksiPencarianPenyelamatanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(saksi: SaksiLaporanPencarianPenyelamatan): Long

    @Query("SELECT * FROM saksi_pencarian_penyelamatan WHERE laporanId = :laporanId")
    suspend fun getSaksiByLaporan(laporanId: Long): List<SaksiLaporanPencarianPenyelamatan>

    @Delete
    suspend fun delete(saksi: SaksiLaporanPencarianPenyelamatan)

    @Query("DELETE FROM saksi_pencarian_penyelamatan WHERE laporanId = :laporanId")
    suspend fun deleteByLaporanId(laporanId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(saksiList: List<SaksiLaporanPencarianPenyelamatan>)

}
