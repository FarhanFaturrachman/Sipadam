package com.example.sipadam.penyelamatan.gawatdarurat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan_gawat_darurat")
data class LaporanGawatDarurat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // ID laporan

    // === Jenis Kegiatan ===
    val jenisKegiatan: String,
    val regu: String,

    // === Waktu Laporan Diterima ===
    val hariDiterima: String,
    val tanggalDiterima: String,
    val waktuDiterima: String,

    // === Lokasi Kegiatan ===
    val alamat: String,
    val kabupaten: String,
    val kecamatan: String,
    val desa: String,

    // === Jarak & Titik Koordinat ===
    val jarak: String,
    val koordinatDesimal: String,
    val koordinatGeografi: String,

    // === Korban ===
    val korbanSelamat: String,

    // === Kendaraan, APD, Upaya ===
    val rencana: String,
    val kronologi: String,
    val kendaraan: String,
    val apd: String,
    val upaya: String,

    // === Petugas Pelaksana ===
    val petugas: String
)
