package com.example.sipadam.penyelamatan.pencarianpenyelamatan

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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.AppDatabasePencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.KorbanLaporanPencarianPenyelamatan
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.LaporanPencarianPenyelamatan
import com.google.android.gms.location.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class InputPencarianPenyelamatanActivity : AppCompatActivity() {

    // DB & Location
    private lateinit var db: AppDatabasePencarianPenyelamatan
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Views (mengikuti struktur contoh)
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
    private lateinit var etKorbanSelamat: EditText
    private lateinit var etKendaraan: EditText
    private lateinit var etApd: EditText
    private lateinit var etRencana: EditText
    private lateinit var etKronologi: EditText
    private lateinit var etUpaya: EditText
    private lateinit var etPetugas: EditText
    private lateinit var etInstansi: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnTambahKoordinat: Button
    private lateinit var btnTambahKorban: Button

    // containers dynamic
    private lateinit var layoutKendaraan: LinearLayout
    private lateinit var layoutPetugas: LinearLayout
    private lateinit var layoutKorbanContainer: LinearLayout

    private lateinit var calendar: Calendar
    private var isFormChanged = false

    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var dayOfWeekFormat: SimpleDateFormat

    // --- Saksi ---
    private lateinit var layoutSaksiContainer: LinearLayout
    private lateinit var btnTambahSaksi: Button


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

        // Kendaraan (ambil dari AddLaporanActivity)
        val kendaraanArray = listOf(
            "KR 01 (T 9909 A)","KR 02 (T 9910 A)","KR 03 (T 9925 B)","KR 04 (T 9901 B)",
            "KR 05 (T 8115 A)","KR 06 (T 9942 J)","KR 07 (T 9941 J)","Ambulance (T 9914 B)",
            "Toyota Hilux (T 8350 A)","Toyota Double Cabin (T 8454 A)","Honda Trail CRF (T 3484 B)",
            "Honda Trail CRF (T 2031 C)","Honda Trail CRF (T 3482 B)","Honda Trail CRF (T 5584 B)",
            "Honda Trail CRF (T 4153 B)"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_pencarian_penyelamatan)

        db = AppDatabasePencarianPenyelamatan.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // LocationRequest & callback
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
                        this@InputPencarianPenyelamatanActivity,
                        "Lokasi tidak tersedia",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        // Inisialisasi view (samakan id dengan layout)
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
        etKorbanSelamat = findViewById(R.id.etKorbanSelamat)
        etKendaraan = findViewById(R.id.etKendaraanLainnya)
        etApd = findViewById(R.id.etApd)
        etRencana = findViewById(R.id.etRencana)
        etKronologi = findViewById(R.id.etKronologi)
        etUpaya = findViewById(R.id.etUpaya)
        etPetugas = findViewById(R.id.etPetugasLainnya)
        etInstansi = findViewById(R.id.etInstansi)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnTambahKoordinat = findViewById(R.id.btnTambahKoordinat)
        btnTambahKorban = findViewById(R.id.btnTambahKorban)

        layoutKendaraan = findViewById(R.id.layoutKendaraan)
        layoutPetugas = findViewById(R.id.layoutPetugas)
        layoutKorbanContainer = findViewById(R.id.layoutKorbanContainer)

        layoutSaksiContainer = findViewById(R.id.layoutSaksiContainer)
        btnTambahSaksi = findViewById(R.id.btnTambahSaksi)

        btnTambahSaksi.setOnClickListener {
            tambahFormSaksi()
        }


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

        // AutoComplete Kabupaten/Kecamatan/Desa
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
        val kegiatanList = listOf("Evakuasi Korban", "Pelepasan Cincin Yang Menyebabkan Infeksi")
        etJenisKegiatan.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                kegiatanList
            )
        )
        etJenisKegiatan.setOnClickListener { etJenisKegiatan.showDropDown(); }

        val reguArray = arrayOf(
            "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
        )
        etRegu.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reguArray
            )
        )
        etRegu.setOnClickListener { etRegu.showDropDown(); }

        // APD default — editable
        val defaultApd = """
            A. Penyelamatan di Air
            1. Water Rescue Equipment
            2. Perahu
            3. Helmet
            4. Pelampung
            5. Dayung
            6. Pompa perahu
            7. Kantung mayat
            
            B. Penyelamatan di Ketinggian/Vertikal
            1.Helmet
            2.Body Harness
            3.Sarung Tangan
            4.Kantung mayat
            
            C. Penyelamatan di Hutan dan Gunung
            1.helmet
            2.Sarung Tangan
            3.Kantung mayat
            
            D. Penyelamatan di Ruang Terbatas 
            1.helmet
            2.Sarung Tangan
            3.Alat Bantu Pernapasan
            4.Senter
            5.Kantung mayat
            
            E. Penyelamatan di Bangunan Runtuh
            1.helmet
            2.Sarung Tangan
            3.Alat Bantu Pernapasan
            4.Senter
            5.Pelindung Lutut dan Siku
            6.Kacamata
            7.Kantung mayat
            
            F.Pertolongan Pelepasan Cincin
            1. Gerinda mini
            2. Tang potong 
            3. Senter
            4. Latex
            5. Masker
            6. Helment

        """.trimIndent()
        if (etApd.text.isNullOrBlank()) etApd.setText(defaultApd)
        val defaultKronologi = """
        Berdasarkan Informasi dari (lanjutkan kronologinya)
        """.trimIndent()
        if (etKronologi.text.isNullOrBlank()) etKronologi.setText(defaultKronologi)

        // Upaya default — editable
        val defaultUpaya = """
        1. Mendatangi TKP
        2. Menentukan rencana dan teknik evakuasi
        3. Melakukan penelusuran Area sekitar kejadian bersama masyarakat
        4. Menyiapkan sarana prasarana termasuk APD sesuai SOP
        5. Korban Berhasil Di evakuasi 
        6. Anggota melaksanakan Evaluasi dan Apel Penutupan pencarian
        7. Anggota kembali ke mako pusat dan membuat laporan
        
        1. Menerima Laporan
        2. Mempersiapkan sarpras penunjang penyelamatan dan evakuasi
        3. Melakukan pemotongan cincin perak dengan menggunakan gerinda mini, dinyatakan selesai pada pukul (MASUKAN WAKTU) WIB.
        4. Membuat laporan


        """.trimIndent()
        if (etUpaya.text.isNullOrBlank()) etUpaya.setText(defaultUpaya)

        // Generate checkbox kendaraan
        kendaraanArray.forEach { nama ->
            val checkBox = CheckBox(this).apply { text = nama }
            layoutKendaraan.addView(checkBox)
        }

        // Generate checkbox petugas (contoh daftar)
        val petugasList = listOf(
            "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
        )
        petugasList.forEach { nama ->
            val checkBox = CheckBox(this).apply { text = nama }
            layoutPetugas.addView(checkBox)
        }

        // Tambah korban
        btnTambahKorban.setOnClickListener { tambahFormKorban() }

        // Tombol koordinat
        btnTambahKoordinat.setOnClickListener {
            etKoordinatDesimal.text.clear()
            etKoordinatGeografi.text.clear()
            getLocation()
        }

        // Simpan (validasi, simpan DB)
        btnSimpan.setOnClickListener {
            if (validateForm()) simpanLaporan()
        }
        setupFormChangeListeners()
    }

    private fun tambahFormKorban() {
        val inflater = layoutInflater
        val korbanView = inflater.inflate(R.layout.item_korban_pencarian, null)

        // Spinner jenis korban
        val spinnerJenis = korbanView.findViewById<Spinner>(R.id.spinnerJenisKorban)
        val jenisKorbanOptions = listOf("Meninggal Dunia", "Luka Ringan", "Luka Berat","Dalam Pencarian")
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
            korbanView.findViewById(R.id.etAlamatKorban),
            korbanView.findViewById(R.id.etCiriciri)
        )

        val etCiriciri = korbanView.findViewById<EditText>(R.id.etCiriciri)
        val defaultCiriCiri = """
    1. Tinggi badan : 
    2. Warna kulit : 
    3. Warna Rambut : 
    4. Jenis Kelamin :
    5. Bentuk Wajah :
    6. Pakaian : 
    7. Ciri khusus (tato, bekas luka, dll) : 
""".trimIndent()

        if (etCiriciri.text.isNullOrBlank()) {
            etCiriciri.setText(defaultCiriCiri)
        }

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

    private fun tambahFormSaksi() {
        val view = layoutInflater.inflate(R.layout.item_saksi_pencarian, layoutSaksiContainer, false)

        // Tombol hapus
        val btnHapus = view.findViewById<ImageButton>(R.id.btnHapusSaksi)
        btnHapus.setOnClickListener {
            layoutSaksiContainer.removeView(view)
            isFormChanged = true
        }

        layoutSaksiContainer.addView(view)
        isFormChanged = true
    }


    private fun setupFormChangeListeners() {
        val allInputs = listOf<View>(
            etJenisKegiatan, etRegu, etHariDiterima, etTanggalDiterima, etWaktuDiterima,
            etHariTindak, etTanggalTindak, etWaktuTindak, etAlamat, etKabupaten, etKecamatan,
            etDesa, etJarak, etKoordinatDesimal, etKoordinatGeografi, etNamaPelapor,etRencana,
            etUsiaPelapor, etHpPelapor, etAlamatPelapor, etKorbanSelamat, etKendaraan, etApd, etUpaya, etPetugas, etKronologi, etInstansi
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

    // showDatePicker & showTimePicker
    private fun showDatePicker(targetDate: EditText, targetDay: EditText) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val chosen = Calendar.getInstance()
            chosen.set(y, m, d)
            targetDate.setText(dateFormat.format(chosen.time))
            targetDay.setText(dayOfWeekFormat.format(chosen.time))
            isFormChanged = true
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
            isFormChanged = true
        }
        picker.show(supportFragmentManager, "TIME_PICKER")
    }

    // getLocation & permission handling
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
            val laporan = LaporanPencarianPenyelamatan(
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
                korbanSelamat = etKorbanSelamat.text.toString(),
                kendaraan = kendaraanDipilih,
                apd = etApd.text.toString(),
                kronologi = etKronologi.text.toString(),
                upaya = etUpaya.text.toString(),
                petugas = petugasDipilih,
                instansi = etInstansi.text.toString(),
                rencana = etRencana.text.toString()
            )

            // simpan laporan dan ambil ID baru
            val newId = withContext(Dispatchers.IO) {
                db.laporanDao().insert(laporan)
            }

            val korbanList = mutableListOf<KorbanLaporanPencarianPenyelamatan>()
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
                val ciri = v.findViewById<EditText>(R.id.etCiriciri).text.toString()

                korbanList.add(
                    KorbanLaporanPencarianPenyelamatan(
                        laporanId = newId,
                        jenisKorban = jenis,
                        namaKorban = nama,
                        jkKorban = jk,
                        usiaKorban = usia,
                        kondisiFisikKorban = kondisi,
                        nikKorban = nik,
                        kkKorban = kk,
                        ttlKorban = ttl,
                        alamatKorban = alamat,
                        ciriciri = ciri
                    )
                )
            }
            // --- Simpan daftar saksi ---
            withContext(Dispatchers.IO) {
                for (i in 0 until layoutSaksiContainer.childCount) {
                    val v = layoutSaksiContainer.getChildAt(i)

                    val nama = v.findViewById<EditText>(R.id.etNamaSaksi).text.toString()
                    val umur = v.findViewById<EditText>(R.id.etUmurSaksi).text.toString()
                    val noHp = v.findViewById<EditText>(R.id.etNoHpSaksi).text.toString()
                    val alamat = v.findViewById<EditText>(R.id.etAlamatSaksi).text.toString()

                    val rgJk = v.findViewById<RadioGroup>(R.id.rgJkSaksi)
                    val jk = when (rgJk.checkedRadioButtonId) {
                        R.id.rbSaksiL -> "Laki-laki"
                        R.id.rbSaksiP -> "Perempuan"
                        else -> null
                    }

                    if (nama.isNotBlank()) {
                        val saksi = com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.SaksiLaporanPencarianPenyelamatan(
                            laporanId = newId,
                            namaSaksi = nama,
                            jkSaksi = jk,
                            umurSaksi = umur,
                            noHpSaksi = noHp,
                            alamatSaksi = alamat
                        )
                        db.saksiDao().insert(saksi)
                    }
                }
            }


            // simpan korban juga di IO thread
            withContext(Dispatchers.IO) {
                if (korbanList.isNotEmpty()) db.korbanDao().insertAll(korbanList)
            }

            runOnUiThread {
                Toast.makeText(
                    this@InputPencarianPenyelamatanActivity,
                    "Laporan tersimpan",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
