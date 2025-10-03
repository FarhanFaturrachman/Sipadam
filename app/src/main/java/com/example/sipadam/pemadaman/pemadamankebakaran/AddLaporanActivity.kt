package com.example.sipadam.pemadaman.pemadamankebakaran

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.pemadaman.pemadamankebakaran.data.AppDatabase
import com.example.sipadam.pemadaman.pemadamankebakaran.data.model.Korban
import com.example.sipadam.pemadaman.pemadamankebakaran.data.model.LaporanKebakaran
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.get

class AddLaporanActivity : AppCompatActivity() {
    private lateinit var reguLapor: AutoCompleteTextView
    private lateinit var layoutKorbanContainer: LinearLayout
    private lateinit var layoutKendaraan: LinearLayout
    private lateinit var layoutRegu: LinearLayout
    private val jenisKorbanOptions = arrayOf("Meninggal Dunia", "Luka Bakar", "Luka Fisik Lainnya")

    private val kendaraanArray = arrayOf(
        "KR 01 (T 9909 A)","KR 02 (T 9910 A)","KR 03 (T 9925 B)","KR 04 (T 9901 B)",
        "KR 05 (T 8115 A)","KR 06 (T 9942 J)","KR 07 (T 9941 J)","Ambulance (T 9914 B)",
        "Toyota Hilux (T 8350 A)","Toyota Double Cabin (T 8454 A)","Honda Trail CRF (T 3484 B)",
        "Honda Trail CRF (T 2031 C)","Honda Trail CRF (T 3482 B)","Honda Trail CRF (T 5584 B)",
        "Honda Trail CRF (T 4153 B)"
    )

    private val reguArray = arrayOf(
        "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
        "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
        "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
        "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
    )
    private val dokumenArray = arrayOf("KTP", "KK", "Ijazah", "BPKB", "STNK")
    private val checkBoxDokumenList = mutableListOf<CheckBox>()
    private lateinit var layoutDokumen: LinearLayout
    private lateinit var etDokumenLainnya: EditText
    private lateinit var etKendaraanLainnya: EditText


    private val checkBoxKendaraanList = mutableListOf<CheckBox>()
    private val checkBoxReguList = mutableListOf<CheckBox>()

    private lateinit var etHari: AutoCompleteTextView
    private lateinit var etTanggal: EditText
    private lateinit var hariList: List<String>
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var calendar: Calendar

    private lateinit var etKoordinatGeografi: EditText
    private lateinit var etKoordinat: EditText
    private lateinit var btnTambahKoordinat: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var etAlamatDetail: EditText
    private lateinit var etKabupaten: AutoCompleteTextView
    private lateinit var etKecamatan: AutoCompleteTextView
    private lateinit var etDesa: AutoCompleteTextView

    // Flag untuk deteksi perubahan
    private var isFormChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_laporan)

        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        supportActionBar?.title = "Tambah Laporan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutKorbanContainer = findViewById(R.id.layoutKorbanContainer)
        layoutKendaraan = findViewById(R.id.layoutKendaraan)
        layoutRegu = findViewById(R.id.layoutRegu)

        etHari = findViewById(R.id.etHari)
        etTanggal = findViewById(R.id.etTanggal)
        etKoordinat = findViewById(R.id.etKoordinat)
        etKoordinatGeografi = findViewById(R.id.etKoordinatGeografi)
        btnTambahKoordinat = findViewById(R.id.btnTambahKoordinat)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).setMaxUpdates(1).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    etKoordinat.setText("$lat, $lon")
                    etKoordinatGeografi.setText(convertToDMS(lat, lon))
                } else {
                    Toast.makeText(this@AddLaporanActivity, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }


        // Hari & tanggal default
        hariList = listOf("Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu")
        val adapterHari = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, hariList)
        etHari.setAdapter(adapterHari)
        etHari.setOnClickListener { etHari.showDropDown() }

        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val hariIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1
        etHari.setText(hariList[hariIndex], false)
        etTanggal.setText(dateFormat.format(calendar.time))

        etTanggal.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val chosenCalendar = Calendar.getInstance()
                chosenCalendar.set(y, m, d)
                etTanggal.setText(dateFormat.format(chosenCalendar.time))
                val newHariIndex = chosenCalendar.get(Calendar.DAY_OF_WEEK) - 1
                etHari.setText(hariList[newHariIndex], false)
            }, year, month, day)
            datePicker.show()
        }

        reguLapor = findViewById(R.id.regu_lapor)


        val reguArray = arrayOf(
            "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
        )

        val reguAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reguArray)
        reguLapor.setAdapter(reguAdapter)
        reguLapor.setOnClickListener { reguLapor.showDropDown() }

        val etWaktuLaporan = findViewById<EditText>(R.id.etWaktuLaporan)
        val etWaktuSampai = findViewById<EditText>(R.id.etWaktuSampai)
        val etWaktuKembali = findViewById<EditText>(R.id.etWaktuKembali)

        fun showTimePicker(target: EditText) {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // bisa CLOCK_12H juga
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .setTitleText("Pilih Waktu")
                .build()

            picker.addOnPositiveButtonClickListener {
                val waktu = String.format("%02d:%02d", picker.hour, picker.minute)
                target.setText(waktu)
            }

            picker.show(supportFragmentManager, "TIME_PICKER")
        }


        etWaktuLaporan.setOnClickListener { showTimePicker(etWaktuLaporan) }
        etWaktuSampai.setOnClickListener { showTimePicker(etWaktuSampai) }
        etWaktuKembali.setOnClickListener { showTimePicker(etWaktuKembali) }
        // === Sampai sini ===

        layoutDokumen = findViewById(R.id.layoutDokumen)
        etDokumenLainnya = findViewById(R.id.etDokumenLainnya)
        etKendaraanLainnya = findViewById(R.id.etKendaraanLainnya)

// Generate checkbox kendaraan
        for (item in kendaraanArray) {
            val checkBox = CheckBox(this)
            checkBox.text = item
            checkBox.setOnCheckedChangeListener { _, _ -> isFormChanged = true }
            layoutKendaraan.addView(checkBox)
            checkBoxKendaraanList.add(checkBox)
        }

// Generate checkbox dokumen
        for (item in dokumenArray) {
            val checkBox = CheckBox(this)
            checkBox.text = item
            checkBox.setOnCheckedChangeListener { _, _ -> isFormChanged = true }
            layoutDokumen.addView(checkBox)
            checkBoxDokumenList.add(checkBox)
        }

        for (item in reguArray) {
            val checkBox = CheckBox(this)
            checkBox.text = item
            checkBox.setOnCheckedChangeListener { _, _ -> isFormChanged = true }
            layoutRegu.addView(checkBox)
            checkBoxReguList.add(checkBox)
        }

        // Tambah korban
        findViewById<Button>(R.id.btnTambahKorban).setOnClickListener { tambahFormKorban() }

        // Simpan laporan
        findViewById<Button>(R.id.btnSimpan).setOnClickListener {
            if (validateForm()) {
                isFormChanged = false
                simpanLaporan()
            }
        }

        // Ambil koordinat
        btnTambahKoordinat.setOnClickListener {
            etKoordinat.text.clear()
            getLocation()
            isFormChanged = true
        }

        etAlamatDetail = findViewById(R.id.etAlamatDetail)
        etKabupaten = findViewById(R.id.etKabupaten)
        etKecamatan = findViewById(R.id.etKecamatan)
        etDesa = findViewById(R.id.etDesa)

        // Pasang listener perubahan pada semua EditText/AutoComplete
        val allEditableViews = listOf(
            etHari, etTanggal, etKoordinat, etAlamatDetail,
            etKabupaten, etKecamatan, etDesa
        )
        for (view in allEditableViews) {
            if (view is EditText) {
                view.addTextChangedListener { isFormChanged = true }
            } else if (view is AutoCompleteTextView) {
                view.addTextChangedListener { isFormChanged = true }
            }
        }
        // Data Kabupaten & Kecamatan & Desa (sama seperti versi kamu)
        val kabupatenList = listOf("Purwakarta")
        val adapterKabupaten =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kabupatenList)
        etKabupaten.setAdapter(adapterKabupaten)
        etKabupaten.setText("Purwakarta", false)
        etKabupaten.setOnClickListener { etKabupaten.showDropDown() }
        val kecamatanMap = mapOf(
            "Babakancikao" to listOf(
                "Desa Babakancikao", "Desa Cicadas", "Desa Cilangkap", "Desa Cigelam", "Desa Ciwareng", "Desa Hegarmanah", "Desa Kadumekar", "Desa Maracang", "Desa Mulyamekar"
            ),
            "Bojong" to listOf(
                "Desa Bojong Barat", "Desa Bojong Timur", "Desa Cibingbin", "Desa Cihanjawar", "Desa Cikeris", "Desa Cileunca", "Desa Cipeundeuy",
                "Desa Kertasari", "Desa Pangkalan", "Desa Pasanggrahan", "Desa Pawenang", "Desa Sindangpanon", "Desa Sindangsari", "Desa Sukamanah"
            ),
            "Bungursari" to listOf(
                "Desa Bungursari", "Desa Cibening", "Desa Cibodas", "Desa Cibungur", "Desa Cikopo", "Desa Cinangka", "Desa Ciwangi", "Desa Dangdeur",
                "Desa Karangmukti", "Desa Wanakerta"
            ),
            "Campaka" to listOf(
                "Desa Benteng", "Desa Campaka", "Desa Campakasari", "Desa Cijaya", "Desa Cijunti", "Desa Cimahi", "Desa Cisaat", "Desa Cikumpay",
                "Desa Cirende", "Desa Kertamukti"
            ),
            "Cibatu" to listOf(
                "Desa Cibatu", "Desa Cibukamanah", "Desa Cikadu", "Desa Cilandak", "Desa Cipancur", "Desa Cipinang", "Desa Cirangkong",
                "Desa Ciparungsari", "Desa Karyamekar", "Desa Wanawali"
            ),
            "Darangdan" to listOf(
                "Desa Cilingga", "Desa Darangdan", "Desa Depok", "Desa Gununghejo", "Desa Legoksari", "Desa Linggamukti", "Desa Linggasari",
                "Desa Mekarsari", "Desa Nagrak", "Desa Nangewer", "Desa Neglasari", "Desa Pasirangin", "Desa Sadarkarya", "Desa Sawit", "Desa Sirnamanah"
            ),
            "Jatiluhur" to listOf(
                "Desa Bunder", "Desa Cibinong", "Desa Cikaobandung", "Desa Cilegong", "Desa Cisalada", "Desa Jatiluhur", "Desa Jatimekar",
                "Desa Kembangkuning", "Desa Mekargalih", "Desa Parakanlima"
            ),
            "Kiarapedes" to listOf(
                "Desa Cibeber", "Desa Ciracas", "Desa Gardu", "Desa Kiarapedes", "Desa Margaluyu", "Desa Mekarjaya", "Desa Parakan Garokgek",
                "Desa Pusakamulya", "Desa Sumbersari", "Desa Taringgul Landeuh"
            ),
            "Maniis" to listOf(
                "Desa Cijati", "Desa Ciramahilir", "Desa Citamiang", "Desa Gunungkarung", "Desa Pasirjambu", "Desa Sinargalih", "Desa Sukamukti","Desa Tegaldatar"
            ),
            "Pasawahan" to listOf(
                "Desa Cihuni", "Desa Ciherang", "Desa Cidahu", "Desa Kertajaya", "Desa Lebakanyar", "Desa Margasari", "Desa Pasawahan",
                "Desa Pasawahananyar", "Desa Pasawahan Kidul", "Desa Sawah Kulon", "Desa Selaawi", "Desa Warung Kadu"
            ),
            "Plered" to listOf(
                "Desa Plered", "Desa Anjun", "Desa Gandasoli", "Desa Babakansari", "Desa Gandamekar", "Desa Citeko", "Desa Citeko Kaler",
                "Desa Linggarsari", "Desa Rawasari", "Desa Palinggihan", "Desa Pamoyanan", "Desa Sempur", "Desa Cibogohilir", "Desa Cibogogirang", "Desa Sindangsari", "Desa Liunggunung"
            ),
            "Pondoksalam" to listOf(
                "Desa Bungurjaya", "Desa Galudra", "Desa Gurudug", "Desa Parakansalam", "Desa Pondokbungur", "Desa Salamjaya",
                "Desa Salammulya", "Desa Salem", "Desa Situ", "Desa Sukajadi", "Desa Tanjungsari"
            ),
            "Purwakarta" to listOf(
                "Kelurahan Nagri Kidul", "Kelurahan Nagri Tengah", "Kelurahan Sindangkasih", "Kelurahan Cipaisan", "Kelurahan Nagri Kaler", "Kelurahan Tegalmunjul",
                "Kelurahan Ciseureuh", "Kelurahan Munjuljaya", "Kelurahan Purwamekar", "Desa Citalang"
            ),
            "Sukatani" to listOf(
                "Desa Sukatani", "Desa Cibodas", "Desa Cipicung", "Desa Cianting Utara", "Desa Cianting", "Desa Pasirmunjul", "Desa Cijantung", "Desa Cilalawi",
                "Desa Tajursindang", "Desa Malangnengah", "Desa Sukajaya", "Desa Panyindangan", "Desa Sindanglaya", "Desa Sukamaju"
            ),
            "Sukasari" to listOf(
                "Desa Kutamanah", "Desa Kertamanah", "Desa Ciririp", "Desa Parungbanteng", "Desa Sukasari"
            ),
            "Tegalwaru" to listOf(
                "Desa Batutumpang", "Desa Cadassari", "Desa Cadasmekar", "Desa Cisarua", "Desa Citalang","Desa Tegalwaru",
                "Desa Warungjeruk", "Desa Galumpit", "Desa Karoya", "Desa Pasanggrahan", "Desa Sukahaji", "Desa Sukamulya", "Desa Tegalsari"
            ),
            "Wanayasa" to listOf(
                "Desa Wanayasa", "Desa Sukadami", "Desa Wanasari", "Desa Simpang", "Desa Nagrog", "Desa Cibuntu", "Desa Babakan", "Desa Nangerang",
                "Desa Ciawi", "Desa Sumurugul", "Desa Raharja", "Desa Sakambang", "Desa Legokhuni", "Desa Taringgul Tonggoh", "Desa Taringgul Tengah"
            )
        )


        // Adapter Kecamatan
        val adapterKecamatan = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            kecamatanMap.keys.toList()
        )
        etKecamatan.setAdapter(adapterKecamatan)
        etKecamatan.setOnClickListener { etKecamatan.showDropDown() }
        etKecamatan.setOnItemClickListener { _, _, position, _ ->
            val kecamatan = adapterKecamatan.getItem(position)
            val desaList = kecamatanMap[kecamatan] ?: emptyList()
            val adapterDesa =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, desaList)
            etDesa.setAdapter(adapterDesa)
            etDesa.setText("", false)
            etDesa.setOnClickListener { etDesa.showDropDown() }
        }

        // Penanggulangan dengan template default
        val etPenanggulangan = findViewById<EditText>(R.id.etPenanggulangan)
        val defaultTemplate = """
        1. Menerima Laporan & mengkonfirmasi informasi.
        2. Menyiapkan RenOps (Rencana Operasi) Pemadaman Kebakaran.
        3. Menggunakan APD, menyiapkan perlengkapan dan kendaraan Truck sesuai dengan SOP pemadaman kebakaran dan kebutuhan dilokasi.
        4. Mendatangi TKK (Tempat Kejadian Kebakaran)
        5. Koordinasi dengan pihak setempat, Pejabat wilayah, TNI & Polri.
        6. Melakukan operasi Pemadaman Kebakaran : Mencari sumber api, Melokalisir Api agar tidak menyebar, Pemadaman sumber Api, pendinginan dan pengecekan lokasi. 
        7. Petugas melakukan pengecekan ulang, Dinyatakan selesai pukul (waktu) WIB
        8. Membuat Laporan lalu balik kanan MAKO Damkar Kab. Purwakarta
        """.trimIndent()
        if (etPenanggulangan.text.isNullOrBlank()) etPenanggulangan.setText(defaultTemplate)
        etPenanggulangan.addTextChangedListener { isFormChanged = true }

        // Peralatan dengan template default
        val etPeralatan = findViewById<EditText>(R.id.etPeralatan)
        val defaultPeralatan = """
        1. Nozzle
        2. Selang (Fire Hose)
        3. Kombinasi
        4. Handy Talkie
        5. APD (Alat Pelindung Diri)
        """.trimIndent()

        if (etPeralatan.text.isNullOrBlank()) etPeralatan.setText(defaultPeralatan)
        etPeralatan.addTextChangedListener { isFormChanged = true }

        // kronologi dengan template default
        val etKronologi = findViewById<EditText>(R.id.etKronologi)
        val defaultKronologi = """
        Berdasarkan Informasi dari (lanjutkan kronologinya)
        """.trimIndent()

        if (etKronologi.text.isNullOrBlank()) etKronologi.setText(defaultKronologi)
        etKronologi.addTextChangedListener { isFormChanged = true }

    }


    private fun convertToDMS(lat: Double, lon: Double): String {
        fun toDMS(value: Double, isLat: Boolean): String {
            val dir = if (isLat) {
                if (value >= 0) "N" else "S"
            } else {
                if (value >= 0) "E" else "W"
            }

            val absVal = Math.abs(value)
            val deg = absVal.toInt()
            val minFull = (absVal - deg) * 60
            val min = minFull.toInt()
            val sec = (minFull - min) * 60

            // Pakai Locale.US biar desimal titik, dan %04.1f untuk leading zero detik
            return String.Companion.format(Locale.US, "%d°%02d'%04.1f\"%s", deg, min, sec, dir)
        }

        return "${toDMS(lat, true)} ${toDMS(lon, false)}"
    }




    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }

    private fun validateForm(): Boolean {
        if (etKoordinat.text.toString().trim().isEmpty()) {
            etKoordinat.error = "Wajib diisi"
            etKoordinat.requestFocus()
            Toast.makeText(this, "Koordinat wajib diisi", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validasi korban selamat wajib isi
        val etKorbanSelamat = findViewById<EditText>(R.id.etKorbanSelamat)
        if (etKorbanSelamat.text.toString().trim().isEmpty()) {
            etKorbanSelamat.error = "Wajib diisi"
            etKorbanSelamat.requestFocus()
            Toast.makeText(this, "Jumlah korban selamat wajib diisi", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validasi setiap korban
        for (i in 0 until layoutKorbanContainer.childCount) {
            val v = layoutKorbanContainer.getChildAt(i)

            val spinnerJenis = v.findViewById<Spinner>(R.id.spinnerJenisKorban)
            val jenisKorban = spinnerJenis.selectedItem.toString()

            if (jenisKorban == "Pilih jenis korban") {
                Toast.makeText(this, "Jenis korban harus dipilih", Toast.LENGTH_SHORT).show()
                spinnerJenis.requestFocus()
                return false
            }

            val rgJk = v.findViewById<RadioGroup>(R.id.rgJenisKelamin)
            if (rgJk.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Jenis kelamin harus dipilih", Toast.LENGTH_SHORT).show()
                return false
            }

            val rgKondisi = v.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
            if (rgKondisi.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Kondisi fisik harus dipilih", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    private fun simpanLaporan() {
        val selectedKendaraan = buildString {
            append(checkBoxKendaraanList.filter { it.isChecked }.joinToString(", ") { it.text.toString() })
            if (etKendaraanLainnya.text.isNotBlank()) {
                if (isNotEmpty()) append(", ")
                append(etKendaraanLainnya.text.toString())
            }
        }

        val selectedDokumen = buildString {
            append(checkBoxDokumenList.filter { it.isChecked }.joinToString(", ") { it.text.toString() })
            if (etDokumenLainnya.text.isNotBlank()) {
                if (isNotEmpty()) append(", ")
                append(etDokumenLainnya.text.toString())
            }
        }
        val selectedRegu = checkBoxReguList.filter { it.isChecked }.joinToString(", ") { it.text.toString() }

        val laporan = LaporanKebakaran(
            hari = etHari.text.toString(),
            tanggal = etTanggal.text.toString(),
            jenisKejadian = findViewById<EditText>(R.id.etJenisKejadian).text.toString(),
            waktuLaporan = findViewById<EditText>(R.id.etWaktuLaporan).text.toString(),
            waktuSampai = findViewById<EditText>(R.id.etWaktuSampai).text.toString(),
            waktuUlang = "", // awalnya kosong, nanti dihitung
            jarak = findViewById<EditText>(R.id.etJarak).text.toString(),
            responTime = findViewById<EditText>(R.id.etResponTime).text.toString(),
            waktuKembali = findViewById<EditText>(R.id.etWaktuKembali).text.toString(),
            objek = findViewById<EditText>(R.id.etObjek).text.toString(),
            alamatDetail = etAlamatDetail.text.toString(),
            kabupaten = etKabupaten.text.toString(),
            kecamatan = etKecamatan.text.toString(),
            desa = etDesa.text.toString(),
            luas = findViewById<EditText>(R.id.etLuas).text.toString(),
            koordinat = etKoordinat.text.toString(),
            koordinatGeografi = etKoordinatGeografi.text.toString(),
            namaPelapor = findViewById<EditText>(R.id.etNamaPelapor).text.toString(),
            alamatPelapor = findViewById<EditText>(R.id.etAlamatPelapor).text.toString(),
            hpPelapor = findViewById<EditText>(R.id.etHpPelapor).text.toString(),
            namaPemilik = findViewById<EditText>(R.id.etNamaPemilik).text.toString(),
            alamatPemilik = findViewById<EditText>(R.id.etAlamatPemilik).text.toString(),
            hpPemilik = findViewById<EditText>(R.id.etHpPemilik).text.toString(),
            asetSelamat = findViewById<EditText>(R.id.etAsetSelamat).text.toString(),
            tafsiranSelamat = findViewById<EditText>(R.id.etTafsiranSelamat).text.toString(),
            asetTerbakar = findViewById<EditText>(R.id.etAsetTerbakar).text.toString(),
            dokumenTerbakar = selectedDokumen,
            tafsiranTerbakar = findViewById<EditText>(R.id.etTafsiranTerbakar).text.toString(),
            deskripsiKronologi = findViewById<EditText>(R.id.etKronologi).text.toString(),
            deskripsiPenanggulangan = findViewById<EditText>(R.id.etPenanggulangan).text.toString(),
            korbanSelamat = findViewById<EditText>(R.id.etKorbanSelamat).text.toString(),
            peralatan = findViewById<EditText>(R.id.etPeralatan).text.toString(),
            kendaraan = selectedKendaraan,
            regu = selectedRegu,
            reguLapor = reguLapor.text.toString(),
            petugas = findViewById<EditText>(R.id.etPetugas).text.toString(),
            instansi = findViewById<EditText>(R.id.etInstansi).text.toString(),
            klasifikasi = findViewById<EditText>(R.id.etKlasifikasi).text.toString()
        )

        val db = AppDatabase.Companion.getDatabase(this)
        lifecycleScope.launch {
            val laporanIdLong = db.laporanDao().insert(laporan)

            // Simpan korban
            for (i in 0 until layoutKorbanContainer.childCount) {
                val v = layoutKorbanContainer.getChildAt(i)
                val jk = v.findViewById<RadioGroup>(R.id.rgJenisKelamin)
                    .findViewById<RadioButton>(
                        v.findViewById<RadioGroup>(R.id.rgJenisKelamin).checkedRadioButtonId
                    ).text.toString()

                val kondisi = v.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
                    .findViewById<RadioButton>(
                        v.findViewById<RadioGroup>(R.id.rgKondisiFisik2).checkedRadioButtonId
                    ).text.toString()

                val korban = Korban(
                    laporanId = laporanIdLong,
                    jenisKorban = v.findViewById<Spinner>(R.id.spinnerJenisKorban).selectedItem.toString(),
                    namaKorban = v.findViewById<EditText>(R.id.etNamaKorban).text.toString(),
                    jkKorban = jk,
                    usiaKorban = v.findViewById<EditText>(R.id.etUsiaKorban).text.toString(),
                    kondisiFisikKorban = kondisi,
                    nikKorban = v.findViewById<EditText>(R.id.etNIKKorban).text.toString(),
                    kkKorban = v.findViewById<EditText>(R.id.etKKKorban).text.toString(),
                    ttlKorban = v.findViewById<EditText>(R.id.etTTL).text.toString(),
                    alamatKorban = v.findViewById<EditText>(R.id.etAlamatKorban).text.toString()
                )

                db.korbanDao().insert(korban)
            }

            // Hitung waktuUlang lalu update DB
            val savedLaporan = db.laporanDao().getById(laporanIdLong)
            if (savedLaporan != null && !savedLaporan.waktuSampai.isNullOrEmpty()) {
                try {
                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val date = format.parse(savedLaporan.waktuSampai)
                    val cal = Calendar.getInstance()
                    cal.time = date!!

                    val jam = cal.get(Calendar.HOUR_OF_DAY)

                    val waktuUlangStr = if (jam in 14..23) {
                        // Kalau jam 14:00 - 23:59 → set fix jam 08:00
                        "08:00"
                    } else {
                        // Selain itu → +8 jam dari waktu sampai
                        cal.add(Calendar.HOUR_OF_DAY, 8)
                        format.format(cal.time)
                    }

                    db.laporanDao().updateWaktuUlang(laporanIdLong, waktuUlangStr)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            runOnUiThread {
                Toast.makeText(this@AddLaporanActivity, "Laporan berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }



    private fun tambahFormKorban() {
        val inflater = layoutInflater
        val korbanView = inflater.inflate(R.layout.item_korban, null)

        // === Jenis Korban (Spinner) ===
        val spinnerJenis = korbanView.findViewById<Spinner>(R.id.spinnerJenisKorban)
        val jenisAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Pilih jenis korban") + jenisKorbanOptions
        )
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = jenisAdapter

        // Listener untuk ubah warna merah kalau belum dipilih
        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    // Masih di "Pilih jenis korban" → kasih warna merah
                    spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2"))
                } else {
                    // Sudah dipilih → balik normal
                    spinnerJenis.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Fungsi ini wajib ada, tapi boleh dikosongin
            }
        }

        // === Jenis Kelamin (RadioGroup) ===
        val rgJk = korbanView.findViewById<RadioGroup>(R.id.rgJenisKelamin)
        rgJk.clearCheck() // default tidak ada yg dipilih

        // === Kondisi Fisik (RadioGroup) ===
        val rgKondisi = korbanView.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
        rgKondisi.clearCheck() // default tidak ada yg dipilih

        // === Tombol hapus ===
        val btnHapus = korbanView.findViewById<ImageButton>(R.id.btnHapusKorban)
        btnHapus.setOnClickListener {
            layoutKorbanContainer.removeView(korbanView)
            isFormChanged = true
        }

        layoutKorbanContainer.addView(korbanView)
        isFormChanged = true
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}