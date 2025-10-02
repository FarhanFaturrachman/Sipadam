package com.example.sipadam.penyelamatan.pencarianpenyelamatan

import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.SaksiLaporanPencarianPenyelamatan
import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
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

class EditPencarianPenyelamatanActivity : AppCompatActivity() {

    // DB
    private lateinit var db: AppDatabasePencarianPenyelamatan
    private var laporanId: Long = -1L
    private var laporan: LaporanPencarianPenyelamatan? = null

    // Form changed flag
    private var isFormChanged = false

    // Location
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
    private lateinit var etApd: EditText
    private lateinit var etRencana: EditText
    private lateinit var etKronologi: EditText
    private lateinit var etUpaya: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnTambahKoordinat: Button
    private lateinit var btnTambahKorban: Button

    private lateinit var etKorbanSelamat: EditText
    private lateinit var etKendaraanLainnya: EditText
    private lateinit var etPetugasLainnya: EditText
    private lateinit var etIntansi: EditText

    // Containers
    private lateinit var layoutKorbanContainer: LinearLayout
    private lateinit var layoutKendaraan: LinearLayout
    private lateinit var layoutPetugas: LinearLayout

    // Date/time formats
    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var timeFormat: SimpleDateFormat
    private lateinit var dayOfWeekFormat: SimpleDateFormat

    private lateinit var btnTambahSaksi: Button
    private lateinit var layoutSaksiContainer: LinearLayout


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        private val kendaraanArray = listOf(
            "KR 01 (T 9909 A)","KR 02 (T 9910 A)","KR 03 (T 9925 B)","KR 04 (T 9901 B)",
            "KR 05 (T 8115 A)","KR 06 (T 9942 J)","KR 07 (T 9941 J)","Ambulance (T 9914 B)",
            "Toyota Hilux (T 8350 A)","Toyota Double Cabin (T 8454 A)","Honda Trail CRF (T 3484 B)",
            "Honda Trail CRF (T 2031 C)","Honda Trail CRF (T 3482 B)","Honda Trail CRF (T 5584 B)",
            "Honda Trail CRF (T 4153 B)"
        )

        private val petugasList = listOf(
            "Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3"
        )

        private val kabupatenList = listOf("Purwakarta")
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

        private val jenisKegiatanList = listOf("Evakuasi Korban", "Pelepasan Cincin Yang Menyebabkan Infeksi")
        private val reguList = listOf("Regu 1 Pusat","Regu 2 Pusat","Regu 3 Pusat",
            "Regu 1 UPTD WIL.1","Regu 2 UPTD WIL.1","Regu 3 UPTD WIL.1",
            "Regu 1 UPTD WIL.2","Regu 2 UPTD WIL.2","Regu 3 UPTD WIL.2",
            "Regu 1 UPTD WIL.3","Regu 2 UPTD WIL.3","Regu 3 UPTD WIL.3")
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
                    Toast.makeText(this@EditPencarianPenyelamatanActivity, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        initViews()
        setupAutoComplete()
        setupDateTimePickers()
        generateKendaraanCheckbox()
        generatePetugasCheckbox()

        btnTambahSaksi = findViewById(R.id.btnTambahSaksi)
        layoutSaksiContainer = findViewById(R.id.layoutSaksiContainer)

        btnTambahSaksi.setOnClickListener { tambahFormSaksi() }


        btnTambahKorban.setOnClickListener { tambahFormKorban() }
        btnTambahKoordinat.setOnClickListener {
            etKoordinatDesimal.text.clear()
            etKoordinatGeografi.text.clear()
            getLocation()
        }

        btnSimpan.text = "UPDATE LAPORAN"
        btnSimpan.setOnClickListener { updateLaporan() }

        laporanId = intent.getLongExtra("laporan_id", -1L)
        if (laporanId == -1L) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            laporan = withContext(Dispatchers.IO) { db.laporanDao().getById(laporanId) }
            laporan?.let { isiForm(it) }
        }
        setupFormChangeListeners()
    }

    private fun setupFormChangeListeners() {
        val textWatchers = listOf(
            etJenisKegiatan, etRegu, etHariDiterima, etTanggalDiterima, etWaktuDiterima,
            etHariTindak, etTanggalTindak, etWaktuTindak, etAlamat, etKabupaten,
            etKecamatan, etDesa, etJarak, etKoordinatDesimal, etKoordinatGeografi,
            etNamaPelapor, etUsiaPelapor, etHpPelapor, etAlamatPelapor,
            etApd, etUpaya, etKronologi, etIntansi,etRencana
        )

        textWatchers.forEach { et -> et.addTextChangedListener { isFormChanged = true } }

        // Checkbox kendaraan & petugas
        layoutKendaraan.children.filterIsInstance<CheckBox>().forEach { cb -> cb.setOnCheckedChangeListener { _, _ -> isFormChanged = true } }
        layoutPetugas.children.filterIsInstance<CheckBox>().forEach { cb -> cb.setOnCheckedChangeListener { _, _ -> isFormChanged = true } }
    }

    private fun setupAutoComplete() {
        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jenisKegiatanList)
        etJenisKegiatan.setAdapter(adapterJenis)
        etJenisKegiatan.setOnClickListener { etJenisKegiatan.showDropDown() }

        val adapterRegu = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, reguList)
        etRegu.setAdapter(adapterRegu)
        etRegu.setOnClickListener { etRegu.showDropDown() }

        val adapterKabupaten = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kabupatenList)
        etKabupaten.setAdapter(adapterKabupaten)
        etKabupaten.setOnClickListener { etKabupaten.showDropDown() }

        val adapterKecamatan = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kecamatanMap.keys.toList())
        etKecamatan.setAdapter(adapterKecamatan)
        etKecamatan.setOnClickListener { etKecamatan.showDropDown() }

        etKecamatan.setOnItemClickListener { _, _, pos, _ ->
            val kec = (etKecamatan.adapter.getItem(pos) ?: "") as String
            val desaList = kecamatanMap[kec] ?: emptyList()
            val adapterDesa = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, desaList)
            etDesa.setAdapter(adapterDesa)
            etDesa.setText("", false)
            etDesa.setOnClickListener { etDesa.showDropDown() }
        }
    }

    private fun initViews() {
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
        etKendaraanLainnya = findViewById(R.id.etKendaraanLainnya)
        etPetugasLainnya = findViewById(R.id.etPetugasLainnya)
        etApd = findViewById(R.id.etApd)
        etRencana = findViewById(R.id.etRencana)
        etKronologi = findViewById(R.id.etKronologi)
        etUpaya = findViewById(R.id.etUpaya)
        etIntansi = findViewById(R.id.etInstansi)

        btnSimpan = findViewById(R.id.btnSimpan)
        btnTambahKoordinat = findViewById(R.id.btnTambahKoordinat)
        btnTambahKorban = findViewById(R.id.btnTambahKorban)

        layoutKorbanContainer = findViewById(R.id.layoutKorbanContainer)
        layoutKendaraan = findViewById(R.id.layoutKendaraan)
        layoutPetugas = findViewById(R.id.layoutPetugas)

        calendar = Calendar.getInstance()
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
    }

    private fun setupDateTimePickers() {
        etTanggalDiterima.setOnClickListener { showDatePicker(etTanggalDiterima, etHariDiterima) }
        etTanggalTindak.setOnClickListener { showDatePicker(etTanggalTindak, etHariTindak) }
        etWaktuDiterima.setOnClickListener { showTimePicker(etWaktuDiterima) }
        etWaktuTindak.setOnClickListener { showTimePicker(etWaktuTindak) }
    }

    private fun generateKendaraanCheckbox() {
        layoutKendaraan.removeAllViews()
        kendaraanArray.forEach { item ->
            val cb = CheckBox(this).apply { text = item }
            layoutKendaraan.addView(cb)
        }
    }

    private fun generatePetugasCheckbox() {
        layoutPetugas.removeAllViews()
        petugasList.forEach { item ->
            val cb = CheckBox(this).apply { text = item }
            layoutPetugas.addView(cb)
        }
    }

    private fun tambahFormKorban(data: KorbanLaporanPencarianPenyelamatan? = null) {
        val view = layoutInflater.inflate(R.layout.item_korban_pencarian, layoutKorbanContainer, false)

        val spinnerJenis = view.findViewById<Spinner>(R.id.spinnerJenisKorban)
        val etNama = view.findViewById<EditText>(R.id.etNamaKorban)
        val etUsia = view.findViewById<EditText>(R.id.etUsiaKorban)
        val etNIK = view.findViewById<EditText>(R.id.etNIKKorban)
        val etKK = view.findViewById<EditText>(R.id.etKKKorban)
        val etTTL = view.findViewById<EditText>(R.id.etTTL)
        val etAlamatKorban = view.findViewById<EditText>(R.id.etAlamatKorban)
        val etCiriciri = view.findViewById<EditText>(R.id.etCiriciri)
        val rgJk = view.findViewById<RadioGroup>(R.id.rgJenisKelamin)
        val rgKondisi = view.findViewById<RadioGroup>(R.id.rgKondisiFisik2)
        val btnHapus = view.findViewById<ImageButton>(R.id.btnHapusKorban)

        val jenisKorbanOptions = listOf("Meninggal Dunia", "Luka Ringan", "Luka Berat","Dalam Pencarian")
        val jenisAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Pilih jenis korban") + jenisKorbanOptions)
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = jenisAdapter

        if (data == null) {
            spinnerJenis.setSelection(0)
            spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2"))
        } else {
            val pos = jenisAdapter.getPosition(data.jenisKorban)
            if (pos >= 0) {
                spinnerJenis.setSelection(pos)
                spinnerJenis.setBackgroundColor(Color.TRANSPARENT)
            } else {
                spinnerJenis.setSelection(0)
                spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2"))
            }

            etNama.setText(data.namaKorban)
            etUsia.setText(data.usiaKorban)
            etNIK.setText(data.nikKorban)
            etKK.setText(data.kkKorban)
            etTTL.setText(data.ttlKorban)
            etAlamatKorban.setText(data.alamatKorban)
            etCiriciri.setText(data.ciriciri)
            if (data.jkKorban == "Laki-laki") rgJk.check(R.id.rbLaki)
            else if (data.jkKorban == "Perempuan") rgJk.check(R.id.rbPerempuan)
            if (data.kondisiFisikKorban == "Sehat") rgKondisi.check(R.id.rbSehat)
            else if (data.kondisiFisikKorban == "Kebutuhan Khusus") rgKondisi.check(R.id.rbKebutuhanKhusus)
        }

        spinnerJenis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                if (position == 0) spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2")) else spinnerJenis.setBackgroundColor(Color.TRANSPARENT)
                isFormChanged = true
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        listOf(etNama, etUsia, etNIK, etKK, etTTL, etAlamatKorban).forEach { it.addTextChangedListener { isFormChanged = true } }
        rgJk.setOnCheckedChangeListener { _, _ -> isFormChanged = true }
        rgKondisi.setOnCheckedChangeListener { _, _ -> isFormChanged = true }

        btnHapus.setOnClickListener {
            layoutKorbanContainer.removeView(view)
            isFormChanged = true
        }

        layoutKorbanContainer.addView(view)
        isFormChanged = true
    }

    private fun tambahFormSaksi(data: SaksiLaporanPencarianPenyelamatan? = null) {
        val view = layoutInflater.inflate(R.layout.item_saksi_pencarian, layoutSaksiContainer, false)

        val etNama = view.findViewById<EditText>(R.id.etNamaSaksi)
        val rgJK = view.findViewById<RadioGroup>(R.id.rgJkSaksi)
        val etUmur = view.findViewById<EditText>(R.id.etUmurSaksi)
        val etHp = view.findViewById<EditText>(R.id.etNoHpSaksi)
        val etAlamat = view.findViewById<EditText>(R.id.etAlamatSaksi)
        val btnHapus = view.findViewById<ImageButton>(R.id.btnHapusSaksi)

        if (data != null) {
            etNama.setText(data.namaSaksi)
            etUmur.setText(data.umurSaksi)
            etHp.setText(data.noHpSaksi)
            etAlamat.setText(data.alamatSaksi)
            if (data.jkSaksi == "Laki-laki") rgJK.check(R.id.rbSaksiL)
            else if (data.jkSaksi == "Perempuan") rgJK.check(R.id.rbSaksiP)
        }

        btnHapus.setOnClickListener {
            layoutSaksiContainer.removeView(view)
            isFormChanged = true
        }

        listOf(etNama, etUmur, etHp, etAlamat).forEach { it.addTextChangedListener { isFormChanged = true } }
        rgJK.setOnCheckedChangeListener { _, _ -> isFormChanged = true }

        layoutSaksiContainer.addView(view)
        isFormChanged = true
    }


    private fun getCheckedFromLayout(container: LinearLayout): String =
        container.children.filterIsInstance<CheckBox>().filter { it.isChecked }.joinToString(", ") { it.text.toString() }

    private suspend fun isiForm(l: LaporanPencarianPenyelamatan) = withContext(Dispatchers.Main) {
        etJenisKegiatan.setText(l.jenisKegiatan, false)
        etRegu.setText(l.regu, false)
        etKabupaten.setText(l.kabupaten, false)
        etKecamatan.setText(l.kecamatan, false)

        // desa adapter
        val desaList = kecamatanMap[l.kecamatan] ?: emptyList()
        val adapterDesa = ArrayAdapter(this@EditPencarianPenyelamatanActivity, android.R.layout.simple_dropdown_item_1line, desaList)
        etDesa.setAdapter(adapterDesa)
        etDesa.setText(l.desa, false)

        etHariDiterima.setText(l.hariDiterima)
        etTanggalDiterima.setText(l.tanggalDiterima)
        etWaktuDiterima.setText(l.waktuDiterima)
        etHariTindak.setText(l.hariTindak)
        etTanggalTindak.setText(l.tanggalTindak)
        etWaktuTindak.setText(l.waktuTindak)
        etAlamat.setText(l.alamat)
        etJarak.setText(l.jarak)
        etKoordinatDesimal.setText(l.koordinatDesimal)
        etKoordinatGeografi.setText(l.koordinatGeografi)
        etNamaPelapor.setText(l.namaPelapor)
        etUsiaPelapor.setText(l.usiaPelapor)
        etHpPelapor.setText(l.noHpPelapor)
        etAlamatPelapor.setText(l.alamatPelapor)
        etKorbanSelamat.setText(l.korbanSelamat)
        etApd.setText(l.apd)
        etRencana.setText(l.rencana)
        etKronologi.setText(l.kronologi)
        etUpaya.setText(l.upaya)
        etIntansi.setText(l.instansi)

        // Kendaraan
        val kendaraanSaved = l.kendaraan.split(", ").filter { it.isNotBlank() }
        for (i in 0 until layoutKendaraan.childCount) {
            val cb = layoutKendaraan.getChildAt(i) as CheckBox
            cb.isChecked = kendaraanSaved.contains(cb.text.toString())
        }
        val kendaraanLainnya = kendaraanSaved.filter { saved ->
            (0 until layoutKendaraan.childCount).none { (layoutKendaraan.getChildAt(it) as CheckBox).text == saved }
        }
        etKendaraanLainnya.setText(kendaraanLainnya.joinToString(", "))

        // Petugas
        val petugasSaved = l.petugas.split(", ").filter { it.isNotBlank() }
        for (i in 0 until layoutPetugas.childCount) {
            val cb = layoutPetugas.getChildAt(i) as CheckBox
            cb.isChecked = petugasSaved.contains(cb.text.toString())
        }
        val petugasLainnya = petugasSaved.filter { saved ->
            (0 until layoutPetugas.childCount).none { (layoutPetugas.getChildAt(it) as CheckBox).text == saved }
        }
        etPetugasLainnya.setText(petugasLainnya.joinToString(", "))

        // Korban: load from DB (suspend)
        val korbanList = withContext(Dispatchers.IO) { db.korbanDao().getKorbanByLaporanNow(l.id) }
        layoutKorbanContainer.removeAllViews()
        korbanList.forEach { tambahFormKorban(it) }

        // Saksi
        val saksiList = withContext(Dispatchers.IO) { db.saksiDao().getSaksiByLaporan(l.id) }
        layoutSaksiContainer.removeAllViews()
        saksiList.forEach { tambahFormSaksi(it) }

    }

    private fun updateLaporan() {
        if (laporan == null) return
        if (!validateKorban()) return
        if (!validateSaksi()) return

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
                    laporanId = laporanId,
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

        val updated = laporan!!.copy(
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
            apd = etApd.text.toString(),
            rencana = etRencana.text.toString(),
            kronologi = etKronologi.text.toString(),
            upaya = etUpaya.text.toString(),
            instansi = etIntansi.text.toString(),
            kendaraan = buildString {
                append(getCheckedFromLayout(layoutKendaraan))
                if (etKendaraanLainnya.text.isNotBlank()) {
                    if (isNotEmpty()) append(", ")
                    append(etKendaraanLainnya.text.toString())
                }
            },
            petugas = buildString {
                append(getCheckedFromLayout(layoutPetugas))
                if (etPetugasLainnya.text.isNotBlank()) {
                    if (isNotEmpty()) append(", ")
                    append(etPetugasLainnya.text.toString())
                }
            }
        )

        // Simpan saksi
        val saksiList = mutableListOf<SaksiLaporanPencarianPenyelamatan>()
        for (i in 0 until layoutSaksiContainer.childCount) {
            val v = layoutSaksiContainer.getChildAt(i)
            val nama = v.findViewById<EditText>(R.id.etNamaSaksi).text.toString()
            val jk = v.findViewById<RadioGroup>(R.id.rgJkSaksi).let {
                if (it.checkedRadioButtonId != -1) it.findViewById<RadioButton>(it.checkedRadioButtonId).text.toString() else null
            }
            val umur = v.findViewById<EditText>(R.id.etUmurSaksi).text.toString()
            val hp = v.findViewById<EditText>(R.id.etNoHpSaksi).text.toString()
            val alamat = v.findViewById<EditText>(R.id.etAlamatSaksi).text.toString()

            saksiList.add(
                SaksiLaporanPencarianPenyelamatan(
                    laporanId = laporanId,
                    namaSaksi = nama,
                    jkSaksi = jk,
                    umurSaksi = umur,
                    noHpSaksi = hp,
                    alamatSaksi = alamat
                )
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            db.laporanDao().update(updated)

            // update korban
            db.korbanDao().deleteByLaporanId(laporanId)
            if (korbanList.isNotEmpty()) db.korbanDao().insertAll(korbanList)

            // update saksi
            db.saksiDao().deleteByLaporanId(laporanId)
            if (saksiList.isNotEmpty()) {
                db.saksiDao().insertAll(saksiList)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditPencarianPenyelamatanActivity, "Laporan diperbarui", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

    }

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

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan untuk mendapatkan koordinat.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToDMS(lat: Double, lon: Double): String {
        fun toDMS(value: Double, isLat: Boolean): String {
            val dir = if (isLat) if (value >= 0) "N" else "S" else if (value >= 0) "E" else "W"
            val absVal = Math.abs(value)
            val deg = absVal.toInt()
            val minFull = (absVal - deg) * 60
            val min = minFull.toInt()
            val sec = (minFull - min) * 60
            return String.format(Locale.US, "%d°%02d'%04.1f\"%s", deg, min, sec, dir)
        }
        return "${toDMS(lat, true)} ${toDMS(lon, false)}"
    }

    private fun validateKorban(): Boolean {
        layoutKorbanContainer.children.forEach { view ->
            val spinnerJenis = view.findViewById<Spinner>(R.id.spinnerJenisKorban)
            val etNama = view.findViewById<EditText>(R.id.etNamaKorban)
            val etUsia = view.findViewById<EditText>(R.id.etUsiaKorban)
            val rgJk = view.findViewById<RadioGroup>(R.id.rgJenisKelamin)
            val rgKondisi = view.findViewById<RadioGroup>(R.id.rgKondisiFisik2)

            if (spinnerJenis.selectedItemPosition == 0) {
                spinnerJenis.setBackgroundColor(Color.parseColor("#FFCDD2"))
                Toast.makeText(this, "Jenis korban wajib dipilih", Toast.LENGTH_SHORT).show()
                return false
            }
            if (etNama.text.isNullOrBlank()) {
                etNama.error = "Nama wajib diisi"; etNama.requestFocus(); return false
            }
            if (etUsia.text.isNullOrBlank()) {
                etUsia.error = "Usia wajib diisi"; etUsia.requestFocus(); return false
            }
            if (rgJk.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Jenis kelamin wajib dipilih", Toast.LENGTH_SHORT).show(); return false
            }
            if (rgKondisi.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Kondisi fisik wajib dipilih", Toast.LENGTH_SHORT).show(); return false
            }
        }
        return true
    }
    private fun validateSaksi(): Boolean {
        layoutSaksiContainer.children.forEach { view ->
            val etNama = view.findViewById<EditText>(R.id.etNamaSaksi)
            val etUmur = view.findViewById<EditText>(R.id.etUmurSaksi)
            val rgJK = view.findViewById<RadioGroup>(R.id.rgJkSaksi)

            if (etNama.text.isNullOrBlank()) {
                etNama.error = "Nama wajib diisi"; etNama.requestFocus(); return false
            }
            if (etUmur.text.isNullOrBlank()) {
                etUmur.error = "Umur wajib diisi"; etUmur.requestFocus(); return false
            }
            if (rgJK.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Jenis kelamin saksi wajib dipilih", Toast.LENGTH_SHORT).show(); return false
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (isFormChanged) {
            AlertDialog.Builder(this)
                .setTitle("Keluar tanpa menyimpan?")
                .setMessage("Ada perubahan yang belum disimpan. Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ -> super.onBackPressed() }
                .setNegativeButton("Batal", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
