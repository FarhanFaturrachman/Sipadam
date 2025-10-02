package com.example.sipadam.penyelamatan.penangananbinatang.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.penangananbinatang.data.AppDatabasePenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.KorbanLaporanPenangananBinatang
import com.example.sipadam.penyelamatan.penangananbinatang.data.model.LaporanPenangananBinatang
import com.google.android.gms.location.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.withContext


class InputPenangananBinatangActivity : AppCompatActivity() {

    private lateinit var db: AppDatabasePenangananBinatang
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Views
    private lateinit var etJenisKegiatan: AutoCompleteTextView
    private lateinit var etRegu: AutoCompleteTextView
    private lateinit var etHariDiterima: EditText
    private lateinit var etTanggalDiterima: EditText
    private lateinit var etWaktuDiterima: EditText
    private lateinit var etHariTindak: EditText
    private lateinit var etTanggalTindak: EditText
    private lateinit var etWaktuTindak: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etKabupaten: AutoCompleteTextView
    private lateinit var etKecamatan: AutoCompleteTextView
    private lateinit var etDesa: AutoCompleteTextView
    private lateinit var etJarak: EditText
    private lateinit var etKoordinatDesimal: EditText
    private lateinit var etKoordinatGeografi: EditText
    private lateinit var etNamaPelapor: EditText
    private lateinit var etUsiaPelapor: EditText
    private lateinit var etHpPelapor: EditText
    private lateinit var etAlamatPelapor: EditText
    private lateinit var etNamaPemilik: EditText
    private lateinit var etUsiaPemilik: EditText
    private lateinit var etHpPemilik: EditText
    private lateinit var etAlamatPemilik: EditText
    private lateinit var etKorbanSelamat: EditText
    private lateinit var etKendaraan: EditText
    private lateinit var etRencana: EditText
    private lateinit var etApd: EditText
    private lateinit var etUpaya: EditText
    private lateinit var etPetugas: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnTambahKoordinat: Button
    private lateinit var btnTambahKorban: Button

    // containers dynamic
    private lateinit var layoutKendaraan: LinearLayout
    private lateinit var layoutPetugas: LinearLayout
    private lateinit var layoutKorbanContainer: LinearLayout

    private lateinit var calendar: Calendar
    // Tambahan untuk tracking perubahan form
    private var isFormChanged = false

    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var dayOfWeekFormat: SimpleDateFormat


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        val kabupatenList = listOf("Purwakarta")
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
    }

    // Kendaraan (ambil dari AddLaporanActivity)
    private val kendaraanArray = listOf(
        "KR 01 (T 9909 A)", "KR 02 (T 9910 A)", "KR 03 (T 9925 B)", "KR 04 (T 9901 B)",
        "KR 05 (T 8115 A)", "KR 06 (T 9942 J)", "KR 07 (T 9941 J)", "Ambulance (T 9914 B)",
        "Toyota Hilux (T 8350 A)", "Toyota Double Cabin (T 8454 A)", "Honda Trail CRF (T 3484 B)",
        "Honda Trail CRF (T 2031 C)", "Honda Trail CRF (T 3482 B)", "Honda Trail CRF (T 5584 B)",
        "Honda Trail CRF (T 4153 B)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_penanganan_binatang)

        db = AppDatabasePenangananBinatang.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // LocationRequest & callback (sama pola Add)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).setMaxUpdates(1).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    etKoordinatDesimal.setText("$lat, $lon")
                    etKoordinatGeografi.setText(convertToDMS(lat, lon))
                } else {
                    Toast.makeText(
                        this@InputPenangananBinatangActivity,
                        "Lokasi tidak tersedia",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        // Inisialisasi view
        etJenisKegiatan = findViewById(R.id.etJenisKegiatan)
        etRegu = findViewById(R.id.etRegu)
        etHariDiterima = findViewById(R.id.etHariDiterima)
        etTanggalDiterima = findViewById(R.id.etTanggalDiterima)
        etWaktuDiterima = findViewById(R.id.etWaktuDiterima)
        etHariTindak = findViewById(R.id.etHariTindak)
        etTanggalTindak = findViewById(R.id.etTanggalTindak)
        etWaktuTindak = findViewById(R.id.etWaktuTindak)
        etAlamat = findViewById(R.id.etAlamat)
        etKabupaten = findViewById(R.id.etKabupaten)
        etKecamatan = findViewById(R.id.etKecamatan)
        etDesa = findViewById(R.id.etDesa)
        etJarak = findViewById(R.id.etJarak)
        etKoordinatDesimal = findViewById(R.id.etKoordinatDesimal)
        etKoordinatGeografi = findViewById(R.id.etKoordinatGeografi)
        etNamaPelapor = findViewById(R.id.etNamaPelapor)
        etUsiaPelapor = findViewById(R.id.etUsiaPelapor)
        etHpPelapor = findViewById(R.id.etHpPelapor)
        etAlamatPelapor = findViewById(R.id.etAlamatPelapor)
        etNamaPemilik = findViewById(R.id.etNamaPemilik)
        etUsiaPemilik = findViewById(R.id.etUsiaPemilik)
        etHpPemilik = findViewById(R.id.etHpPemilik)
        etAlamatPemilik = findViewById(R.id.etAlamatPemilik)
        etKorbanSelamat = findViewById(R.id.etKorbanSelamat)
        etKendaraan = findViewById(R.id.etKendaraanLainnya)
        etRencana = findViewById(R.id.etRencana)
        etApd = findViewById(R.id.etApd)
        etUpaya = findViewById(R.id.etUpaya)
        etPetugas = findViewById(R.id.etPetugasLainnya)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnTambahKoordinat = findViewById(R.id.btnTambahKoordinat)
        btnTambahKorban = findViewById(R.id.btnTambahKorban)

        layoutKendaraan = findViewById(R.id.layoutKendaraan)
        layoutPetugas = findViewById(R.id.layoutPetugas)
        layoutKorbanContainer = findViewById(R.id.layoutKorbanContainer)

        // Format waktu & tanggal default
        calendar = Calendar.getInstance()
        dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

        etHariDiterima.setText(dayOfWeekFormat.format(calendar.time))
        etTanggalDiterima.setText(dateFormat.format(calendar.time))
        etWaktuDiterima.setText(timeFormat.format(calendar.time))
        etHariTindak.setText(dayOfWeekFormat.format(calendar.time))
        etTanggalTindak.setText(dateFormat.format(calendar.time))
        etWaktuTindak.setText(timeFormat.format(calendar.time))

        // DatePicker & TimePicker
        etTanggalDiterima.setOnClickListener { showDatePicker(etTanggalDiterima, etHariDiterima) }
        etTanggalTindak.setOnClickListener { showDatePicker(etTanggalTindak, etHariTindak) }
        etWaktuDiterima.setOnClickListener { showTimePicker(etWaktuDiterima) }
        etWaktuTindak.setOnClickListener { showTimePicker(etWaktuTindak) }

        // AutoComplete Kabupaten/Kecamatan/Desa (sama pola)
        val adapterKabupaten =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kabupatenList)
        etKabupaten.setAdapter(adapterKabupaten)
        etKabupaten.setText("Purwakarta", false)
        etKabupaten.setOnClickListener { etKabupaten.showDropDown() }

        val adapterKecamatan = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            kecamatanMap.keys.toList()
        )
        etKecamatan.setAdapter(adapterKecamatan)
        etKecamatan.setOnClickListener { etKecamatan.showDropDown() }
        etKecamatan.setOnItemClickListener { _, _, position, _ ->
            val kec = adapterKecamatan.getItem(position)
            val desaList = kecamatanMap[kec] ?: emptyList()
            val adapterDesa =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, desaList)
            etDesa.setAdapter(adapterDesa)
            etDesa.setText("", false)
            etDesa.setOnClickListener { etDesa.showDropDown() }
        }

        // Jenis kegiatan & regu (autocomplete)
        val kegiatanList = listOf("Pemusnahan Sarang Tawon/Lebah","Penagkapan dan Evakuasi Ular","Penagkapan dan Evakuasi Biawak","Penagkapan dan Evakuasi Kera","Penagkapan dan Evakuasi Toke","Evakuasi Kucing")
        etJenisKegiatan.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                kegiatanList
            )
        )
        etJenisKegiatan.setOnClickListener { etJenisKegiatan.showDropDown(); }

        val reguArray = arrayOf(
            "Regu 1 Pusat", "Regu 2 Pusat", "Regu 3 Pusat",
            "Regu 1 UPTD WIL.1", "Regu 2 UPTD WIL.1", "Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2", "Regu 2 UPTD WIL.2", "Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3", "Regu 2 UPTD WIL.3", "Regu 3 UPTD WIL.3"
        )
        etRegu.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reguArray
            )
        )
        etRegu.setOnClickListener { etRegu.showDropDown(); }

        // APD default (sesuai permintaan Anda) — editable
        val defaultApd = """
            1. Helmet
            2. Sarung tangan
            3. Hook
            4. Tangga
            5. Jaring
        """.trimIndent()
        if (etApd.text.isNullOrBlank()) etApd.setText(defaultApd)

        // Upaya default (sesuai permintaan Anda) — editable
        val defaultUpaya = """
            1. Meminta foto situasi lokasi dan foto
            2. Menentukan rencana dan teknik penanganan
            3. Menyiapkan sarana prasarana termasuk APD sesuai SOP
            4. Menuju lokasi dan koordinasi dengan pemilik lokasi dan aparat setempat
            5. Melaksanakan penangkapan dengan cara ditangkap menggunakan hook, dinyatakan selesai pada pukul (Masukan waktu) WIB
            6. Menandatangani berita acara
            7. Meminta pelapor agar mengisi format SKM
            8. Kembali ke mako dan membuat laporan
        """.trimIndent()
        if (etUpaya.text.isNullOrBlank()) etUpaya.setText(defaultUpaya)

        // Generate checkbox kendaraan (pakai daftar dari AddLaporan)
        kendaraanArray.forEach { nama ->
            val checkBox = CheckBox(this).apply { text = nama }
            layoutKendaraan.addView(checkBox)
        }

        // Generate checkbox petugas (contoh daftar, bisa ditambah)
        val petugasList = listOf(
            "Regu 1 Pusat", "Regu 2 Pusat", "Regu 3 Pusat",
            "Regu 1 UPTD WIL.1", "Regu 2 UPTD WIL.1", "Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2", "Regu 2 UPTD WIL.2", "Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3", "Regu 2 UPTD WIL.3", "Regu 3 UPTD WIL.3"
        )
        petugasList.forEach { nama ->
            val checkBox = CheckBox(this).apply { text = nama }
            layoutPetugas.addView(checkBox)
        }

        // Tambah korban: gunakan pola AddLaporan.tambahFormKorban
        btnTambahKorban.setOnClickListener { tambahFormKorban() }

        // Tombol koordinat
        btnTambahKoordinat.setOnClickListener {
            etKoordinatDesimal.text.clear()
            etKoordinatGeografi.text.clear()
            getLocation()
        }

        // Simpan (implementasi simpan DB sesuai model Anda)
        btnSimpan.setOnClickListener {
            if (validateForm()) simpanLaporan()
        }
        setupFormChangeListeners()
    }


    // Fungsi menambahkan form korban dinamis (mengikuti pola AddLaporanActivity)
    private fun tambahFormKorban() {
        val inflater = layoutInflater
        val korbanView = inflater.inflate(R.layout.item_korban, null)

        // === Spinner jenis korban ===
        val spinnerJenis = korbanView.findViewById<Spinner>(R.id.spinnerJenisKorban)
        val jenisKorbanOptions = listOf("Meninggal Dunia", "Luka Ringan", "Luka Berat")
        val jenisAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Pilih jenis korban") + jenisKorbanOptions
        )
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = jenisAdapter

        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2"))
                } else {
                    spinnerJenis.setBackgroundColor(Color.TRANSPARENT)
                }
                isFormChanged = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // RadioGroup gender & kondisi
        val rgJk = korbanView.findViewById<RadioGroup>(R.id.rgJenisKelamin)
        rgJk.clearCheck()
        rgJk.setOnCheckedChangeListener { _, _ -> isFormChanged = true }

        val rgKondisi = korbanView.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
        rgKondisi.clearCheck()
        rgKondisi.setOnCheckedChangeListener { _, _ -> isFormChanged = true }

        // Input teks → listener untuk tandai perubahan
        val inputs = listOf<EditText>(
            korbanView.findViewById(R.id.etNamaKorban),
            korbanView.findViewById(R.id.etUsiaKorban),
            korbanView.findViewById(R.id.etNIKKorban),
            korbanView.findViewById(R.id.etKKKorban),
            korbanView.findViewById(R.id.etTTL),
            korbanView.findViewById(R.id.etAlamatKorban)
        )
        inputs.forEach { input ->
            input.addTextChangedListener { isFormChanged = true }
        }

        // Tombol hapus
        val btnHapus = korbanView.findViewById<ImageButton>(R.id.btnHapusKorban)
        btnHapus.setOnClickListener {
            layoutKorbanContainer.removeView(korbanView)
            isFormChanged = true
        }

        layoutKorbanContainer.addView(korbanView)
        isFormChanged = true
    }


    private fun setupFormChangeListeners() {
        val allInputs = listOf<View>(
            etJenisKegiatan, etRegu, etHariDiterima, etTanggalDiterima, etWaktuDiterima,
            etHariTindak, etTanggalTindak, etWaktuTindak, etAlamat, etKabupaten, etKecamatan,
            etDesa, etJarak, etKoordinatDesimal, etKoordinatGeografi, etNamaPelapor,
            etUsiaPelapor, etHpPelapor, etAlamatPelapor, etNamaPemilik, etUsiaPemilik,
            etHpPemilik, etAlamatPemilik, etKorbanSelamat, etKendaraan, etApd, etUpaya, etPetugas, etRencana
        )

        allInputs.forEach { view ->
            if (view is EditText) {
                view.addTextChangedListener { isFormChanged = true }
            } else if (view is AutoCompleteTextView) {
                view.setOnItemClickListener { _, _, _, _ -> isFormChanged = true }
            }
        }
    }

    // Validasi sebelum simpan
    private fun validateForm(): Boolean {
        if (etKoordinatDesimal.text.isNullOrBlank()) {
            etKoordinatDesimal.error = "Koordinat wajib diisi"
            etKoordinatDesimal.requestFocus()
            return false
        }

        if (etKorbanSelamat.text.isNullOrBlank()) {
            etKorbanSelamat.error = "Korban selamat wajib diisi"
            etKorbanSelamat.requestFocus()
            return false
        }

        for (i in 0 until layoutKorbanContainer.childCount) {
            val v = layoutKorbanContainer.getChildAt(i)

            val spinnerJenis = v.findViewById<Spinner>(R.id.spinnerJenisKorban)
            if (spinnerJenis.selectedItemPosition == 0) {
                Toast.makeText(this, "Jenis korban harus dipilih", Toast.LENGTH_SHORT).show()
                return false
            }

            val rgJk = v.findViewById<RadioGroup>(R.id.rgJenisKelamin)
            if (rgJk.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Jenis kelamin korban harus dipilih", Toast.LENGTH_SHORT).show()
                return false
            }

            val rgKondisi = v.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
            if (rgKondisi.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Kondisi fisik korban harus dipilih", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }
    // showDatePicker & showTimePicker (sama pola)
    private fun showDatePicker(targetDate: EditText, targetDay: EditText) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val chosen = Calendar.getInstance()
            chosen.set(y, m, d)
            targetDate.setText(dateFormat.format(chosen.time))
            targetDay.setText(dayOfWeekFormat.format(chosen.time))
        }, year, month, day)
        datePicker.show()
    }

    private fun showTimePicker(target: EditText) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
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

    // getLocation & permission handling (pakai pola AddLaporanActivity)
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            Toast.makeText(
                this,
                "Izin lokasi diperlukan untuk mendapatkan koordinat.",
                Toast.LENGTH_SHORT
            ).show()
        }
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
            return String.format(Locale.US, "%d°%02d'%04.1f\"%s", deg, min, sec, dir)
        }

        return "${toDMS(lat, true)} ${toDMS(lon, false)}"
    }

    // helper ambil teks dari checkbox dalam container
    private fun getCheckedTextsFromLayout(container: LinearLayout): String {
        val selected = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is CheckBox && view.isChecked) {
                selected.add(view.text.toString())
            }
        }
        return selected.joinToString(", ")
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

    private fun simpanLaporan() {
        if (etKoordinatDesimal.text.toString().trim().isEmpty()) {
            etKoordinatDesimal.error = "Koordinat wajib diisi"
            etKoordinatDesimal.requestFocus()
            Toast.makeText(this, "Koordinat wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val kendaraanDipilih = buildString {
            append(getCheckedTextsFromLayout(layoutKendaraan))
            if (etKendaraan.text.isNotBlank()) {
                if (isNotEmpty()) append(", ")
                append(etKendaraan.text.toString())
            }
        }

        val petugasDipilih = buildString {
            append(getCheckedTextsFromLayout(layoutPetugas))
            if (etPetugas.text.isNotBlank()) {
                if (isNotEmpty()) append(", ")
                append(etPetugas.text.toString())
            }
        }

        lifecycleScope.launch {
            val laporan = LaporanPenangananBinatang(
                jenisKegiatan = etJenisKegiatan.text.toString(),
                regu = etRegu.text.toString(),
                hariDiterima = etHariDiterima.text.toString(),
                tanggalDiterima = etTanggalDiterima.text.toString(),
                waktuDiterima = etWaktuDiterima.text.toString(),
                hariTindak = etHariTindak.text.toString(),
                tanggalTindak = etTanggalTindak.text.toString(),
                waktuTindak = etWaktuTindak.text.toString(),
                alamat = etAlamat.text.toString(),
                kabupaten = etKabupaten.text.toString(),
                kecamatan = etKecamatan.text.toString(),
                desa = etDesa.text.toString(),
                jarak = etJarak.text.toString(),
                koordinatDesimal = etKoordinatDesimal.text.toString(),
                koordinatGeografi = etKoordinatGeografi.text.toString(),
                namaPelapor = etNamaPelapor.text.toString(),
                usiaPelapor = etUsiaPelapor.text.toString(),
                noHpPelapor = etHpPelapor.text.toString(),
                alamatPelapor = etAlamatPelapor.text.toString(),
                namaPemilik = etNamaPemilik.text.toString(),
                usiaPemilik = etUsiaPemilik.text.toString(),
                noHpPemilik = etHpPemilik.text.toString(),
                alamatPemilik = etAlamatPemilik.text.toString(),
                korbanSelamat = etKorbanSelamat.text.toString(),
                kendaraan = kendaraanDipilih,
                apd = etApd.text.toString(),
                upaya = etUpaya.text.toString(),
                petugas = petugasDipilih,
                rencana = etRencana.text.toString()
            )

            // ✅ simpan laporan dan ambil ID baru
            val newId = withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.laporanDao().insert(laporan)
            }

            val korbanList = mutableListOf<KorbanLaporanPenangananBinatang>()
            for (i in 0 until layoutKorbanContainer.childCount) {
                val v = layoutKorbanContainer.getChildAt(i)
                val jenis = v.findViewById<Spinner>(R.id.spinnerJenisKorban).selectedItem.toString()
                val nama = v.findViewById<EditText>(R.id.etNamaKorban).text.toString()
                val jk = v.findViewById<RadioGroup>(R.id.rgJenisKelamin)
                    .let { if (it.checkedRadioButtonId != -1) it.findViewById<RadioButton>(it.checkedRadioButtonId).text.toString() else "" }
                val usia = v.findViewById<EditText>(R.id.etUsiaKorban).text.toString()
                val kondisi = v.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
                    .let { if (it.checkedRadioButtonId != -1) it.findViewById<RadioButton>(it.checkedRadioButtonId).text.toString() else "" }
                val nik = v.findViewById<EditText>(R.id.etNIKKorban).text.toString()
                val kk = v.findViewById<EditText>(R.id.etKKKorban).text.toString()
                val ttl = v.findViewById<EditText>(R.id.etTTL).text.toString()
                val alamat = v.findViewById<EditText>(R.id.etAlamatKorban).text.toString()

                korbanList.add(
                    KorbanLaporanPenangananBinatang(
                        laporanId = newId,
                        jenisKorban = jenis,
                        namaKorban = nama,
                        jkKorban = jk,
                        usiaKorban = usia,
                        kondisiFisikKorban = kondisi,
                        nikKorban = nik,
                        kkKorban = kk,
                        ttlKorban = ttl,
                        alamatKorban = alamat
                    )
                )
            }

            // simpan korban juga di IO thread
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.korbanDao().insertAll(korbanList)
            }

            runOnUiThread {
                Toast.makeText(
                    this@InputPenangananBinatangActivity,
                    "Laporan tersimpan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
