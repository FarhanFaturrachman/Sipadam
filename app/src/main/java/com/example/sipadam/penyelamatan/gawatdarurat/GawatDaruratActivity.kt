package com.example.sipadam.penyelamatan.gawatdarurat.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.gawatdarurat.data.AppDatabaseGawatDarurat
import com.example.sipadam.penyelamatan.gawatdarurat.data.dao.GawatDaruratDao
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat
import com.example.sipadam.penyelamatan.gawatdarurat.util.ShareUtilsGawatDarurat
import com.example.sipadam.penyelamatan.gawatdarurat.util.WordGeneratorGawatDarurat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.core.content.FileProvider
import com.example.sipadam.penyelamatan.gawatdarurat.DetailGawatDaruratActivity
import com.example.sipadam.penyelamatan.gawatdarurat.EditGawatDaruratActivity
import com.example.sipadam.penyelamatan.gawatdarurat.GawatDaruratAdapter
import com.example.sipadam.penyelamatan.gawatdarurat.InputGawatDaruratActivity
import kotlinx.coroutines.launch
import java.io.File

class GawatDaruratActivity : AppCompatActivity() {

    private lateinit var laporanAdapter: GawatDaruratAdapter
    private lateinit var laporanDao: GawatDaruratDao
    private lateinit var tvKosong: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gawat_darurat)

        // === ✅ Toolbar ===
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // kita pakai custom TextView untuk title

        // Custom title text
        val titleView = findViewById<TextView>(R.id.toolbarTitle)
        titleView.text = "GAWAT DARURAT (SPGDT)"
        titleView.setTextColor(Color.WHITE)

        // Tombol back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Pastikan toolbar di atas background
        toolbar.elevation = 8f

        // === RecyclerView ===
        tvKosong = findViewById(R.id.tvKosong)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        laporanAdapter = GawatDaruratAdapter(
            laporanList = emptyList(),
            onDetailClick = { laporan -> bukaDetail(laporan) },
            onEditClick = { laporan -> bukaEdit(laporan) },
            onDeleteClick = { laporan -> konfirmasiHapus(laporan) },
            onShareClick = { laporan -> showShareOptions(laporan) }
        )
        recyclerView.adapter = laporanAdapter

        // === Tombol tambah laporan ===
        val btnTambah: CardView = findViewById(R.id.btnTambahLaporan)
        btnTambah.setOnClickListener {
            val intent = Intent(this, InputGawatDaruratActivity::class.java)
            startActivity(intent)
        }

        // === Ambil data dari DB ===
        laporanDao = AppDatabaseGawatDarurat.getDatabase(this).laporanDao()
        laporanDao.getAll().observe(this, Observer { list: List<LaporanGawatDarurat> ->
            laporanAdapter.updateData(list)
            tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun bukaDetail(laporan: LaporanGawatDarurat) {
        val intent = Intent(this, DetailGawatDaruratActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun bukaEdit(laporan: LaporanGawatDarurat) {
        val intent = Intent(this, EditGawatDaruratActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun konfirmasiHapus(laporan: LaporanGawatDarurat) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Hapus Laporan")
            .setMessage("Apakah Anda yakin ingin menghapus laporan ini?")
            .setPositiveButton("Hapus") { _, _ -> deleteLaporan(laporan) }
            .setNegativeButton("Batal", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
        }
        dialog.show()
    }

    private fun deleteLaporan(laporan: LaporanGawatDarurat) {
        lifecycleScope.launch {
            laporanDao.delete(laporan)
            runOnUiThread {
                Toast.makeText(this@GawatDaruratActivity, "Laporan dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showShareOptions(laporan: LaporanGawatDarurat) {
        val options = arrayOf("Bagikan sebagai Teks", "Bagikan sebagai File Word")
        AlertDialog.Builder(this)
            .setTitle("Bagikan Laporan")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> ShareUtilsGawatDarurat.shareAsText(this, laporan, lifecycleScope)
                    1 -> shareAsWord(laporan)
                }
            }
            .show()
    }

    private fun shareAsWord(laporan: LaporanGawatDarurat) {
        lifecycleScope.launch {
            val file: File = WordGeneratorGawatDarurat.generate(applicationContext, laporan)
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
}
