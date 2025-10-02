package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "korban_pencarian_penyelamatan")
data class KorbanLaporanPencarianPenyelamatan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,  // ID korban
    val laporanId: Long,                                 // FK ke laporan utama

    val jenisKorban: String,
    val namaKorban: String,
    val jkKorban: String,
    val usiaKorban: String,
    val kondisiFisikKorban: String,
    val nikKorban: String,
    val kkKorban: String,
    val ttlKorban: String,
    val alamatKorban: String,
    val ciriciri: String
)
