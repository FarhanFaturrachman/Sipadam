package com.example.sipadam.pemadaman

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.pemadaman.data.AppDatabase
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran
import com.example.sipadam.pemadaman.util.ShareUtils
import com.example.sipadam.pemadaman.util.WordGenerator
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DetailLaporanActivity : AppCompatActivity() {

    private lateinit var containerKorban: LinearLayout
    private var laporanId: Long = -1L
    private var laporan: LaporanKebakaran? = null

    // Launcher untuk membuka EditLaporanActivity dan menerima hasilnya
    private val editLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)

        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // ✅ Hubungkan toolbar di XML dengan ActionBar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Detail Laporan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        containerKorban = findViewById(R.id.containerKorban)
        laporanId = intent.getLongExtra("laporan_id", -1L)

        if (laporanId == -1L) {
            Toast.makeText(this, "ID Laporan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadData()

        // Tombol Edit
        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            laporan?.let {
                val intent = Intent(this, EditLaporanActivity::class.java)
                intent.putExtra("laporan_id", it.id)
                editLauncher.launch(intent)
            }
        }

        // Tombol Share
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            laporan?.let {
                showShareOptions(it)
            }
        }
    }

    private fun loadData() {
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) {
                db.laporanDao().getById(laporanId)
            }

            if (laporan == null) {
                Toast.makeText(this@DetailLaporanActivity, "Data laporan tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            showData(laporan!!)

            val korbanList = withContext(Dispatchers.IO) {
                db.korbanDao().getKorbanByLaporan(laporan!!.id)
            }

            containerKorban.removeAllViews()
            if (korbanList.isNotEmpty()) {
                korbanList.forEach { korban ->
                    val view = layoutInflater.inflate(R.layout.item_korban_detail, containerKorban, false)
                    view.findViewById<TextView>(R.id.tvJenisKorban).text = korban.jenisKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvNamaKorban).text = korban.namaKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvJkKorban).text = korban.jkKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvUsiaKorban).text = korban.usiaKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvKondisiFisikKorban).text = korban.kondisiFisikKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvNikKorban).text = korban.nikKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvKkKorban).text = korban.kkKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvTtlKorban).text = korban.ttlKorban ?: "-"
                    view.findViewById<TextView>(R.id.tvAlamatKorban).text = korban.alamatKorban ?: "-"
                    containerKorban.addView(view)
                }
            } else {
                val tvKosong = TextView(this@DetailLaporanActivity).apply {
                    text = "Tidak ada data korban"
                    setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                    textSize = 14f
                }
                containerKorban.addView(tvKosong)
            }
        }
    }

    private fun showData(l: LaporanKebakaran) {
        fun setText(id: Int, value: String?) {
            val tv = findViewById<TextView>(id)
            tv.text = value ?: "-"
        }
        setText(R.id.tvHari, l.hari)
        setText(R.id.tvTanggal, l.tanggal)
        setText(R.id.tvJenisKejadian, l.jenisKejadian)
        setText(R.id.tvWaktuLaporan, l.waktuLaporan)
        setText(R.id.tvWaktuSampai, l.waktuSampai)
        setText(R.id.tvJarak, l.jarak)
        setText(R.id.tvResponTime, l.responTime)
        setText(R.id.tvWaktuKembali, l.waktuKembali)
        setText(R.id.tvObjek, l.objek)
        setText(R.id.tvLokasi, "${l.alamatDetail}, Desa ${l.desa}, Kec. ${l.kecamatan}, Kab. ${l.kabupaten}")
        setText(R.id.tvKoordinat, l.koordinat)
        setText(R.id.tvKoordinatGeografi, l.koordinatGeografi)
        setText(R.id.tvLuas, l.luas)
        setText(R.id.tvNamaPelapor, l.namaPelapor)
        setText(R.id.tvHpPelapor, l.hpPelapor)
        setText(R.id.tvAlamatPelapor, l.alamatPelapor)
        setText(R.id.tvNamaPemilik, l.namaPemilik)
        setText(R.id.tvHpPemilik, l.hpPemilik)
        setText(R.id.tvAlamatPemilik, l.alamatPemilik)
        setText(R.id.tvAsetSelamat, l.asetSelamat)
        setText(R.id.tvTafsiranSelamat, l.tafsiranSelamat)
        setText(R.id.tvAsetTerbakar, l.asetTerbakar)
        setText(R.id.tvDokumenTerbakar, l.dokumenTerbakar)
        setText(R.id.tvTafsiranTerbakar, l.tafsiranTerbakar)
        setText(R.id.tvKronologi, l.deskripsiKronologi)
        setText(R.id.tvPenanggulangan, l.deskripsiPenanggulangan)
        setText(R.id.tvKendaraan, l.kendaraan)
        setText(R.id.tvPeralatan, l.peralatan)
        setText(R.id.tvRegu, l.regu)
        setText(R.id.tvReguLapor, l.reguLapor)
        setText(R.id.tvPetugas, l.petugas)
        setText(R.id.tvInstansi, l.instansi)
        setText(R.id.tvKlasifikasi, l.klasifikasi)
        setText(R.id.tvKorbanSelamat, l.korbanSelamat)
    }

    private fun showShareOptions(laporan: LaporanKebakaran) {
        val options = arrayOf("Bagikan sebagai Teks", "Bagikan sebagai File Word")
        AlertDialog.Builder(this)
            .setTitle("Bagikan Laporan")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> ShareUtils.shareAsText(this, laporan, lifecycleScope)
                    1 -> shareAsWord(laporan)
                }
            }
            .show()
    }

    private fun shareAsWord(laporan: LaporanKebakaran) {
        lifecycleScope.launch {
            val file: File = WordGenerator.generate(applicationContext, laporan)
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
