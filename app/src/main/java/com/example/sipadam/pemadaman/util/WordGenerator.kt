package com.example.sipadam.pemadaman.util

import android.content.Context
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import java.io.File
import java.io.FileOutputStream

object WordGenerator {

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
                    // Ganti teks di paragraf cell
                    replaceInParagraphs(cell.paragraphs, replacements)

                    // Jika ada tabel di dalam cell, proses lagi secara rekursif
                    if (cell.tables.isNotEmpty()) {
                        replaceInTables(cell.tables, replacements)
                    }
                }
            }
        }
    }

    fun generate(context: Context, laporan: LaporanKebakaran): File {
        // Buka template dari assets
        val templateStream = context.assets.open("BA DAMKAR.docx")
        val doc = XWPFDocument(templateStream)

        // Data pengganti placeholder
        val replacements = mapOf(
            "{hari}" to laporan.hari,
            "{tanggal}" to laporan.tanggal,
            "{lokasi}" to laporan.alamatDetail,
            "{desa}" to laporan.desa,
            "{kecamatan}" to laporan.kecamatan,
            "{kabupaten}" to laporan.kabupaten,
            "{ResponsTime}" to laporan.responTime,
            "{waktuLaporan}" to laporan.waktuLaporan,
            "{waktuSampai}" to laporan.waktuSampai,
            "{waktukembali}" to laporan.waktuKembali,
            "{objek}" to laporan.objek,
            "{namaPemilik}" to laporan.namaPemilik,
            "{hpPelapor}" to laporan.hpPelapor,
            "{hpPemilik}" to laporan.hpPemilik,
            "{namaPelapor}" to laporan.namaPelapor,
            "{luas}" to laporan.luas,
            "{tafsiranKebakaran}" to laporan.tafsiranTerbakar,
            "{dokumenTerbakar}" to laporan.dokumenTerbakar,
            "{kronologi}" to laporan.deskripsiKronologi,
            "{kendaraan}" to laporan.kendaraan,
            "{peralatan}" to laporan.peralatan,
            "{regu}" to laporan.regu,
            "{regu_lapor}" to laporan.reguLapor,
            "{instansi_pembantu}" to laporan.instansi,
            "{waktuUlang}" to laporan.waktuUlang,
            "{koordinat}" to laporan.koordinat,
            "{koordinatgeografi}" to laporan.koordinatGeografi
        )

        // Ganti di paragraf biasa
        replaceInParagraphs(doc.paragraphs, replacements)

        // Ganti di semua tabel (termasuk tabel di dalam tabel)
        replaceInTables(doc.tables, replacements)

        // Simpan hasil ke file baru
        val safeObjek = laporan.objek.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeHari = laporan.hari.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val safeTanggal = laporan.tanggal.replace("[^0-9A-Za-z_-]".toRegex(), "_")
        val fileName = "LAPORAN_KEBAKARAN_${safeObjek}_${safeHari}_${safeTanggal}.docx"

        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { doc.write(it) }
        doc.close()
        return file
    }
}