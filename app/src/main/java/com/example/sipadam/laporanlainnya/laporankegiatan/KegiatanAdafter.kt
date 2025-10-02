package com.example.sipadam.laporanlainnya.laporankegiatan

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.laporanlainnya.laporankegiatan.data.model.LaporanKegiatan

class KegiatanAdapter(
    private var laporanList: List<LaporanKegiatan>,
    private val onDetailClick: (LaporanKegiatan) -> Unit,
    private val onEditClick: (LaporanKegiatan) -> Unit,
    private val onDeleteClick: (LaporanKegiatan) -> Unit,
    private val onShareClick: (LaporanKegiatan) -> Unit
) : RecyclerView.Adapter<KegiatanAdapter.ViewHolder>() {

    fun updateData(newData: List<LaporanKegiatan>) {
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
        val menuMore: ImageView = view.findViewById(R.id.menuMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laporan = laporanList[position]

        // Mapping field LaporanKegiatan ke layout
        holder.tvObjek.text = laporan.kegiatan      // Objek utama = kegiatan
        holder.tvHari.text = laporan.hari
        holder.tvTanggal.text = laporan.tanggal
        holder.tvWaktu.text = laporan.waktu
        holder.tvJenis.text = laporan.kegiatan          // aku mapping "Jenis" ke regu biar semua terpakai
        holder.tvLokasi.text = laporan.lokasi

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
