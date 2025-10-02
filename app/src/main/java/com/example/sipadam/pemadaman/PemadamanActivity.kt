package com.example.sipadam.pemadaman

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.pemadaman.data.AppDatabase
import com.example.sipadam.pemadaman.data.dao.LaporanDao
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran
import com.example.sipadam.pemadaman.util.ExcelGenerator
import com.example.sipadam.pemadaman.util.ShareUtils
import com.example.sipadam.pemadaman.util.WordGenerator
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class PemadamanActivity : AppCompatActivity() {

    private lateinit var laporanAdapter: LaporanAdapter
    private lateinit var laporanDao: LaporanDao
    private lateinit var tvKosong: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemadaman)

        // === ✅ Toolbar ===
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // karena kita pakai TextView custom

        val titleView = findViewById<TextView>(R.id.toolbarTitle)
        titleView.text = "PEMADAMAN"
        titleView.setTextColor(Color.WHITE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        // warna icon overflow titik tiga jadi putih
        toolbar.overflowIcon?.setTint(Color.WHITE)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // supaya toolbar tidak ketiban background
        toolbar.elevation = 8f

        // === RecyclerView ===
        tvKosong = findViewById(R.id.tvKosong)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        laporanAdapter = LaporanAdapter(
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
            startActivity(Intent(this, AddLaporanActivity::class.java))
        }

        // === Load data ===
        laporanDao = AppDatabase.getDatabase(this).laporanDao()
        laporanDao.getAll().observe(this, Observer { list: List<LaporanKebakaran> ->
            laporanAdapter.updateData(list)
            tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    // === Toolbar menu ===
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pemadaman, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rekap_excel -> {
                showFilterTanggalDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // === Dialog filter tanggal ===
    private fun showFilterTanggalDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_filter_tanggal, null)
        val etAwal = view.findViewById<EditText>(R.id.etTanggalAwal)
        val etAkhir = view.findViewById<EditText>(R.id.etTanggalAkhir)

        etAwal.setOnClickListener { pickDate(etAwal) }
        etAkhir.setOnClickListener { pickDate(etAkhir) }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Filter Tanggal Rekap")
            .setView(view)
            .setPositiveButton("Rekap", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            val btnRekap = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnRekap.setOnClickListener {
                val awal = etAwal.text.toString()
                val akhir = etAkhir.text.toString()
                if (awal.isNotEmpty() && akhir.isNotEmpty()) {
                    dialog.dismiss()
                    exportExcel(awal, akhir)
                } else {
                    Toast.makeText(this, "Tanggal harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun pickDate(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                target.setText("%04d-%02d-%02d".format(year, month + 1, dayOfMonth))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // === Export Excel ===
    private fun exportExcel(tglAwal: String, tglAkhir: String) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@PemadamanActivity)
            val laporanList = try {
                db.laporanDao().getBetweenDates(tglAwal, tglAkhir)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<LaporanKebakaran>()
            }

            val korbanList = try {
                db.korbanDao().getAll()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList<com.example.sipadam.pemadaman.data.model.Korban>()
            }

            if (laporanList.isEmpty()) {
                val all = try { db.laporanDao().getAllList() } catch (e: Exception) { emptyList<LaporanKebakaran>() }
                val parsedFiltered = try {
                    val sdfIso = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val sdfAlt = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    val sdfLong = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id", "ID"))

                    val start = sdfIso.parse(tglAwal) ?: sdfAlt.parse(tglAwal) ?: sdfLong.parse(tglAwal)
                    val end = sdfIso.parse(tglAkhir) ?: sdfAlt.parse(tglAkhir) ?: sdfLong.parse(tglAkhir)

                    if (start != null && end != null) {
                        all.filter { lap ->
                            val d = try {
                                sdfIso.parse(lap.tanggal)
                            } catch (_: Exception) {
                                try { sdfAlt.parse(lap.tanggal) } catch (_: Exception) {
                                    try { sdfLong.parse(lap.tanggal) } catch (_: Exception) { null }
                                }
                            }
                            d?.let { !it.before(start) && !it.after(end) } ?: false
                        }
                    } else emptyList()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList<LaporanKebakaran>()
                }

                if (parsedFiltered.isNotEmpty()) {
                    proceedExport(parsedFiltered, korbanList, tglAwal, tglAkhir)
                    return@launch
                } else {
                    Toast.makeText(this@PemadamanActivity, "Tidak ada data laporan di rentang tanggal", Toast.LENGTH_SHORT).show()
                    return@launch
                }
            }

            proceedExport(laporanList, korbanList, tglAwal, tglAkhir)
        }
    }

    private fun proceedExport(
        laporanList: List<LaporanKebakaran>,
        korbanList: List<com.example.sipadam.pemadaman.data.model.Korban>,
        tglAwal: String,
        tglAkhir: String
    ) {
        val korbanMap = korbanList.groupBy { it.laporanId }
        val file = ExcelGenerator.generate(this@PemadamanActivity, laporanList, korbanMap, tglAwal, tglAkhir)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(this@PemadamanActivity, "File Excel gagal dibuat atau kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(applicationContext, "$packageName.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Bagikan file Excel"))
    }

    private fun bukaDetail(laporan: LaporanKebakaran) {
        startActivity(Intent(this, DetailLaporanActivity::class.java).apply {
            putExtra("laporan_id", laporan.id)
        })
    }

    private fun bukaEdit(laporan: LaporanKebakaran) {
        startActivity(Intent(this, EditLaporanActivity::class.java).apply {
            putExtra("laporan_id", laporan.id)
        })
    }

    private fun konfirmasiHapus(laporan: LaporanKebakaran) {
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

    private fun deleteLaporan(laporan: LaporanKebakaran) {
        lifecycleScope.launch {
            laporanDao.delete(laporan)
            runOnUiThread {
                Toast.makeText(this@PemadamanActivity, "Laporan dihapus", Toast.LENGTH_SHORT).show()
            }
        }
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
}
