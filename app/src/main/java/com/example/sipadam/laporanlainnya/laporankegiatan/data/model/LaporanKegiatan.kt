package com.example.sipadam.laporanlainnya.laporankegiatan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan_kegiatan")
data class LaporanKegiatan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // ID laporan

    // === Data Kegiatan ===
    val regu: String,
    val kegiatan: String,

    // === Waktu ===
    val hari: String,
    val tanggal: String,
    val waktu: String,

    // === Lokasi ===
    val lokasi: String,

    // === Keterangan Tambahan ===
    val keterangan: String,
    val statusKegiatan: String
)
