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
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram

class AccVerifNilaiAdapter(private val listMhs: List<ItemPendaftarProgram?>) :
    ListAdapter<ItemPendaftarProgram, AccVerifNilaiAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(alarm: ItemPendaftarProgram)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardAccPendaftarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            CardAccPendaftarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.checkboxAcc.visibility = View.GONE

        holder.binding.txtNpm.text = listMhs[position]?.npm
        holder.binding.txtNama.text = listMhs[position]?.nama
        holder.binding.txtJenismbkm.text = listMhs[position]?.namaProgram
        holder.binding.txtPtperusahaan.text = listMhs[position]?.namaMitra
        holder.binding.txtDospem.text = listMhs[position]?.namaPegawai ?: "-"

        if (getUser(holder.itemView.context)?.user?.role?.id == "HA06") {
            holder.binding.txtPtperusahaan.visibility = View.GONE
            holder.binding.txtDospem.visibility = View.GONE
            holder.binding.txtNonilai.visibility = View.GONE

            if (listMhs[position]?.status == 3 && listMhs[position]?.isFinish == true) {
                holder.binding.txtStatus.text = "Verifikasi Penilai Konversi"
                holder.binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            } else {
                holder.binding.txtStatus.text = "Nilai Belum Diverifikasi"
                holder.binding.txtStatus.setTextColor(Color.WHITE)
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            }

            holder.binding.cardAcc.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("dataprogram", listMhs[position])
                Navigation.findNavController(it)
                    .navigate(R.id.action_penilaianFragment_to_detailPenilaianFragment, bundle)
            }
        }
        else if (getUser(holder.itemView.context)?.user?.role?.id == "HA03"){
            if (listMhs[position]?.status == 1) {
                holder.binding.txtStatus.text = "Menunggu Persetujuan Revisi"
                holder.binding.txtStatus.setTextColor(Color.WHITE)
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                holder.binding.txtStatus.text = "Persetujuan Revisi Disetujui"
                holder.binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }

            holder.binding.cardAcc.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("dataprogram", listMhs[position])
                Navigation.findNavController(it)
                    .navigate(R.id.action_revisiNilaiFragment_to_detailRevisiNilaiFragment, bundle)
            }

            holder.binding.txtNonilai.visibility = View.GONE
        }
        else {
            if (listMhs[position]?.status != 3 && listMhs[position]?.isFinish == false) {
                holder.binding.txtStatus.text = "Minta Persetujuan Nilai"
                holder.binding.txtStatus.setTextColor(Color.WHITE)
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                holder.binding.txtStatus.text = "Penilaian Disetujui"
                holder.binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                holder.binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }

            if(listMhs[position]?.linkPenilaian == null || listMhs[position]?.linkPenilaian == "")
                holder.binding.txtNonilai.visibility = View.VISIBLE
            else holder.binding.txtNonilai.visibility = View.GONE

            holder.binding.cardAcc.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("dataprogram", listMhs[position])
                Navigation.findNavController(it)
                    .navigate(R.id.action_magangDospemFragment_to_detailVerifNilaiFragment, bundle)
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