package com.example.sipadam.penyelamatan.penangananbinatang.util

import android.content.Context
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import java.io.File
import java.io.FileOutputStream

object WordGeneratorBinatang {

    private fun replaceInParagraphs(paragraphs: List<XWPFParagraph>, replacements: Map<String, String>) {
        paragraphs.forEach { p ->
            p.runs.forEach { r ->
                var text = r.text()
                if (text != null) {
                    replacements.forEach { (key, value) ->
                        text = text.replace(key, value)
                    }
                    r.setText(text, 0)
                }
            }
        }
    }

    private fun replaceInTables(tables: List<XWPFTable>, replacements: Map<String, String>) {
        tables.forEach { table ->
            table.rows.forEach { row ->
                row.tableCells.forEach { cell ->
                    replaceInParagraphs(cell.paragraphs, replacements)
                    if (cell.tables.isNotEmpty()) {
                        replaceInTables(cell.tables, replacements)
                    }
                }
            }
        }
    }

    fun generate(context: Context, laporan: LaporanPenangananBinatang): File {
        // Buka template Word dari assets
        val templateStream = context.assets.open("BA BINATANG.docx")
        val doc = XWPFDocument(templateStream)

        // Placeholder → data laporan
        val replacements = mapOf(
            "{objek2}" to laporan.jenisKegiatan,
            "{hari1}" to laporan.hariDiterima,
            "{tanggal1}" to laporan.tanggalDiterima,
            "{hari2}" to laporan.hariTindak,
            "{tanggal2}" to laporan.tanggalTindak,
            "{lokasi2}" to laporan.alamat,
            "{desa2}" to laporan.desa,
            "{kecamatan2}" to laporan.kecamatan,
            "{kabupaten2}" to laporan.kabupaten,
            "{waktu1}" to laporan.waktuDiterima,
            "{waktu2}" to laporan.waktuTindak,
            "{koordinat1}" to laporan.koordinatDesimal,
            "{koordinat2}" to laporan.koordinatGeografi,
            "{kendaraan2}" to laporan.kendaraan,
            "{sarana2}" to laporan.apd,
            "{teknik2}" to laporan.upaya,
            "{petugas2}" to laporan.petugas,
            "{rencana}" to laporan.rencana,
        )

        // Ganti di paragraf & tabel
        replaceInParagraphs(doc.paragraphs, replacements)
        replaceInTables(doc.tables, replacements)

        // Buat nama file aman
        val safeKegiatan = laporan.jenisKegiatan.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeHari = laporan.hariDiterima.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeTanggal = laporan.tanggalDiterima.replace("[^0-9A-Za-z_-]".toRegex(), "_")
        val fileName = "LAPORAN_BINATANG_${safeKegiatan}_${safeHari}_${safeTanggal}.docx"

        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { doc.write(it) }
        doc.close()
        return file
    }
}
