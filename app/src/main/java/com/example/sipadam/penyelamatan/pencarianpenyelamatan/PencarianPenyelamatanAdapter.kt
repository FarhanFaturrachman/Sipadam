package com.example.sipadam.penyelamatan.pencarianpenyelamatan

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.penyelamatan.pencarianpenyelamatan.data.model.LaporanPencarianPenyelamatan

class PencarianPenyelamatanAdapter(
    private var laporanList: List<LaporanPencarianPenyelamatan>,
    private val onDetailClick: (LaporanPencarianPenyelamatan) -> Unit,
    private val onEditClick: (LaporanPencarianPenyelamatan) -> Unit,
    private val onDeleteClick: (LaporanPencarianPenyelamatan) -> Unit,
    private val onShareClick: (LaporanPencarianPenyelamatan) -> Unit
) : RecyclerView.Adapter<PencarianPenyelamatanAdapter.ViewHolder>() {

    fun updateData(newData: List<LaporanPencarianPenyelamatan>) {
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
            .inflate(R.layout.item_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laporan = laporanList[position]

        holder.tvObjek.text = laporan.jenisKegiatan
        holder.tvHari.text = laporan.hariDiterima
        holder.tvTanggal.text = laporan.tanggalDiterima
        holder.tvWaktu.text = laporan.waktuDiterima
        holder.tvJenis.text = laporan.jenisKegiatan
        holder.tvLokasi.text =
            "${laporan.alamat}, ${laporan.desa}, Kec. ${laporan.kecamatan}, Kab. ${laporan.kabupaten}"

        // Klik item → detail
        holder.itemView.setOnClickListener { onDetailClick(laporan) }

        // Klik titik tiga → menu popup
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
