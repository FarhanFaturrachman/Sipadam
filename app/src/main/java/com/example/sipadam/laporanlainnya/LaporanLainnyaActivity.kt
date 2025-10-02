package com.example.sipadam.laporanlainnya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.sipadam.R
import com.example.sipadam.laporanlainnya.laporankegiatan.LaporanKegiatanActivity

class LaporanLainnyaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporanlainnya)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Atur judul dan warna teks toolbar agar putih seperti di MainActivity
        supportActionBar?.apply {
            title = "LAPORAN LAINNYA"
            setDisplayHomeAsUpEnabled(true)
        }
        // set warna teks title menjadi putih
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white, theme))

        // tombol back
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // contoh tombol (silakan sesuaikan dengan id di layout activity_laporanlainnya.xml)
        val btnLaporan1 = findViewById<CardView>(R.id.btn_laporankegiatan)
        btnLaporan1.setOnClickListener {
            // ganti dengan activity tujuanmu
            startActivity(Intent(this, LaporanKegiatanActivity::class.java))
        }
    }
}
