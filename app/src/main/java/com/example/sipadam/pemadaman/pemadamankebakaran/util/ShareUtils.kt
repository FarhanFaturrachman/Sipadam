package com.example.sipadam.pemadaman.pemadamankebakaran.util

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.sipadam.pemadaman.pemadamankebakaran.data.AppDatabase
import com.example.sipadam.pemadaman.pemadamankebakaran.data.model.LaporanKebakaran
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ShareUtils {

    fun shareAsText(context: Context, laporan: LaporanKebakaran, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val db = AppDatabase.Companion.getDatabase(context)

            val korbanList = withContext(Dispatchers.IO) {
                db.korbanDao().getKorbanByLaporan(laporan.id)
            }

            val daftarKorbanText = if (korbanList.isNotEmpty()) {
                korbanList.mapIndexed { index, korban ->
                    """
                ${index + 1}. Jenis Korban     : ${korban.jenisKorban.ifEmpty { "-" }}
                    Nama             : ${korban.namaKorban.ifEmpty { "-" }}
                    NIK              : ${korban.nikKorban.ifEmpty { "-" }}
                    No KK            : ${korban.kkKorban.ifEmpty { "-" }}
                    Jenis Kelamin    : ${korban.jkKorban.ifEmpty { "-" }}
                    Usia             : ${korban.usiaKorban.ifEmpty { "-" }}
                    Kondisi Fisik    : ${korban.kondisiFisikKorban.ifEmpty { "-" }}
                    TTL              : ${korban.ttlKorban.ifEmpty { "-" }}
                    Alamat           : ${korban.alamatKorban.ifEmpty { "-" }}

                """.trimIndent()
                }.joinToString("\n")
            } else {
                "-"
            }

            val text = """
*LAPORAN PEMADAMAN KEBAKARAN (65)*
Dinas Pemadam Kebakaran dan Penyelamatan Kabupaten Purwakarta

Ijin melaporkan giat Pemadaman, Pada :

Hari, Tanggal      : ${laporan.hari}, ${laporan.tanggal}
Jenis Kejadian    : ${laporan.jenisKejadian}

*I. WAKTU & JARAK TKK*
1. Laporan diterima    : ${laporan.waktuLaporan ?: "-"} WIB
2. Sampai di TKK       : ${laporan.waktuSampai ?: "-"} WIB
3. Jarak TKK           : ± ${laporan.jarak ?: "-"} Km
4. Respon Time         : ± ${laporan.responTime ?: "-"} Menit

*II. IDENTIFIKASI OBJEK KEBAKARAN*
1. Objek        : ${laporan.objek}
2. Lokasi       : ${laporan.alamatDetail}, ${laporan.desa}, Kec. ${laporan.kecamatan}, Kab. ${laporan.kabupaten}
3. Luas area    : ${laporan.luas} m²
4. Koordinat    : ${laporan.koordinat} | ${laporan.koordinatGeografi}

*III. IDENTITAS PELAPOR & PEMILIK*
1. Pelapor
   - Nama   : ${laporan.namaPelapor}
   - Alamat : ${laporan.alamatPelapor ?: "-"}
   - No. Hp : ${laporan.hpPelapor}
    
2. Pemilik objek terbakar
   - Nama   : ${laporan.namaPemilik}
   - Alamat : ${laporan.alamatPemilik ?: "-"}
   - No. Hp : ${laporan.hpPemilik}

*IV. JUMLAH TAKSIRAN ASET*
1. Jumlah & jenis aset diselamatkan : 
${laporan.asetSelamat}
Taksiran aset diselamatkan       : ± Rp. ${laporan.tafsiranSelamat}

2. Jumlah & jenis aset terbakar     : 
${laporan.asetTerbakar}
Taksiran aset terbakar           : ± Rp. ${laporan.tafsiranTerbakar} 

*V. KRONOLOGI KEJADIAN*
${laporan.deskripsiKronologi}

*VI. PENANGGULANGAN*
${laporan.deskripsiPenanggulangan}

*VII. KORBAN TERDAMPAK*
Jumlah Korban Yang diselamatkan : ${laporan.petugas ?: "-"} orang
Daftar Korban :

$daftarKorbanText 

*VIII. KENDARAAN OPERASIONAL*
${laporan.kendaraan ?: "-"}

*IX. PETUGAS & INSTANSI MEMBANTU*
1. Petugas :
${laporan.regu ?: "-"}, ${laporan.petugas ?: "-"}

2. Instansi yang membantu :
${laporan.instansi ?: "-"}

*X. KLASIFIKASI KEBAKARAN*
    ${laporan.klasifikasi ?: "-"}

*Demikian kami laporkan*
(Dokumentasi Terlampir)
Hormat kami
*${laporan.reguLapor ?: "-"}*
""".trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan laporan"))
        }
    }
}