package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.CardPlottingBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram

class PlottingAdapter(private val listMhs: List<ItemPendaftarProgram?>) :
    ListAdapter<ItemPendaftarProgram, PlottingAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: CardPlottingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardPlottingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNpm.text = listMhs[position]?.npm
        holder.binding.txtNama.text = listMhs[position]?.nama
        holder.binding.txtJenismbkm.text = listMhs[position]?.namaProgram
        holder.binding.txtPtperusahaan.text = listMhs[position]?.namaMitra

        if (listMhs[position]?.namaPegawai.isNullOrEmpty()) {
            holder.binding.txtDospem.text = "-"
            holder.binding.txtStatus.text = "Belum Dosen Pembimbing"
            holder.binding.txtStatus.setTextColor(Color.WHITE)
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
        } else {
            holder.binding.txtDospem.text = listMhs[position]?.namaPegawai
            holder.binding.txtStatus.text = "Sudah Dosen Pembimbing"
            holder.binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
        }

        holder.binding.cardPlotting.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("dataprogram", listMhs[position])
            if(getUser(holder.itemView.context)?.user?.role?.id == "HA07")
            Navigation.findNavController(it).navigate(R.id.action_plottingDosenFragment_to_detailPlottingFragment, bundle)
            else Navigation.findNavController(it).navigate(R.id.action_mbkmFragment_to_detailPlottingFragment, bundle)
        }

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ItemPendaftarProgram> =
            object : DiffUtil.ItemCallback<ItemPendaftarProgram>() {
                override fun areItemsTheSame(
                    oldUser: ItemPendaftarProgram,
                    newUser: ItemPendaftarProgram
                ): Boolean {
                    return oldUser.id == newUser.id
                }

                override fun areContentsTheSame(
                    oldUser: ItemPendaftarProgram,
                    newUser: ItemPendaftarProgram
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun getItemCount(): Int = listMhs.size

}