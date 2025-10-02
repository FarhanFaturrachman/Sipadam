package com.example.sipadam.penyelamatan.gawatdarurat.util

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sipadam.penyelamatan.gawatdarurat.data.AppDatabaseGawatDarurat
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ShareUtilsGawatDarurat {

    fun shareAsText(context: Context, laporan: LaporanGawatDarurat, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val db = AppDatabaseGawatDarurat.getDatabase(context)

            // Ambil daftar korban dari database
            val korbanList = withContext(Dispatchers.IO) {
                db.korbanDao().getKorbanByLaporanNow(laporan.id)
            }

            // Hitung jumlah korban
            val korbanMeninggal = korbanList.count { it.jenisKorban.equals("Meninggal Dunia", true) }
            val korbanLukaBerat = korbanList.count { it.jenisKorban.equals("Luka Berat", true) }
            val korbanLukaRingan = korbanList.count { it.jenisKorban.equals("Luka Ringan", true) }

            // Daftar detail korban
            val daftarKorbanText = if (korbanList.isNotEmpty()) {
                korbanList.mapIndexed { index, korban ->
                    """
${index + 1}. *Jenis Korban*   : ${korban.jenisKorban.ifEmpty { "-" }}
    *Nama*           : ${korban.namaKorban.ifEmpty { "-" }}
    *NIK*            : ${korban.nikKorban.ifEmpty { "-" }}
    *No KK*          : ${korban.kkKorban.ifEmpty { "-" }}
    *Jenis Kelamin*  : ${korban.jkKorban.ifEmpty { "-" }}
    *Usia*           : ${korban.usiaKorban.ifEmpty { "-" }}
    *Kondisi Fisik*  : ${korban.kondisiFisikKorban.ifEmpty { "-" }}
    *TTL*            : ${korban.ttlKorban.ifEmpty { "-" }}
    *Alamat*         : ${korban.alamatKorban.ifEmpty { "-"}}

""".trimIndent()
                }.joinToString("\n")
            } else {
                "-\n"
            }

            // Susun teks laporan
            val text = """
*LAPORAN PENANGULANGAN GAWAT DARURAT TERPADU (SPGDT)*

*JENIS KEGIATAN :*
${laporan.jenisKegiatan}

*WAKTU :*
${laporan.hariDiterima}, ${laporan.tanggalDiterima} Pukul ${laporan.waktuDiterima} WIB

*LOKASI*
${laporan.alamat}, ${laporan.desa}, Kec. ${laporan.kecamatan}, Kab. ${laporan.kabupaten}

*JARAK TEMPUH*
${laporan.jarak} KM

*TITIK KOORDINAT*
${laporan.koordinatDesimal} | ${laporan.koordinatGeografi}

*KORBAN*
Korban Meninggal   : ${if (korbanMeninggal == 0) "Nihil" else "$korbanMeninggal orang"}
Korban Luka Berat  : ${if (korbanLukaBerat == 0) "Nihil" else "$korbanLukaBerat orang"}
Korban Luka Ringan : ${if (korbanLukaRingan == 0) "Nihil" else "$korbanLukaRingan orang"}

*DAFTAR KORBAN*
$daftarKorbanText

*RENCANA OPERASI*
${laporan.rencana}

*KRONOLOGI*
${laporan.kronologi}

*KENDARAAN*
${if (laporan.kendaraan.isBlank()) "-" else laporan.kendaraan.split(",").joinToString("\n") { "- ${it.trim()}" }}

*APD YANG DIGUNAKAN*
${laporan.apd}

*UPAYA YANG DILAKUKAN*
${laporan.upaya}

*PETUGAS PELAKSANA*
${laporan.petugas.split(",").joinToString("\n") { "- ${it.trim()}" }}

*CATATAN :*
Dokumentasi terlampir

Hormat kami  
*${laporan.regu}*
""".trimIndent()

            // Intent untuk share
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan laporan"))
        }
    }
}
