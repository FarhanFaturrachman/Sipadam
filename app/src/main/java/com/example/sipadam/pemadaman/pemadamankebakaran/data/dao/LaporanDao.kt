package com.example.sipadam.pemadaman.pemadamankebakaran.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sipadam.pemadaman.pemadamankebakaran.data.model.LaporanKebakaran

@Dao
interface LaporanDao {

    // Ambil semua laporan, urutkan dari yang terbaru
    @Query("SELECT * FROM laporan ORDER BY id DESC")
    fun getAll(): LiveData<List<LaporanKebakaran>>

    @Query("SELECT * FROM laporan WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LaporanKebakaran?

    // Insert dengan mengembalikan ID
    @Insert
    suspend fun insert(laporan: LaporanKebakaran): Long

    // Update laporan
    @Update
    suspend fun update(laporan: LaporanKebakaran)

    // Hapus laporan
    @Delete
    suspend fun delete(laporan: LaporanKebakaran)

    // ✅ Update field tertentu
    @Query("UPDATE laporan SET waktuUlang = :waktuUlang WHERE id = :id")
    suspend fun updateWaktuUlang(id: Long, waktuUlang: String)

    // ✅ Tambahan: filter laporan berdasarkan tanggal (format yyyy-MM-dd)
    @Query("SELECT * FROM laporan WHERE substr(tanggal,1,10) BETWEEN :startDate AND :endDate ORDER BY tanggal ASC")
    suspend fun getBetweenDates(startDate: String, endDate: String): List<LaporanKebakaran>

    // ✅ Tambahan: ambil semua sebagai List (bukan LiveData), untuk fallback & debug
    @Query("SELECT * FROM laporan ORDER BY tanggal ASC")
    suspend fun getAllList(): List<LaporanKebakaran>
}
