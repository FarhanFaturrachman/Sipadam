package com.example.sipadam.penyelamatan.pencarianpenyelamatan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.AppDatabasePencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.LaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.KorbanLaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.SaksiLaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.util.ShareUtilsPencarian
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.util.WordGeneratorPencarian
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DetailPencarianPenyelamatanActivity : AppCompatActivity() {

    private var laporanId: Long = -1L
    private var laporan: LaporanPencarianPenyelamatan? = null
    private lateinit var containerKorban: LinearLayout
    private lateinit var containerSaksi: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pencarian_penyelamatan)

        // Status bar putih + ikon gelap
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Detail Pencarian & Penyelamatan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        containerKorban = findViewById(R.id.containerKorban)
        containerSaksi = findViewById(R.id.containerSaksi)


        laporanId = intent.getLongExtra("laporan_id", -1L)
        if (laporanId == -1L) {
            Toast.makeText(this, "ID Laporan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadData()

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            laporan?.let {
                val intent = Intent(this, EditPencarianPenyelamatanActivity::class.java)
                intent.putExtra("laporan_id", it.id)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
            laporan?.let { showShareOptions(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (laporanId != -1L) {
            loadData()
        }
    }

    private fun loadData() {
        val db = AppDatabasePencarianPenyelamatan.getDatabase(this)

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) {
                db.laporanDao().getById(laporanId)
            }

            if (laporan == null) {
                Toast.makeText(
                    this@DetailPencarianPenyelamatanActivity,
                    "Data laporan tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            val korbanList = withContext(Dispatchers.IO) {
                db.korbanDao().getKorbanByLaporanNow(laporanId)
            }
            val saksiList = withContext(Dispatchers.IO) {
                db.saksiDao().getSaksiByLaporan(laporanId)
            }
            showData(laporan!!, korbanList, saksiList)


        }
    }

    private fun showData(
        l: LaporanPencarianPenyelamatan,
        korbanList: List<KorbanLaporanPencarianPenyelamatan>,
        saksiList: List<SaksiLaporanPencarianPenyelamatan>
    ) {
        fun setText(id: Int, value: String?) {
            val tv = findViewById<TextView>(id)
            tv.text = if (value.isNullOrBlank()) "-" else value
        }

        // Informasi umum
        setText(R.id.tvJenisKegiatan, l.jenisKegiatan)
        setText(R.id.tvRegu, l.regu)

        // Waktu
        setText(R.id.tvHariDiterima, l.hariDiterima)
        setText(R.id.tvTanggalDiterima, l.tanggalDiterima)
        setText(R.id.tvWaktuDiterima, l.waktuDiterima)
        setText(R.id.tvHariTindak, l.hariTindak)
        setText(R.id.tvTanggalTindak, l.tanggalTindak)
        setText(R.id.tvWaktuTindak, l.waktuTindak)

        // Lokasi
        setText(R.id.tvAlamat, l.alamat)
        setText(R.id.tvKabupaten, l.kabupaten)
        setText(R.id.tvKecamatan, l.kecamatan)
        setText(R.id.tvDesa, l.desa)
        setText(R.id.tvJarak, l.jarak)
        setText(R.id.tvKoordinatDesimal, l.koordinatDesimal)
        setText(R.id.tvKoordinatGeografi, l.koordinatGeografi)

        // Identitas Pelapor
        setText(R.id.tvNamaPelapor, l.namaPelapor)
        setText(R.id.tvUsiaPelapor, l.usiaPelapor)
        setText(R.id.tvNoHpPelapor, l.noHpPelapor)
        setText(R.id.tvAlamatPelapor, l.alamatPelapor)

        // Jumlah korban selamat
        setText(R.id.tvKorbanSelamat, l.korbanSelamat)

        // Detail korban
        containerKorban.removeAllViews()
        if (korbanList.isNotEmpty()) {
            korbanList.forEach { korban ->
                val view = layoutInflater.inflate(R.layout.item_korban_detail_pencarian, containerKorban, false)

                view.findViewById<TextView>(R.id.tvJenisKorban).text = korban.jenisKorban
                view.findViewById<TextView>(R.id.tvNamaKorban).text = korban.namaKorban
                view.findViewById<TextView>(R.id.tvJkKorban).text = korban.jkKorban
                view.findViewById<TextView>(R.id.tvUsiaKorban).text = korban.usiaKorban
                view.findViewById<TextView>(R.id.tvKondisiFisikKorban).text = korban.kondisiFisikKorban
                view.findViewById<TextView>(R.id.tvNikKorban).text = korban.nikKorban
                view.findViewById<TextView>(R.id.tvKkKorban).text = korban.kkKorban
                view.findViewById<TextView>(R.id.tvTtlKorban).text = korban.ttlKorban
                view.findViewById<TextView>(R.id.tvAlamatKorban).text = korban.alamatKorban
                view.findViewById<TextView>(R.id.tvCiriciri).text = korban.ciriciri
                containerKorban.addView(view)
            }
        } else {
            val tvKosong = TextView(this).apply {
                text = "Tidak ada data korban"
                setTextColor(getColor(android.R.color.darker_gray))
                textSize = 14f
            }
            containerKorban.addView(tvKosong)
        }

        // Detail saksi
        containerSaksi.removeAllViews()
        if (saksiList.isNotEmpty()) {
            saksiList.forEach { saksi ->
                val view = layoutInflater.inflate(R.layout.item_saksi_detail_pencarian, containerSaksi, false)
                view.findViewById<TextView>(R.id.tvNamaSaksi).text = saksi.namaSaksi
                view.findViewById<TextView>(R.id.tvJkSaksi).text = saksi.jkSaksi ?: "-"
                view.findViewById<TextView>(R.id.tvUmurSaksi).text = saksi.umurSaksi
                view.findViewById<TextView>(R.id.tvHpSaksi).text = saksi.noHpSaksi
                view.findViewById<TextView>(R.id.tvAlamatSaksi).text = saksi.alamatSaksi
                containerSaksi.addView(view)
            }
        } else {
            val tvKosong = TextView(this).apply {
                text = "Tidak ada data saksi"
                setTextColor(getColor(android.R.color.darker_gray))
                textSize = 14f
            }
            containerSaksi.addView(tvKosong)
        }


        // Kendaraan, APD, Upaya, Petugas
        setText(R.id.tvRencana, l.rencana)
        setText(R.id.tvKendaraan, l.kendaraan)
        setText(R.id.tvApd, l.apd)
        setText(R.id.tvKronologi, l.kronologi)
        setText(R.id.tvUpaya, l.upaya)
        setText(R.id.tvPetugas, l.petugas)
        setText(R.id.tvInstansi, l.instansi)
    }

    private fun showShareOptions(laporan: LaporanPencarianPenyelamatan) {
        val options = arrayOf("Bagikan sebagai Teks", "Bagikan sebagai File Word")
        AlertDialog.Builder(this)
            .setTitle("Bagikan Laporan")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> ShareUtilsPencarian.shareAsText(this, laporan, lifecycleScope)
                    1 -> shareAsWord(laporan)
                }
            }
            .show()
    }

    private fun shareAsWord(laporan: LaporanPencarianPenyelamatan) {
        lifecycleScope.launch {
            val file: File = WordGeneratorPencarian.generate(applicationContext, laporan)
            val uri = FileProvider.getUriForFile(
                applicationContext,
                "$packageName.provider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Bagikan file Word"))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
