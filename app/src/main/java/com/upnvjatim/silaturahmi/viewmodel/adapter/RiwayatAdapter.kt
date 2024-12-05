package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardRiwayatBinding
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.statusKegiatan


class RiwayatAdapter(private val listRiwayat: List<ItemPendaftarProgram?>) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: ItemPendaftarProgram)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardRiwayatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtPosisi.text = listRiwayat[position]?.namaPosisiKegiatanTopik
        holder.binding.txtMitra.text = listRiwayat[position]?.namaMitra
        holder.binding.btnStatus.text = statusKegiatan((listRiwayat[position]?.status?:0), (listRiwayat[position]?.isFinish?:false))

        holder.binding.cardRiwayat.setOnClickListener{
            if (onItemClickListener != null) onItemClickListener?.onClick(listRiwayat[position]!!)

        }
    }

    override fun getItemCount(): Int = listRiwayat.size

}
