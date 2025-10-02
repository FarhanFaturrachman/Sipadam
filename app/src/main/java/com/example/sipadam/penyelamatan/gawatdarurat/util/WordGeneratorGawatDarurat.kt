package com.example.sipadam.penyelamatan.gawatdarurat.util

import android.content.Context
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import java.io.File
import java.io.FileOutputStream

object WordGeneratorGawatDarurat {

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

    fun generate(context: Context, laporan: LaporanGawatDarurat): File {
        // Buka template Word dari assets (pastikan ada file BA GAWATDARURAT.docx di assets)
        val templateStream = context.assets.open("BA GAWATDARURAT.docx")
        val doc = XWPFDocument(templateStream)

        // Map placeholder → data laporan
        val replacements = mapOf(
            "{objek2}" to laporan.jenisKegiatan,
            "{hari2}" to laporan.hariDiterima,
            "{tanggal2}" to laporan.tanggalDiterima,
            "{lokasi2}" to laporan.alamat,
            "{desa2}" to laporan.desa,
            "{kecamatan2}" to laporan.kecamatan,
            "{kabupaten2}" to laporan.kabupaten,
            "{waktu2}" to laporan.waktuDiterima,
            "{koordinat1}" to laporan.koordinatDesimal,
            "{koordinat2}" to laporan.koordinatGeografi,
            "{kendaraan2}" to laporan.kendaraan,
            "{sarana2}" to laporan.apd,
            "{teknik2}" to laporan.upaya,
            "{petugas2}" to laporan.petugas,
            "{kronologi2}" to laporan.kronologi,
            "{rencana}" to laporan.rencana,
        )

        // Ganti teks di paragraf & tabel
        replaceInParagraphs(doc.paragraphs, replacements)
        replaceInTables(doc.tables, replacements)

        // Buat nama file yang aman
        val safeKegiatan = laporan.jenisKegiatan.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeHari = laporan.hariDiterima.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeTanggal = laporan.tanggalDiterima.replace("[^0-9A-Za-z_-]".toRegex(), "_")
        val fileName = "LAPORAN_GAWATDARURAT_${safeKegiatan}_${safeHari}_${safeTanggal}.docx"

        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { doc.write(it) }
        doc.close()
        return file
    }
}
