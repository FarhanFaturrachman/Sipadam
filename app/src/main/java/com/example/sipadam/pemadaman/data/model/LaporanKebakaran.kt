package com.example.sipadam.pemadaman.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan")
data class LaporanKebakaran(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,  // ✅ ID laporan

    val tanggal: String,
    val hari: String,
    val jenisKejadian: String,
    val reguLapor: String,

    // I. Waktu & Jarak TKK
    val waktuLaporan: String,
    val waktuSampai: String,
    val waktuUlang: String,
    val jarak: String,
    val responTime: String,
    val waktuKembali: String,

    // II. Identifikasi Objek Kebakaran
    val objek: String,
    // Lokasi
    val alamatDetail: String,
    val kabupaten: String,
    val kecamatan: String,
    val desa: String,
    val luas: String,
    val koordinat: String,
    val koordinatGeografi: String,

    // III. Pelapor & Pemilik
    val namaPelapor: String,
    val alamatPelapor: String,
    val hpPelapor: String,
    val namaPemilik: String,
    val alamatPemilik: String,
    val hpPemilik: String,

    // IV. Aset & Tafsiran
    val asetSelamat: String,
    val tafsiranSelamat: String,
    val asetTerbakar: String,
    val tafsiranTerbakar: String,
    val dokumenTerbakar: String,

    // V. Kronologi
    val deskripsiKronologi: String,

    // VI. Penanggulangan
    val deskripsiPenanggulangan: String,

    // VII. Ringkasan Korban
    val korbanSelamat: String,

    // VIII. Kendaraan dan Peralatan
    val peralatan: String,
    val kendaraan: String,

    // IX. Petugas & Instansi
    val regu: String,
    val petugas: String,
    val instansi: String,

    // X. Klasifikasi
    val klasifikasi: String
)