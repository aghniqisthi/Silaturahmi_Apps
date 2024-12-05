package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.CardAccPendaftarBinding
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram

class AccPendaftarAdapter(private val listMhs: List<ItemPendaftarProgram?>) :
    ListAdapter<ItemPendaftarProgram, AccPendaftarAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(alarm: ItemPendaftarProgram)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardAccPendaftarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardAccPendaftarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNpm.text = listMhs[position]?.npm
        holder.binding.txtNama.text = listMhs[position]?.nama
        holder.binding.txtJenismbkm.text = listMhs[position]?.namaProgram
        holder.binding.txtPtperusahaan.text = listMhs[position]?.namaMitra
        holder.binding.txtNonilai.visibility = View.GONE

        if (listMhs[position]?.status == 0) {
            holder.binding.txtDospem.text = "-"
            holder.binding.txtStatus.text = "Magang Belum Disetujui"
            holder.binding.txtStatus.setTextColor(Color.WHITE)
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            holder.binding.checkboxAcc.visibility = View.VISIBLE
        } else {
            holder.binding.txtDospem.text = listMhs[position]?.namaPegawai
            holder.binding.txtStatus.text = "Magang Telah Disetujui"
            holder.binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
            holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            holder.binding.checkboxAcc.visibility = View.GONE
        }

        holder.binding.cardAcc.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("dataprogram", listMhs[position])
            Navigation.findNavController(it).navigate(R.id.action_mbkmFragment_to_detailPendaftarMagangFragment, bundle)
        }

        holder.binding.checkboxAcc.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener?.onClick(listMhs[position]!!)
            }
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