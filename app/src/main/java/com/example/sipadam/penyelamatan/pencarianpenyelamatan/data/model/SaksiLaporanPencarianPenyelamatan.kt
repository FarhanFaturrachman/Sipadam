package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saksi_pencarian_penyelamatan")
data class SaksiLaporanPencarianPenyelamatan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val laporanId: Long,
    val namaSaksi: String,
    val jkSaksi: String?,
    val umurSaksi: String?,
    val noHpSaksi: String?,
    val alamatSaksi: String?
)
