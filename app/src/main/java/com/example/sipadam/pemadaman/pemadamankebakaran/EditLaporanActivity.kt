package com.example.sipadam.pemadaman.pemadamankebakaran

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.get

class EditLaporanActivity : AppCompatActivity() {
    private lateinit var reguLapor: AutoCompleteTextView
    private var laporanId: Long = -1L
    private lateinit var laporan: LaporanKebakaran
    private lateinit var layoutKorbanContainer: LinearLayout
    private val jenisKorbanOptions = arrayOf("Meninggal Dunia", "Luka Bakar", "Luka Fisik Lainnya")

    // Hari & Tanggal
    private lateinit var etHari: AutoCompleteTextView
    private lateinit var etTanggal: EditText
    private lateinit var hariList: List<String>
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var calendar: Calendar

    // Lokasi
    private lateinit var etAlamatDetail: EditText
    private lateinit var etKabupaten: AutoCompleteTextView
    private lateinit var etKecamatan: AutoCompleteTextView
    private lateinit var etDesa: AutoCompleteTextView

    // Koordinat
    private lateinit var etKoordinat: EditText
    private lateinit var btnTambahKoordinat: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Kendaraan
    private lateinit var layoutKendaraan: LinearLayout
    private val kendaraanList = listOf(
        "KR 01 (T 9909 A)", "KR 02 (T 9910 A)", "KR 03 (T 9925 B)",
        "KR 04 (T 9901 B)", "KR 05 (T 8115 A)", "KR 06 (T 9942 J)",
        "KR 07 (T 9941 J)", "Ambulance (T 9914 B)", "Toyota Hilux (T 8350 A)",
        "Toyota Double Cabin (T 8454 A)", "Honda Trail CRF (T 3484 B)",
        "Honda Trail CRF (T 2031 C)", "Honda Trail CRF (T 3482 B)",
        "Honda Trail CRF (T 5584 B)", "Honda Trail CRF (T 4153 B)"
    )
    private val checkBoxKendaraan = mutableListOf<CheckBox>()
    private val dokumenList = listOf("KTP", "KK", "Ijazah", "BPKB", "STNK")
    private val checkBoxDokumen = mutableListOf<CheckBox>()
    private lateinit var layoutDokumen: LinearLayout
    private lateinit var etDokumenLainnya: EditText
    private lateinit var etKendaraanLainnya: EditText


    // Regu
    private lateinit var layoutRegu: LinearLayout
    private val reguList = listOf(
        "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
        "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
        "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
        "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
    )
    private val checkBoxRegu = mutableListOf<CheckBox>()

    // Flag deteksi perubahan data
    private var isDataChanged = false

    private lateinit var etKoordinatGeografi: EditText

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_laporan)

        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        supportActionBar?.title = "Edit Laporan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutKorbanContainer = findViewById(R.id.layoutKorbanContainer)
        layoutKendaraan = findViewById(R.id.layoutKendaraan)
        layoutRegu = findViewById(R.id.layoutRegu)
        layoutDokumen = findViewById(R.id.layoutDokumen)
        etDokumenLainnya = findViewById(R.id.etDokumenLainnya)
        etKendaraanLainnya = findViewById(R.id.etKendaraanLainnya)


        // Hari & Tanggal
        etHari = findViewById(R.id.etHari)
        etTanggal = findViewById(R.id.etTanggal)
        hariList = listOf("Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu")
        val adapterHari = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, hariList)
        etHari.setAdapter(adapterHari)
        etHari.setOnClickListener { etHari.showDropDown() }
        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
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

        val reguLaporList = listOf(
            "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
        )

        val reguLaporAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reguLaporList)
        reguLapor.setAdapter(reguLaporAdapter)
        reguLapor.setOnClickListener { reguLapor.showDropDown() }

// === Tambahkan ini di sini ===
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
        // Lokasi
        etAlamatDetail = findViewById(R.id.etAlamatDetail)
        etKabupaten = findViewById(R.id.etKabupaten)
        etKecamatan = findViewById(R.id.etKecamatan)
        etDesa = findViewById(R.id.etDesa)
        val kabupatenList = listOf("Purwakarta")
        val adapterKabupaten =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kabupatenList)
        etKabupaten.setAdapter(adapterKabupaten)
        etKabupaten.setText("Purwakarta", false)
        etKabupaten.setOnClickListener { etKabupaten.showDropDown() }

        val kecamatanMap = mapOf(
            "Babakancikao" to listOf(
                "Babakancikao", "Cicadas", "Cilangkap", "Cigelam", "Ciwareng", "Hegarmanah", "Kadumekar", "Maracang", "Mulyamekar"
            ),
            "Bojong" to listOf(
                "Bojong Barat", "Bojong Timur", "Cibingbin", "Cihanjawar", "Cikeris", "Cileunca", "Cipeundeuy",
                "Kertasari", "Pangkalan", "Pasanggrahan", "Pawenang", "Sindangpanon", "Sindangsari", "Sukamanah"
            ),
            "Bungursari" to listOf(
                "Bungursari", "Cibening", "Cibodas", "Cibungur", "Cikopo", "Cinangka", "Ciwangi", "Dangdeur",
                "Karangmukti", "Wanakerta"
            ),
            "Campaka" to listOf(
                "Benteng", "Campaka", "Campakasari", "Cijaya", "Cijunti", "Cimahi", "Cisaat", "Cikumpay",
                "Cirende", "Kertamukti"
            ),
            "Cibatu" to listOf(
                "Cibatu", "Cibukamanah", "Cikadu", "Cilandak", "Cipancur", "Cipinang", "Cirangkong",
                "Ciparungsari", "Karyamekar", "Wanawali"
            ),
            "Darangdan" to listOf(
                "Cilingga", "Darangdan", "Depok", "Gununghejo", "Legoksari", "Linggamukti", "Linggasari",
                "Mekarsari", "Nagrak", "Nangewer", "Neglasari", "Pasirangin", "Sadarkarya", "Sawit", "Sirnamanah"
            ),
            "Jatiluhur" to listOf(
                "Bunder", "Cibinong", "Cikaobandung", "Cilegong", "Cisalada", "Jatiluhur", "Jatimekar",
                "Kembangkuning", "Mekargalih", "Parakanlima"
            ),
            "Kiarapedes" to listOf(
                "Cibeber", "Ciracas", "Gardu", "Kiarapedes", "Margaluyu", "Mekarjaya", "Parakan Garokgek",
                "Pusakamulya", "Sumbersari", "Taringgul Landeuh"
            ),
            "Maniis" to listOf(
                "Cijati", "Ciramahilir", "Citamiang", "Gunungkarung", "Pasirjambu", "Sinargalih", "Sukamukti","Tegaldatar"
            ),
            "Pasawahan" to listOf(
                "Cihuni", "Ciherang", "Cidahu", "Kertajaya", "Lebakanyar", "Margasari", "Pasawahan",
                "Pasawahananyar", "Pasawahan Kidul", "Sawah Kulon", "Selaawi", "Warung Kadu"
            ),
            "Plered" to listOf(
                "Plered", "Anjun", "Gandasoli", "Babakansari", "Gandamekar", "Citeko", "Citeko Kaler",
                "Linggarsari", "Rawasari", "Palinggihan", "Pamoyanan", "Sempur", "Cibogohilir", "Cibogogirang", "Sindangsari", "Liunggunung"
            ),
            "Pondoksalam" to listOf(
                "Bungurjaya", "Galudra", "Gurudug", "Parakansalam", "Pondokbungur", "Salamjaya",
                "Salammulya", "Salem", "Situ", "Sukajadi", "Tanjungsari"
            ),
            "Purwakarta" to listOf(
                "Nagri Kidul", "Nagri Tengah", "Sindangkasih", "Cipaisan", "Nagri Kaler", "Tegalmunjul",
                "Ciseureuh", "Munjuljaya", "Purwamekar", "Citalang"
            ),
            "Sukatani" to listOf(
                "Sukatani", "Cibodas", "Cipicung", "Cianting Utara", "Cianting", "Pasirmunjul", "Cijantung", "Cilalawi",
                "Tajursindang", "Malangnengah", "Sukajaya", "Panyindangan", "Sindanglaya", "Sukamaju"
            ),
            "Sukasari" to listOf(
                "Kutamanah", "Kertamanah", "Ciririp", "Parungbanteng", "Sukasari"
            ),
            "Tegalwaru" to listOf(
                "Batutumpang", "Cadassari", "Cadasmekar", "Cisarua", "Citalang","Tegalwaru",
                "Warungjeruk", "Galumpit", "Karoya", "Pasanggrahan", "Sukahaji", "Sukamulya", "Tegalsari"
            ),
            "Wanayasa" to listOf(
                "Wanayasa", "Sukadami", "Wanasari", "Simpang", "Nagrog", "Cibuntu", "Babakan", "Nangerang",
                "Ciawi", "Sumurugul", "Raharja", "Sakambang", "Legokhuni", "Taringgul Tonggoh", "Taringgul Tengah"
            )
        )

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

        // Koordinat
        etKoordinat = findViewById(R.id.etKoordinat)
        etKoordinatGeografi = findViewById(R.id.etKoordinatGeografi)
        btnTambahKoordinat = findViewById(R.id.btnTambahKoordinat)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        laporanId = intent.getLongExtra("laporan_id", -1L)
        if (laporanId == -1L) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = AppDatabase.Companion.getDatabase(this)
        val laporanDao = db.laporanDao()
        val korbanDao = db.korbanDao()

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) { laporanDao.getById(laporanId) } ?: run {
                Toast.makeText(this@EditLaporanActivity, "Data laporan tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch

            }
            reguLapor.setText(laporan.reguLapor, false)

            etHari.setText(laporan.hari, false)
            etTanggal.setText(laporan.tanggal)
            setInputText(R.id.etJenisKejadian, laporan.jenisKejadian)
            setInputText(R.id.etWaktuLaporan, laporan.waktuLaporan)
            setInputText(R.id.etWaktuSampai, laporan.waktuSampai)
            setInputText(R.id.etJarak, laporan.jarak)
            setInputText(R.id.etResponTime, laporan.responTime)
            setInputText(R.id.etWaktuKembali, laporan.waktuKembali)
            setInputText(R.id.etObjek, laporan.objek)

            etAlamatDetail.setText(laporan.alamatDetail)
            etKabupaten.setText(laporan.kabupaten, false)
            etKecamatan.setText(laporan.kecamatan, false)
            val desaList = kecamatanMap[laporan.kecamatan] ?: emptyList()
            val adapterDesa = ArrayAdapter(
                this@EditLaporanActivity,
                android.R.layout.simple_dropdown_item_1line,
                desaList
            )
            etDesa.setAdapter(adapterDesa)
            etDesa.setText(laporan.desa, false)

            setInputText(R.id.etKoordinat, laporan.koordinat)
            setInputText(R.id.etKoordinatGeografi, laporan.koordinatGeografi)
            setInputText(R.id.etLuas, laporan.luas)
            setInputText(R.id.etNamaPelapor, laporan.namaPelapor)
            setInputText(R.id.etHpPelapor, laporan.hpPelapor)
            setInputText(R.id.etAlamatPelapor, laporan.alamatPelapor)
            setInputText(R.id.etNamaPemilik, laporan.namaPemilik)
            setInputText(R.id.etHpPemilik, laporan.hpPemilik)
            setInputText(R.id.etAlamatPemilik, laporan.alamatPemilik)
            setInputText(R.id.etAsetSelamat, laporan.asetSelamat)
            setInputText(R.id.etTafsiranSelamat, laporan.tafsiranSelamat)
            setInputText(R.id.etAsetTerbakar, laporan.asetTerbakar)
            setInputText(R.id.etTafsiranTerbakar, laporan.tafsiranTerbakar)
            setInputText(R.id.etKronologi, laporan.deskripsiKronologi)
            setInputText(R.id.etPenanggulangan, laporan.deskripsiPenanggulangan)
            setInputText(R.id.etPeralatan, laporan.peralatan)
            setInputText(R.id.etPetugas, laporan.petugas)
            setInputText(R.id.etInstansi, laporan.instansi)
            setInputText(R.id.etKlasifikasi, laporan.klasifikasi)
            setInputText(R.id.etKorbanSelamat, laporan.korbanSelamat)

            // === Load korban ===
            val korbanList =
                withContext(Dispatchers.IO) { korbanDao.getKorbanByLaporan(laporan.id) }
            layoutKorbanContainer.removeAllViews()
            korbanList.forEach { korban ->
                addKorbanView(
                    korban.jenisKorban,
                    korban.namaKorban,
                    korban.jkKorban,
                    korban.usiaKorban,
                    korban.kondisiFisikKorban,
                    korban.nikKorban,
                    korban.kkKorban,
                    korban.ttlKorban,
                    korban.alamatKorban
                )
            }

            // === Load kendaraan ===
            layoutKendaraan.removeAllViews()
            checkBoxKendaraan.clear()
            kendaraanList.forEach { kendaraan ->
                val cb = CheckBox(this@EditLaporanActivity).apply {
                    text = kendaraan
                    if (!laporan.kendaraan.isNullOrEmpty() && laporan.kendaraan.contains(kendaraan)) {
                        isChecked = true
                    }
                }
                layoutKendaraan.addView(cb)
                checkBoxKendaraan.add(cb)
            }

// isi kendaraan lainnya
            val sisaKendaraan = laporan.kendaraan?.split(", ")
                ?.filter { it !in kendaraanList }
                ?.joinToString(", ") ?: ""
            etKendaraanLainnya.setText(sisaKendaraan)


// === Load dokumen ===
            layoutDokumen.removeAllViews()
            checkBoxDokumen.clear()
            dokumenList.forEach { dokumen ->
                val cb = CheckBox(this@EditLaporanActivity).apply {
                    text = dokumen
                    if (!laporan.dokumenTerbakar.isNullOrEmpty() && laporan.dokumenTerbakar.contains(dokumen)) {
                        isChecked = true
                    }
                }
                layoutDokumen.addView(cb)
                checkBoxDokumen.add(cb)
            }

// isi dokumen lainnya
            val sisaDokumen = laporan.dokumenTerbakar?.split(", ")
                ?.filter { it !in dokumenList }
                ?.joinToString(", ") ?: ""
            etDokumenLainnya.setText(sisaDokumen)


// === Load regu ===
            layoutRegu.removeAllViews()
            checkBoxRegu.clear()
            reguList.forEach { regu ->
                val cb = CheckBox(this@EditLaporanActivity).apply {
                    text = regu
                    if (!laporan.regu.isNullOrEmpty() && laporan.regu.contains(regu)) {
                        isChecked = true
                    }
                }
                layoutRegu.addView(cb)
                checkBoxRegu.add(cb)
            }

            setupChangeListeners()
            findViewById<Button>(R.id.btnTambahKorban).setOnClickListener {
                addKorbanView("", "", "", "", "", "", "", "", "")
            }

            btnTambahKoordinat.setOnClickListener {
                AlertDialog.Builder(this@EditLaporanActivity)
                    .setTitle("Konfirmasi")
                    .setMessage("Yakin ingin mengubah koordinat?")
                    .setPositiveButton("Ya") { _, _ ->
                        etKoordinat.text.clear()
                        getLocation()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            findViewById<Button>(R.id.btnSimpan).setOnClickListener {
                val kendaraanDipilih = buildString {
                    append(checkBoxKendaraan.filter { it.isChecked }.joinToString(", ") { it.text })
                    if (etKendaraanLainnya.text.isNotBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(etKendaraanLainnya.text.toString())
                    }
                }

                val dokumenDipilih = buildString {
                    append(checkBoxDokumen.filter { it.isChecked }.joinToString(", ") { it.text })
                    if (etDokumenLainnya.text.isNotBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(etDokumenLainnya.text.toString())
                    }
                }
                val reguDipilih = checkBoxRegu.filter { it.isChecked }.joinToString(", ") { it.text }

                val laporanBaru = laporan.copy(
                    hari = etHari.text.toString(),
                    tanggal = etTanggal.text.toString(),
                    jenisKejadian = getInputText(R.id.etJenisKejadian),
                    waktuLaporan = getInputText(R.id.etWaktuLaporan),
                    waktuSampai = getInputText(R.id.etWaktuSampai),
                    waktuKembali = getInputText(R.id.etWaktuKembali),
                    jarak = getInputText(R.id.etJarak),
                    responTime = getInputText(R.id.etResponTime),
                    objek = getInputText(R.id.etObjek),
                    alamatDetail = etAlamatDetail.text.toString(),
                    kabupaten = etKabupaten.text.toString(),
                    kecamatan = etKecamatan.text.toString(),
                    desa = etDesa.text.toString(),
                    koordinat = getInputText(R.id.etKoordinat),
                    koordinatGeografi = getInputText(R.id.etKoordinatGeografi),
                    luas = getInputText(R.id.etLuas),
                    namaPelapor = getInputText(R.id.etNamaPelapor),
                    hpPelapor = getInputText(R.id.etHpPelapor),
                    alamatPelapor = getInputText(R.id.etAlamatPelapor),
                    namaPemilik = getInputText(R.id.etNamaPemilik),
                    hpPemilik = getInputText(R.id.etHpPemilik),
                    alamatPemilik = getInputText(R.id.etAlamatPemilik),
                    asetSelamat = getInputText(R.id.etAsetSelamat),
                    tafsiranSelamat = getInputText(R.id.etTafsiranSelamat),
                    asetTerbakar = getInputText(R.id.etAsetTerbakar),
                    dokumenTerbakar = dokumenDipilih,
                    tafsiranTerbakar = getInputText(R.id.etTafsiranTerbakar),
                    deskripsiKronologi = getInputText(R.id.etKronologi),
                    deskripsiPenanggulangan = getInputText(R.id.etPenanggulangan),
                    peralatan = getInputText(R.id.etPeralatan),
                    petugas = getInputText(R.id.etPetugas),
                    instansi = getInputText(R.id.etInstansi),
                    klasifikasi = getInputText(R.id.etKlasifikasi),
                    korbanSelamat = getInputText(R.id.etKorbanSelamat),
                    kendaraan = kendaraanDipilih,
                    regu = reguDipilih,
                    reguLapor = reguLapor.text.toString(),

                    )

                lifecycleScope.launch(Dispatchers.IO) {
                    laporanDao.update(laporanBaru)

                    // === Hitung ulang waktuUlang ===
                    if (!laporanBaru.waktuSampai.isNullOrEmpty()) {
                        try {
                            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val date = format.parse(laporanBaru.waktuSampai)
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

                            laporanDao.updateWaktuUlang(laporanBaru.id, waktuUlangStr)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                    // === Simpan ulang korban ===
                    korbanDao.deleteByLaporan(laporan.id)
                    for (i in 0 until layoutKorbanContainer.childCount) {
                        val view = layoutKorbanContainer.getChildAt(i)
                        val spinnerJenisKorban = view.findViewById<Spinner>(R.id.spinnerJenisKorban)
                        val etNamaKorban = view.findViewById<EditText>(R.id.etNamaKorban)
                        val etJkKorban = view.findViewById<EditText>(R.id.etJK)
                        val etUsiaKorban = view.findViewById<EditText>(R.id.etUsiaKorban)
                        val spinnerKondisiFisik = view.findViewById<Spinner>(R.id.spinnerKondisiFisik)
                        val etNikKorban = view.findViewById<EditText>(R.id.etNIKKorban)
                        val etKkKorban = view.findViewById<EditText>(R.id.etKKKorban)
                        val etTtlKorban = view.findViewById<EditText>(R.id.etTTL)
                        val etAlamatKorban = view.findViewById<EditText>(R.id.etAlamatKorban)

                        val korban = Korban(
                            laporanId = laporan.id,
                            jenisKorban = spinnerJenisKorban.selectedItem.toString(),
                            namaKorban = etNamaKorban.text.toString(),
                            jkKorban = etJkKorban.text.toString(),
                            usiaKorban = etUsiaKorban.text.toString(),
                            kondisiFisikKorban = spinnerKondisiFisik.selectedItem.toString(),
                            nikKorban = etNikKorban.text.toString(),
                            kkKorban = etKkKorban.text.toString(),
                            ttlKorban = etTtlKorban.text.toString(),
                            alamatKorban = etAlamatKorban.text.toString()
                        )
                        korbanDao.insert(korban)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditLaporanActivity,
                            "Data berhasil diperbarui",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
    private fun setupChangeListeners() {
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
        fun traverse(viewGroup: ViewGroup) {
            for (i in 0 until viewGroup.childCount) {
                val v = viewGroup.getChildAt(i)
                when (v) {
                    is EditText -> v.addTextChangedListener { isDataChanged = true }
                    is AutoCompleteTextView -> v.addTextChangedListener { isDataChanged = true }
                    is CheckBox -> v.setOnCheckedChangeListener { _, _ -> isDataChanged = true }
                    is Spinner -> v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            isDataChanged = true
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                    is ViewGroup -> traverse(v)
                }
            }
        }
        traverse(rootView)
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

        // Request lokasi real-time
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000 // update tiap 2 detik
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
                    Toast.makeText(this@EditLaporanActivity, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
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

            // Pakai Locale.US supaya desimal titik, %04.1f biar selalu ada leading zero
            return String.Companion.format(Locale.US, "%d°%02d'%04.1f\"%s", deg, min, sec, dir)
        }

        return "${toDMS(lat, true)} ${toDMS(lon, false)}"
    }

    private fun setInputText(id: Int, value: String?) { findViewById<EditText>(id).setText(value ?: "") }
    private fun getInputText(id: Int): String = findViewById<EditText>(id).text.toString()

    private fun addKorbanView(
        jenisKorban: String,
        nama: String,
        jk: String,
        usia: String,
        kondisi: String,
        nik: String,
        kk: String,
        ttl: String,
        alamat: String
    ) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_korban, layoutKorbanContainer, false)

        // === Jenis Korban (spinner) ===
        val spinnerJenisKorban = view.findViewById<Spinner>(R.id.spinnerJenisKorban)
        val jenisOptions = listOf("Pilih jenis korban") + jenisKorbanOptions
        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisOptions)
        adapterJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenisKorban.adapter = adapterJenis

        val idxJenis = if (jenisKorban.isNullOrBlank()) 0 else jenisOptions.indexOf(jenisKorban).coerceAtLeast(0)
        spinnerJenisKorban.setSelection(idxJenis, false)
        spinnerJenisKorban.isActivated = (idxJenis == 0) // merah saat placeholder
        spinnerJenisKorban.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                spinnerJenisKorban.isActivated = (position == 0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // === JK (RadioGroup baru) -> etJK (lama, hidden) ===
        val rgJK = view.findViewById<RadioGroup>(R.id.rgJenisKelamin)
        val rbLaki = view.findViewById<RadioButton>(R.id.rbLaki)
        val rbPerempuan = view.findViewById<RadioButton>(R.id.rbPerempuan)
        val etJK = view.findViewById<EditText>(R.id.etJK)

        // Prefill radio dari data jk (tanpa auto pilih kalau kosong)
        when (jk) {
            "Laki-laki", "Laki", "L" -> rbLaki.isChecked = true
            "Perempuan", "P" -> rbPerempuan.isChecked = true
        }
        // Sinkronkan ke etJK
        fun syncJkToHidden() {
            etJK.setText(
                when (rgJK.checkedRadioButtonId) {
                    R.id.rbLaki -> "Laki-laki"
                    R.id.rbPerempuan -> "Perempuan"
                    else -> ""
                }
            )
        }
        syncJkToHidden()
        rgJK.setOnCheckedChangeListener { _, _ -> syncJkToHidden() }

        // === Kondisi Fisik (RadioGroup baru) -> spinnerKondisiFisik (lama, hidden) ===
        val rgKondisi2 = view.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
        val rbSehat = view.findViewById<RadioButton>(R.id.rbSehat)
        val rbKebutuhanKhusus = view.findViewById<RadioButton>(R.id.rbKebutuhanKhusus)
        val spinnerKondisiFisik = view.findViewById<Spinner>(R.id.spinnerKondisiFisik)

        val kondisiList = listOf("Pilih kondisi fisik", "Sehat", "Kebutuhan Khusus")
        val adapterKondisi = ArrayAdapter(this, android.R.layout.simple_spinner_item, kondisiList)
        adapterKondisi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKondisiFisik.adapter = adapterKondisi

// Prefill dari data kondisi (tanpa auto pilih kalau kosong)
        val idxKondisi = if (kondisi.isNullOrBlank()) 0 else kondisiList.indexOf(kondisi).coerceAtLeast(0)
        spinnerKondisiFisik.setSelection(idxKondisi, false)

        when (kondisi) {
            "Sehat" -> rbSehat.isChecked = true
            "Kebutuhan Khusus" -> rbKebutuhanKhusus.isChecked = true
        }

        fun syncKondisiToSpinner() {
            val pos = when (rgKondisi2.checkedRadioButtonId) {
                R.id.rbSehat -> 1
                R.id.rbKebutuhanKhusus -> 2
                else -> 0 // placeholder
            }
            spinnerKondisiFisik.setSelection(pos, false)
        }
        syncKondisiToSpinner()
        rgKondisi2.setOnCheckedChangeListener { _, _ -> syncKondisiToSpinner() }

        // === Field lain ===
        view.findViewById<EditText>(R.id.etNamaKorban).setText(nama)
        view.findViewById<EditText>(R.id.etUsiaKorban).setText(usia)
        view.findViewById<EditText>(R.id.etNIKKorban).setText(nik)
        view.findViewById<EditText>(R.id.etKKKorban).setText(kk)
        view.findViewById<EditText>(R.id.etTTL).setText(ttl)
        view.findViewById<EditText>(R.id.etAlamatKorban).setText(alamat)

        // === Hapus ===
        view.findViewById<ImageButton>(R.id.btnHapusKorban).setOnClickListener {
            layoutKorbanContainer.removeView(view)
        }

        layoutKorbanContainer.addView(view)
    }


    override fun onBackPressed() {
        if (isDataChanged) {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Perubahan belum disimpan. Yakin keluar tanpa menyimpan?")
                .setPositiveButton("Ya") { _, _ -> super.onBackPressed() }
                .setNegativeButton("Batal", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}