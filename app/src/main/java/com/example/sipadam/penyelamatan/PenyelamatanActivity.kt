package com.example.sipadam.penyelamatan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.gawatdarurat.ui.GawatDaruratActivity
import com.example.sipadam.penyelamatan.penangananbinatang.ui.PenangananBinatangActivity
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.ui.PencarianPenyelamatanActivity

class PenyelamatanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penyelamatan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Atur judul dan warna teks toolbar agar putih seperti di MainActivity
        supportActionBar?.apply {
            title = "PENYELAMATAN"
            setDisplayHomeAsUpEnabled(true)
        }
        // set warna teks title menjadi putih
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white, theme))

        // tombol back
        toolbar.setNavigationOnClickListener {
            finish()
            // atau startActivity(Intent(this, MainActivity::class.java)) jika mau langsung ke MainActivity
        }

        val btnBinatang = findViewById<CardView>(R.id.btn_binatang)
        btnBinatang.setOnClickListener {
            startActivity(Intent(this, PenangananBinatangActivity::class.java))
        }
        val btnGawatDarurat = findViewById<CardView>(R.id.btn_gawatdarurat)
        btnGawatDarurat.setOnClickListener {
            startActivity(Intent(this, GawatDaruratActivity::class.java))
        }
        val btnPencarianPenyelamatan = findViewById<CardView>(R.id.btn_evakuasi)
        btnPencarianPenyelamatan.setOnClickListener {
            startActivity(Intent(this, PencarianPenyelamatanActivity::class.java))
        }

    }
}
