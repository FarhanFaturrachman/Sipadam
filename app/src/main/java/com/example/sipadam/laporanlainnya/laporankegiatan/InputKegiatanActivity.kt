package com.example.sipadam.laporanlainnya.laporankegiatan

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.laporanlainnya.laporankegiatan.data.AppDatabaseKegiatan
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class InputKegiatanActivity : AppCompatActivity() {

    private lateinit var db: AppDatabaseKegiatan

    private lateinit var etRegu: AutoCompleteTextView
    private lateinit var etKegiatan: AutoCompleteTextView
    private lateinit var etHari: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etWaktu: EditText
    private lateinit var etLokasi: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var btnSimpan: Button

    // status kegiatan (radio)
    private lateinit var rgKeterangan: RadioGroup
    private lateinit var rbSudah: RadioButton
    private lateinit var rbSedang: RadioButton

    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var dayOfWeekFormat: SimpleDateFormat

    private var isFormChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_kegiatan)

        db = AppDatabaseKegiatan.getDatabase(this)

        // Inisialisasi views
        etRegu = findViewById(R.id.etRegu)
        etKegiatan = findViewById(R.id.etKegiatan)
        etHari = findViewById(R.id.etHari)
        etTanggal = findViewById(R.id.etTanggal)
        etWaktu = findViewById(R.id.etWaktu)
        etLokasi = findViewById(R.id.etLokasi)
        etKeterangan = findViewById(R.id.etKeterangan)
        btnSimpan = findViewById(R.id.btnSimpan)

        rgKeterangan = findViewById(R.id.rgKeterangan)
        rbSudah = findViewById(R.id.rbSudah)
        rbSedang = findViewById(R.id.rbSedang)

        // Format tanggal dan waktu
        calendar = Calendar.getInstance()
        dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

        // Default isi hari/tanggal/waktu sekarang
        etHari.setText(dayOfWeekFormat.format(calendar.time))
        etTanggal.setText(dateFormat.format(calendar.time))
        etWaktu.setText(timeFormat.format(calendar.time))

        // DatePicker
        etTanggal.setOnClickListener { showDatePicker() }
        // TimePicker
        etWaktu.setOnClickListener { showTimePicker() }

        // Dropdown regu
        val reguArray = arrayOf(
            "Regu 1 Pusat", "Regu 2 Pusat", "Regu 3 Pusat",
            "Regu 1 UPTD WIL.1", "Regu 2 UPTD WIL.1", "Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2", "Regu 2 UPTD WIL.2", "Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3", "Regu 2 UPTD WIL.3", "Regu 3 UPTD WIL.3"
        )
        etRegu.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reguArray))
        etRegu.setOnClickListener { etRegu.showDropDown() }

        // Dropdown kegiatan
        val kegiatanList = listOf("Latihan pasukan", "Pengamanan (PAM)", "Sosialisasi dan Edukasi", "Penyemprotan", "Pembersihan")
        etKegiatan.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kegiatanList))
        etKegiatan.setOnClickListener { etKegiatan.showDropDown() }

        // Simpan
        btnSimpan.setOnClickListener {
            if (validateForm()) simpanLaporan()
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val chosen = Calendar.getInstance()
            chosen.set(y, m, d)
            etTanggal.setText(dateFormat.format(chosen.time))
            etHari.setText(dayOfWeekFormat.format(chosen.time))
        }, year, month, day)
        datePicker.show()
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText("Pilih Waktu")
            .build()

        picker.addOnPositiveButtonClickListener {
            val waktu = String.format("%02d:%02d", picker.hour, picker.minute)
            etWaktu.setText(waktu)
        }
        picker.show(supportFragmentManager, "TIME_PICKER")
    }

    private fun validateForm(): Boolean {
        if (etRegu.text.isNullOrBlank()) {
            etRegu.error = "Regu wajib diisi"
            etRegu.requestFocus()
            return false
        }
        if (etKegiatan.text.isNullOrBlank()) {
            etKegiatan.error = "Kegiatan wajib diisi"
            etKegiatan.requestFocus()
            return false
        }
        if (etHari.text.isNullOrBlank()) {
            etHari.error = "Hari wajib diisi"
            etHari.requestFocus()
            return false
        }
        if (etTanggal.text.isNullOrBlank()) {
            etTanggal.error = "Tanggal wajib diisi"
            etTanggal.requestFocus()
            return false
        }
        if (etWaktu.text.isNullOrBlank()) {
            etWaktu.error = "Waktu wajib diisi"
            etWaktu.requestFocus()
            return false
        }
        if (etLokasi.text.isNullOrBlank()) {
            etLokasi.error = "Lokasi wajib diisi"
            etLokasi.requestFocus()
            return false
        }

        // **Status kegiatan wajib**
        if (rgKeterangan.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih status kegiatan (sudah dilaksanakan / sedang berlangsung)", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun simpanLaporan() {
        lifecycleScope.launch {
            // Ambil status kegiatan (radio button)
            val statusKegiatan = when (rgKeterangan.checkedRadioButtonId) {
                R.id.rbSudah -> rbSudah.text.toString()
                R.id.rbSedang -> rbSedang.text.toString()
                else -> ""
            }

            // Ambil keterangan opsional (boleh kosong)
            val keteranganInput = etKeterangan.text.toString().trim()

            val laporan = LaporanKegiatan(
                regu = etRegu.text.toString(),
                kegiatan = etKegiatan.text.toString(),
                hari = etHari.text.toString(),
                tanggal = etTanggal.text.toString(),
                waktu = etWaktu.text.toString(),
                lokasi = etLokasi.text.toString(),
                statusKegiatan = statusKegiatan,
                keterangan = keteranganInput // "" kalau kosong
            )

            withContext(Dispatchers.IO) {
                db.KegiatanDao().insert(laporan)
            }

            runOnUiThread {
                Toast.makeText(this@InputKegiatanActivity, "Laporan tersimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (isFormChanged) {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin keluar tanpa menyimpan?")
                .setPositiveButton("Ya") { _, _ -> super.onBackPressed() }
                .setNegativeButton("Batal", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
