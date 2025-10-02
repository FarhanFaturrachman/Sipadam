package com.example.sipadam.pemadaman.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "korban")
data class Korban(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // ID korban
    val laporanId: Long, // ID laporan kebakaran (foreign key)

    val jenisKorban: String,
    val namaKorban: String,

    // 🔹 Tambahan field baru
    val jkKorban: String,             // Jenis kelamin
    val usiaKorban: String,           // Usia
    val kondisiFisikKorban: String,   // Kondisi fisik
    val nikKorban: String,            // NIK
    val kkKorban: String,             // No KK
    val ttlKorban: String,            // Tempat, Tanggal Lahir
    val alamatKorban: String          // Alamat
)