package com.example.sipadam.penyelamatan.penangananbinatang.util

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sipadam.penyelamatan.penangananbinatang.data.AppDatabasePenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ShareUtilsBinatang {

    fun shareAsText(context: Context, laporan: LaporanPenangananBinatang, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val db = AppDatabasePenangananBinatang.getDatabase(context)

            val korbanList = withContext(Dispatchers.IO) {
                db.korbanDao().getKorbanByLaporanNow(laporan.id)
            }

            // Hitung jumlah korban berdasarkan jenis
            val korbanJiwa = korbanList.count { it.jenisKorban.equals("Meninggal Dunia", true) }
            val korbanLukaBerat = korbanList.count { it.jenisKorban.equals("Luka Berat", true) }
            val korbanLukaRingan = korbanList.count { it.jenisKorban.equals("Luka Ringan", true) }

            // Buat daftar detail korban
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

            val text = """
*LAPORAN PENYELAMATAN DAN PENANGANAN BINATANG*

*JENIS KEGIATAN :*
${laporan.jenisKegiatan}

*WAKTU*
Laporan diterima        : ${laporan.hariDiterima}, ${laporan.tanggalDiterima} Pukul ${laporan.waktuDiterima} WIB
Laporan ditindaklanjuti : ${laporan.hariTindak}, ${laporan.tanggalTindak} Pukul ${laporan.waktuTindak} WIB

*LOKASI KEGIATAN*
${laporan.alamat}, ${laporan.desa}, Kec. ${laporan.kecamatan}, Kab. ${laporan.kabupaten}

*TITIK KOORDINAT*
${laporan.koordinatDesimal} | ${laporan.koordinatGeografi}

*JARAK TEMPUH*
${laporan.jarak} KM

*IDENTITAS PELAPOR*
Nama     : ${laporan.namaPelapor}
Usia     : ${laporan.usiaPelapor}
No HP    : ${laporan.noHpPelapor}
Alamat   : ${laporan.alamatPelapor}

*IDENTITAS PEMILIK LOKASI KEGIATAN*
Nama     : ${laporan.namaPemilik}
Usia     : ${laporan.usiaPemilik}
No HP    : ${laporan.noHpPemilik}
Alamat   : ${laporan.alamatPemilik}

*DAMPAK*
Korban Terselamatkan : ${laporan.korbanSelamat}

Korban Jiwa          : ${if (korbanJiwa == 0) "Nihil" else "$korbanJiwa orang"}
Korban Luka Berat    : ${if (korbanLukaBerat == 0) "Nihil" else "$korbanLukaBerat orang"}
Korban Luka Ringan   : ${if (korbanLukaRingan == 0) "Nihil" else "$korbanLukaRingan orang"}

*DAFTAR KORBAN*
$daftarKorbanText

*RENCANA OPERASI*
${laporan.rencana}

*PETUGAS PELAKSANA:*
${laporan.petugas.split(",").joinToString("\n") { "- ${it.trim()}" }}

*KENDARAAN YANG DIGUNAKAN:*
${if (laporan.kendaraan.isBlank()) "-" else laporan.kendaraan.split(",").joinToString("\n") { "- ${it.trim()}" }}

*APD YANG DIGUNAKAN:*
${laporan.apd}

*UPAYA YANG DILAKUKAN:*
${laporan.upaya}

*CATATAN :*
Dokumentasi terlampir

Hormat kami  
*${laporan.regu}*
""".trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan laporan"))
        }
    }
}
