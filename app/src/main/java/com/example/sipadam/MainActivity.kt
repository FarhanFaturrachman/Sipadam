package com.example.sipadam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.sipadam.pemadaman.pemadamankebakaran.PemadamanActivity
import com.example.sipadam.penyelamatan.PenyelamatanActivity
import com.example.sipadam.laporanlainnya.LaporanLainnyaActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Atur judul dan warna teks toolbar
        supportActionBar?.apply {
            title = "SIPADAM"
        }
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white, theme))

        val btnPemadaman: CardView = findViewById(R.id.btn_pemadaman)
        val btnPenyelamatan: CardView = findViewById(R.id.btn_penyelamatan)
        val btnLaporan: CardView = findViewById(R.id.btn_laporan)

        btnPemadaman.setOnClickListener {
            startActivity(Intent(this, PemadamanActivity::class.java))
        }
        btnPenyelamatan.setOnClickListener {
            startActivity(Intent(this, PenyelamatanActivity::class.java))
        }
        btnLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanLainnyaActivity::class.java))
        }
    }
}
