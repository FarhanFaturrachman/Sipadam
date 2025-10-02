package com.example.sipadam.pemadaman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sipadam.R
import com.example.sipadam.pemadaman.data.model.LaporanKebakaran

class LaporanAdapter(
    private var laporanList: List<LaporanKebakaran>,
    private val onDetailClick: (LaporanKebakaran) -> Unit,
    private val onEditClick: (LaporanKebakaran) -> Unit,
    private val onDeleteClick: (LaporanKebakaran) -> Unit,
    private val onShareClick: (LaporanKebakaran) -> Unit
) : RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    fun updateData(newData: List<LaporanKebakaran>) {
        laporanList = newData
        notifyDataSetChanged()
    }

    inner class LaporanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvObjek: TextView = view.findViewById(R.id.etObjek)
        val tvHari: TextView = view.findViewById(R.id.etHari)
        val tvTanggal: TextView = view.findViewById(R.id.etTanggal)
        val tvWaktu: TextView = view.findViewById(R.id.etWaktuLaporan)
        val tvJenis: TextView = view.findViewById(R.id.etJenisKejadian)
        val tvLokasi: TextView = view.findViewById(R.id.etLokasi)
        val menuMore: View = view.findViewById(R.id.menuMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = laporanList[position]

        holder.tvObjek.text = "${laporan.objek}"
        holder.tvHari.text = "${laporan.hari}"
        holder.tvTanggal.text = "${laporan.tanggal}"
        holder.tvWaktu.text = "${laporan.waktuLaporan}"
        holder.tvJenis.text = "${laporan.jenisKejadian}"
        holder.tvLokasi.text = "${laporan.alamatDetail}, ${laporan.desa}, ${laporan.kecamatan}, ${laporan.kabupaten}"

        // Klik item → lihat detail
        holder.itemView.setOnClickListener { onDetailClick(laporan) }

        // Klik menu titik tiga
        holder.menuMore.setOnClickListener {
            // Gunakan ContextThemeWrapper agar popup menu pakai style custom
            val wrapper = android.view.ContextThemeWrapper(
                holder.itemView.context,
                R.style.MyPopupMenuOverride
            )
            val popup = PopupMenu(wrapper, it)
            popup.menuInflater.inflate(R.menu.menu_laporan_item, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        onEditClick(laporan)
                        true
                    }
                    R.id.menu_delete -> {
                        onDeleteClick(laporan)
                        true
                    }
                    R.id.menu_share -> {
                        onShareClick(laporan)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }

    }

    override fun getItemCount(): Int = laporanList.size
}
