package com.example.sipadam.penyelamatan.gawatdarurat

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.gawatdarurat.data.model.LaporanGawatDarurat

class GawatDaruratAdapter(
    private var laporanList: List<LaporanGawatDarurat>,
    private val onDetailClick: (LaporanGawatDarurat) -> Unit,
    private val onEditClick: (LaporanGawatDarurat) -> Unit,
    private val onDeleteClick: (LaporanGawatDarurat) -> Unit,
    private val onShareClick: (LaporanGawatDarurat) -> Unit
) : RecyclerView.Adapter<GawatDaruratAdapter.ViewHolder>() {

    fun updateData(newData: List<LaporanGawatDarurat>) {
        laporanList = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvObjek: TextView = view.findViewById(R.id.etObjek)
        val tvHari: TextView = view.findViewById(R.id.etHari)
        val tvTanggal: TextView = view.findViewById(R.id.etTanggal)
        val tvWaktu: TextView = view.findViewById(R.id.etWaktuLaporan)
        val tvJenis: TextView = view.findViewById(R.id.etJenisKejadian)
        val tvLokasi: TextView = view.findViewById(R.id.etLokasi)
        val menuMore: View = view.findViewById(R.id.menuMore)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan, parent, false) // pakai layout baru
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laporan = laporanList[position]

        holder.tvObjek.text = laporan.jenisKegiatan              // contoh: pakai jenisKegiatan sebagai objek
        holder.tvHari.text = laporan.hariDiterima
        holder.tvTanggal.text = laporan.tanggalDiterima
        holder.tvWaktu.text = laporan.waktuDiterima
        holder.tvJenis.text = laporan.jenisKegiatan
        holder.tvLokasi.text = "${laporan.alamat}, ${laporan.desa}, Kec. ${laporan.kecamatan}, Kab. ${laporan.kabupaten}"

        // klik item → detail
        holder.itemView.setOnClickListener { onDetailClick(laporan) }

        // klik titik tiga → menu popup
        holder.menuMore.setOnClickListener {
            val wrapper = ContextThemeWrapper(holder.itemView.context, R.style.MyPopupMenuOverride)
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater.inflate(R.menu.menu_laporan_item, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> { onEditClick(laporan); true }
                    R.id.menu_delete -> { onDeleteClick(laporan); true }
                    R.id.menu_share -> { onShareClick(laporan); true }
                    else -> false
                }
            }
            popup.show()
        }
    }


    override fun getItemCount(): Int = laporanList.size
}
