package com.example.sipadam.laporanlainnya.laporankegiatan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.laporanlainnya.laporankegiatan.data.AppDatabaseKegiatan
import com.example.sipadam.laporanlainnya.laporankegiatan.data.dao.KegiatanDao
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan
import com.example.sipadam.laporanlainnya.laporankegiatan.util.ShareUtilsKegiatan
import kotlinx.coroutines.launch
import java.io.File

class LaporanKegiatanActivity : AppCompatActivity() {

    private lateinit var laporanAdapter: KegiatanAdapter
    private lateinit var laporanDao: KegiatanDao
    private lateinit var tvKosong: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_kegiatan)

        // === Toolbar ===
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "LAPORAN KEGIATAN"
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // === RecyclerView ===
        tvKosong = findViewById(R.id.tvKosong)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        laporanAdapter = KegiatanAdapter(
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
            val intent = Intent(this, InputKegiatanActivity::class.java)
            startActivity(intent)
        }

        // === Ambil data dari DB ===
        laporanDao = AppDatabaseKegiatan.getDatabase(this).KegiatanDao()
        laporanDao.getAll().observe(this, Observer { list: List<LaporanKegiatan> ->
            laporanAdapter.updateData(list)
            tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun bukaDetail(laporan: LaporanKegiatan) {
        val intent = Intent(this, DetailKegiatanActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun bukaEdit(laporan: LaporanKegiatan) {
        val intent = Intent(this, EditKegiatanActivity::class.java)
        intent.putExtra("laporan_id", laporan.id)
        startActivity(intent)
    }

    private fun konfirmasiHapus(laporan: LaporanKegiatan) {
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

    private fun deleteLaporan(laporan: LaporanKegiatan) {
        lifecycleScope.launch {
            laporanDao.delete(laporan)
            runOnUiThread {
                Toast.makeText(this@LaporanKegiatanActivity, "Laporan dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showShareOptions(laporan: LaporanKegiatan) {
        ShareUtilsKegiatan.shareAsText(this, laporan)
    }

}
