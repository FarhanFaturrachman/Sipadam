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

class EditKegiatanActivity : AppCompatActivity() {

    private lateinit var db: AppDatabaseKegiatan
    private var laporanId: Long = -1L
    private var laporan: LaporanKegiatan? = null

    private lateinit var etRegu: AutoCompleteTextView
    private lateinit var etKegiatan: AutoCompleteTextView
    private lateinit var etHari: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etWaktu: EditText
    private lateinit var etLokasi: EditText
    private lateinit var etKeterangan: EditText
    private lateinit var btnUpdate: Button

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
        laporanId = intent.getLongExtra("laporan_id", -1)

        etRegu = findViewById(R.id.etRegu)
        etKegiatan = findViewById(R.id.etKegiatan)
        etHari = findViewById(R.id.etHari)
        etTanggal = findViewById(R.id.etTanggal)
        etWaktu = findViewById(R.id.etWaktu)
        etLokasi = findViewById(R.id.etLokasi)
        etKeterangan = findViewById(R.id.etKeterangan)
        btnUpdate = findViewById(R.id.btnSimpan)
        btnUpdate.text = "Update Laporan"

        rgKeterangan = findViewById(R.id.rgKeterangan)
        rbSudah = findViewById(R.id.rbSudah)
        rbSedang = findViewById(R.id.rbSedang)

        calendar = Calendar.getInstance()
        dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

        etTanggal.setOnClickListener { showDatePicker() }
        etWaktu.setOnClickListener { showTimePicker() }

        val reguArray = arrayOf(
            "Regu 1 Pusat", "Regu 2 Pusat", "Regu 3 Pusat",
            "Regu 1 UPTD WIL.1", "Regu 2 UPTD WIL.1", "Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2", "Regu 2 UPTD WIL.2", "Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3", "Regu 2 UPTD WIL.3", "Regu 3 UPTD WIL.3"
        )
        etRegu.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reguArray))
        etRegu.setOnClickListener { etRegu.showDropDown() }

        val kegiatanList = listOf("Latihan pasukan", "Pengamanan (PAM)", "Sosialisasi dan Edukasi", "Penyemprotan", "Pembersihan")
        etKegiatan.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kegiatanList))
        etKegiatan.setOnClickListener { etKegiatan.showDropDown() }

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) { db.KegiatanDao().getById(laporanId) }
            laporan?.let { isiForm(it) }
        }

        btnUpdate.setOnClickListener {
            if (validateForm()) updateLaporan()
        }
    }

    private fun isiForm(l: LaporanKegiatan) {
        etRegu.setText(l.regu)
        etKegiatan.setText(l.kegiatan)
        etHari.setText(l.hari)
        etTanggal.setText(l.tanggal)
        etWaktu.setText(l.waktu)
        etLokasi.setText(l.lokasi)
        etKeterangan.setText(l.keterangan)

        // status kegiatan ke radio
        when (l.statusKegiatan) {
            rbSudah.text.toString() -> rgKeterangan.check(R.id.rbSudah)
            rbSedang.text.toString() -> rgKeterangan.check(R.id.rbSedang)
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
            etWaktu.setText(String.format("%02d:%02d", picker.hour, picker.minute))
        }
        picker.show(supportFragmentManager, "TIME_PICKER")
    }

    private fun validateForm(): Boolean {
        if (etRegu.text.isNullOrBlank()) { etRegu.error = "Regu wajib diisi"; return false }
        if (etKegiatan.text.isNullOrBlank()) { etKegiatan.error = "Kegiatan wajib diisi"; return false }
        if (etHari.text.isNullOrBlank()) { etHari.error = "Hari wajib diisi"; return false }
        if (etTanggal.text.isNullOrBlank()) { etTanggal.error = "Tanggal wajib diisi"; return false }
        if (etWaktu.text.isNullOrBlank()) { etWaktu.error = "Waktu wajib diisi"; return false }
        if (etLokasi.text.isNullOrBlank()) { etLokasi.error = "Lokasi wajib diisi"; return false }
        if (rgKeterangan.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih status kegiatan (sudah dilaksanakan / sedang berlangsung)", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun updateLaporan() {
        laporan?.let { old ->
            lifecycleScope.launch {
                val status = when (rgKeterangan.checkedRadioButtonId) {
                    R.id.rbSudah -> rbSudah.text.toString()
                    R.id.rbSedang -> rbSedang.text.toString()
                    else -> ""
                }

                val updated = old.copy(
                    regu = etRegu.text.toString(),
                    kegiatan = etKegiatan.text.toString(),
                    hari = etHari.text.toString(),
                    tanggal = etTanggal.text.toString(),
                    waktu = etWaktu.text.toString(),
                    lokasi = etLokasi.text.toString(),
                    statusKegiatan = status,
                    keterangan = etKeterangan.text.toString().trim()
                )

                withContext(Dispatchers.IO) { db.KegiatanDao().update(updated) }

                runOnUiThread {
                    Toast.makeText(this@EditKegiatanActivity, "Laporan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                }
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
        } else super.onBackPressed()
    }
}
