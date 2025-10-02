package com.example.sipadam.pemadaman.util

import android.content.Context
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran
import com.example.sipadam.pemadaman.data.model.Korban
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object ExcelGenerator {

    fun generate(
        context: Context,
        laporanList: List<LaporanKebakaran>,
        korbanMap: Map<Long, List<Korban>>,
        startDate: String,
        endDate: String
    ): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Rekap Pemadaman")

        var rowIndex = 0

        // 🔹 Header tabel utama + korban
        val headerRow = sheet.createRow(rowIndex++)
        val headers = listOf(
            "Tanggal", "Hari", "Jenis Kejadian", "Regu Yang Melaporkan",
            "Waktu Laporan", "Waktu Sampai", "Waktu Pendataan Ulang", "Jarak", "Respon Time", "Waktu Kembali Dari TKK",
            "Objek", "Alamat", "Kabupaten", "Kecamatan", "Desa", "Luas", "Koordinat", "Koordinat Geografi",
            "Nama Pelapor", "Alamat Pelapor", "HP Pelapor",
            "Nama Pemilik", "Alamat Pemilik", "HP Pemilik",
            "Aset Selamat", "Taksiran aset diselamat", "Aset Terbakar", "Taksiran aset terbakar", "Dokumen Terbakar",
            "Kronologi", "Penanggulangan",
            "Korban Selamat", "Peralatan", "Kendaraan",
            "Regu", "Petugas", "Instansi", "Klasifikasi",
            "Korban Meninggal Dunia", "Korban Luka Bakar", "Korban Luka Fisik Lainnya",
            // 🔹 Detail korban
            "Jenis Korban", "Nama", "Jenis Kelamin", "Usia", "Kondisi Fisik",
            "NIK", "KK", "Tempat Tanggal Lahir", "Alamat"
        )
        headers.forEachIndexed { idx, title -> headerRow.createCell(idx).setCellValue(title) }

        // 🔹 Isi laporan
        for (laporan in laporanList) {
            val korbanList = korbanMap[laporan.id] ?: emptyList()

            // Ringkasan korban
            val meninggal = korbanList.count { it.jenisKorban.equals("Meninggal Dunia", true) }
            val lukaBakar = korbanList.count { it.jenisKorban.equals("Luka Bakar", true) }
            val lukaLain = korbanList.count { it.jenisKorban.equals("Luka Fisik Lainnya", true) }

            // Baris utama laporan
            val row = sheet.createRow(rowIndex++)
            var col = 0
            row.createCell(col++).setCellValue(laporan.tanggal)
            row.createCell(col++).setCellValue(laporan.hari)
            row.createCell(col++).setCellValue(laporan.jenisKejadian)
            row.createCell(col++).setCellValue(laporan.reguLapor)
            row.createCell(col++).setCellValue(laporan.waktuLaporan)
            row.createCell(col++).setCellValue(laporan.waktuSampai)
            row.createCell(col++).setCellValue(laporan.waktuUlang)
            row.createCell(col++).setCellValue(laporan.jarak)
            row.createCell(col++).setCellValue(laporan.responTime)
            row.createCell(col++).setCellValue(laporan.waktuKembali)
            row.createCell(col++).setCellValue(laporan.objek)
            row.createCell(col++).setCellValue(laporan.alamatDetail)
            row.createCell(col++).setCellValue(laporan.kabupaten)
            row.createCell(col++).setCellValue(laporan.kecamatan)
            row.createCell(col++).setCellValue(laporan.desa)
            row.createCell(col++).setCellValue(laporan.luas)
            row.createCell(col++).setCellValue(laporan.koordinat)
            row.createCell(col++).setCellValue(laporan.koordinatGeografi)
            row.createCell(col++).setCellValue(laporan.namaPelapor)
            row.createCell(col++).setCellValue(laporan.alamatPelapor)
            row.createCell(col++).setCellValue(laporan.hpPelapor)
            row.createCell(col++).setCellValue(laporan.namaPemilik)
            row.createCell(col++).setCellValue(laporan.alamatPemilik)
            row.createCell(col++).setCellValue(laporan.hpPemilik)
            row.createCell(col++).setCellValue(laporan.asetSelamat)
            row.createCell(col++).setCellValue(laporan.tafsiranSelamat)
            row.createCell(col++).setCellValue(laporan.asetTerbakar)
            row.createCell(col++).setCellValue(laporan.tafsiranTerbakar)
            row.createCell(col++).setCellValue(laporan.dokumenTerbakar)
            row.createCell(col++).setCellValue(laporan.deskripsiKronologi)
            row.createCell(col++).setCellValue(laporan.deskripsiPenanggulangan)
            row.createCell(col++).setCellValue(laporan.korbanSelamat)
            row.createCell(col++).setCellValue(laporan.peralatan)
            row.createCell(col++).setCellValue(laporan.kendaraan)
            row.createCell(col++).setCellValue(laporan.regu)
            row.createCell(col++).setCellValue(laporan.petugas)
            row.createCell(col++).setCellValue(laporan.instansi)
            row.createCell(col++).setCellValue(laporan.klasifikasi)

            // Ringkasan korban
            row.createCell(col++).setCellValue(meninggal.toString())
            row.createCell(col++).setCellValue(lukaBakar.toString())
            row.createCell(col++).setCellValue(lukaLain.toString())

// 🔹 Tulis detail korban
            if (korbanList.isNotEmpty()) {
                // ✅ Korban pertama ditulis di baris utama
                var korbanCol = col
                val korbanPertama = korbanList.first()
                row.createCell(korbanCol++).setCellValue(korbanPertama.jenisKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.namaKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.jkKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.usiaKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.kondisiFisikKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.nikKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.kkKorban)
                row.createCell(korbanCol++).setCellValue(korbanPertama.ttlKorban)
                row.createCell(korbanCol).setCellValue(korbanPertama.alamatKorban)

                // ✅ Korban berikutnya dibuat baris baru
                for (i in 1 until korbanList.size) {
                    val korban = korbanList[i]
                    val rowKorban = sheet.createRow(rowIndex++)
                    var korbanCol2 = col
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.jenisKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.namaKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.jkKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.usiaKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.kondisiFisikKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.nikKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.kkKorban)
                    rowKorban.createCell(korbanCol2++).setCellValue(korban.ttlKorban)
                    rowKorban.createCell(korbanCol2).setCellValue(korban.alamatKorban)
                }
            }

        }

        // Simpan file
        val file = File(context.getExternalFilesDir(null), "Rekap_Pemadaman_${startDate}_$endDate.xlsx")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        return file
    }
}
