package com.example.sipadam.laporanlainnya.laporankegiatan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.laporanlainnya.laporankegiatan.data.AppDatabaseKegiatan
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan
import com.example.sipadam.laporanlainnya.laporankegiatan.util.ShareUtilsKegiatan
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailKegiatanActivity : AppCompatActivity() {

    private var laporanId: Long = -1L
    private var laporan: LaporanKegiatan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kegiatan)

        // Status bar putih + ikon gelap
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Detail Kegiatan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        laporanId = intent.getLongExtra("laporan_id", -1L)
        if (laporanId == -1L) {
            Toast.makeText(this, "ID Laporan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadData()

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            laporan?.let {
                val intent = Intent(this, EditKegiatanActivity::class.java)
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
        val db = AppDatabaseKegiatan.getDatabase(this)

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) {
                db.KegiatanDao().getById(laporanId)
            }

            if (laporan == null) {
                Toast.makeText(
                    this@DetailKegiatanActivity,
                    "Data laporan tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            showData(laporan!!)
        }
    }

    private fun showData(l: LaporanKegiatan) {
        fun setText(id: Int, value: String?) {
            val tv = findViewById<TextView>(id)
            tv.text = if (value.isNullOrBlank()) "-" else value
        }

        // Isi data ke layout
        setText(R.id.tvRegu, l.regu)
        setText(R.id.tvKegiatan, l.kegiatan)
        setText(R.id.tvHari, l.hari)
        setText(R.id.tvTanggal, l.tanggal)
        setText(R.id.tvWaktu, l.waktu)
        setText(R.id.tvLokasi, l.lokasi)
        setText(R.id.tvKeterangan, l.keterangan)
        setText(R.id.tvStatusKegiatan, l.statusKegiatan)
    }


    private fun showShareOptions(laporan: LaporanKegiatan) {
        ShareUtilsKegiatan.shareAsText(this, laporan)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
