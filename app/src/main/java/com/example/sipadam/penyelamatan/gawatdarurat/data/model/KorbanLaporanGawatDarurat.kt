package com.example.sipadam.penyelamatan.gawatdarurat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "korban_gawat_darurat")
data class KorbanLaporanGawatDarurat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,  // ID korban
    val laporanId: Long,                                 // FK ke laporan gawat darurat

    val jenisKorban: String,
    val namaKorban: String,
    val jkKorban: String,
    val usiaKorban: String,
    val kondisiFisikKorban: String,
    val nikKorban: String,
    val kkKorban: String,
    val ttlKorban: String,
    val alamatKorban: String
)
