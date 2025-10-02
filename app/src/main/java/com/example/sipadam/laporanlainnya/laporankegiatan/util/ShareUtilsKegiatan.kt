package com.example.sipadam.laporanlainnya.laporankegiatan.util

import android.content.Context
import android.content.Intent
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan

object ShareUtilsKegiatan {

    fun shareAsText(context: Context, laporan: LaporanKegiatan) {
        // Susun teks laporan sesuai format
        val text = """
*DARI : ${laporan.regu}*

Assalamualaikum Wr. Wb.

Izin melapokan kegiatan ${laporan.kegiatan} pada : 

Hari        : ${laporan.hari}
Tanggal     : ${laporan.tanggal}
Pukul       : ${laporan.waktu} WIB
Lokasi      : ${laporan.lokasi}
Kegiatan    : ${laporan.kegiatan}
Keterangan  : ${laporan.statusKegiatan} ${laporan.keterangan}

Demikian dilaporkan. 
(Dokumentasi Terlampir)
Wassalamualaikum Wr. Wb.

*Hormat kami*
${laporan.regu}
""".trimIndent()

        // Intent untuk share
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan laporan"))
    }
}
