package com.example.sipadam.penyelamatan.penangananbinatang.ui

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
import com.example.sipadam.penyelamatan.penangananbinatang.data.AppDatabasePenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.data.dao.PenangananBinatangDao
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.util.ShareUtilsBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.util.WordGeneratorBinatang
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

class PenangananBinatangActivity : AppCompatActivity() {

    private lateinit var laporanAdapter: PenangananBinatangAdapter
    private lateinit var laporanDao: PenangananBinatangDao
    private lateinit var tvKosong: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penanganan_binatang)

        // === ✅ Toolbar ===
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // kita pakai TextView sendiri

        // Atur custom title
        val titleView = findViewById<TextView>(R.id.toolbarTitle)
        titleView.text = "PENANGANAN BINATANG"
        titleView.setTextColor(Color.WHITE)

        // Tombol back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Naikkan toolbar biar gak ketiban background
        toolbar.elevation = 8f

        // === RecyclerView ===
        tvKosong = findViewById(R.id.tvKosong)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        laporanAdapter = PenangananBinatangAdapter(
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
            val intent = Intent(this, InputPenangananBinatangActivity::class.java)
            startActivity(intent)
        }

        // === Ambil data dari DB ===
        laporanDao = AppDatabasePenangananBinatang.getDatabase(this).laporanDao()
        laporanDao.getAll().observe(this, Observer { list: List<LaporanPenangananBinatang> ->
            laporanAdapter.updateData(list)
            tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun bukaDetail(laporan: LaporanPenangananBinatang) {
        val intent = Intent(this, DetailPenangananBinatangActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun bukaEdit(laporan: LaporanPenangananBinatang) {
        val intent = Intent(this, EditPenangananBinatangActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun konfirmasiHapus(laporan: LaporanPenangananBinatang) {
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

    private fun deleteLaporan(laporan: LaporanPenangananBinatang) {
        lifecycleScope.launch {
            laporanDao.delete(laporan)
            runOnUiThread {
                Toast.makeText(this@PenangananBinatangActivity, "Laporan dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showShareOptions(laporan: LaporanPenangananBinatang) {
        val options = arrayOf("Bagikan sebagai Teks", "Bagikan sebagai File Word")
        AlertDialog.Builder(this)
            .setTitle("Bagikan Laporan")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> ShareUtilsBinatang.shareAsText(this, laporan, lifecycleScope)
                    1 -> shareAsWord(laporan)
                }
            }
            .show()
    }

    private fun shareAsWord(laporan: LaporanPenangananBinatang) {
        lifecycleScope.launch {
            val file: File = WordGeneratorBinatang.generate(applicationContext, laporan)
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
