package com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan_pencarian_penyelamatan")
data class LaporanPencarianPenyelamatan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // ID laporan

    // === Jenis Kegiatan ===
    val jenisKegiatan: String,
    val regu: String,

    // === Waktu Laporan Diterima ===
    val hariDiterima: String,
    val tanggalDiterima: String,
    val waktuDiterima: String,

    // === Waktu Laporan Ditindaklanjuti ===
    val hariTindak: String,
    val tanggalTindak: String,
    val waktuTindak: String,

    // === Lokasi Kegiatan ===
    val alamat: String,
    val kabupaten: String,
    val kecamatan: String,
    val desa: String,

    // === Jarak & Titik Koordinat ===
    val jarak: String,
    val koordinatDesimal: String,
    val koordinatGeografi: String,

    // === Identitas Pelapor ===
    val namaPelapor: String,
    val usiaPelapor: String,
    val noHpPelapor: String,
    val alamatPelapor: String,

    // === Korban ===
    val korbanSelamat: String,

    // === Kendaraan, APD, Upaya ===
    val kendaraan: String,
    val apd: String,
    val rencana: String,
    val kronologi: String,
    val upaya: String,

    // === Petugas Pelaksana ===
    val petugas: String,
    val instansi: String
)
