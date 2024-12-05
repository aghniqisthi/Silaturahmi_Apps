package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardPilihprogramBinding
import com.upnvjatim.silaturahmi.model.response.ItemLookupProgram

class ListProgramAdapter(
    private val listProgram: List<ItemLookupProgram?>,
//    private val listener: OnProgramSelectedListener
) : ListAdapter<ItemLookupProgram, ListProgramAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: ItemLookupProgram)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardPilihprogramBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardPilihprogramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNamaprogram.text = listProgram[position]?.namaPosisiKegiatanTopik
        holder.binding.txtMitra.text = listProgram[position]?.namaMitra
        holder.binding.txtPosisikeg.text = listProgram[position]?.namaProgram
        holder.binding.txtAwalkeg.text = listProgram[position]?.awalKegiatan
        holder.binding.txtAkhirkeg.text = listProgram[position]?.akhirKegiatan

        holder.binding.cardProgram.setOnClickListener {
            if (holder.binding.linearHidden.visibility === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.binding.cardProgram, AutoTransition())
                holder.binding.linearHidden.visibility = View.GONE
                holder.binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.cardProgram, AutoTransition())
                holder.binding.linearHidden.visibility = View.VISIBLE
                holder.binding.imageButton.rotation = 90F
            }
        }

        holder.binding.imageButton.setOnClickListener {
            if (holder.binding.linearHidden.visibility === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.binding.cardProgram, AutoTransition())
                holder.binding.linearHidden.visibility = View.GONE
                holder.binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.cardProgram, AutoTransition())
                holder.binding.linearHidden.visibility = View.VISIBLE
                holder.binding.imageButton.rotation = 90F
            }
        }

        holder.binding.btnPilih.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener?.onClick(listProgram[position]!!)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ItemLookupProgram> =
            object : DiffUtil.ItemCallback<ItemLookupProgram>() {
                override fun areItemsTheSame(
                    oldUser: ItemLookupProgram,
                    newUser: ItemLookupProgram
                ): Boolean {
                    return oldUser.id == newUser.id
                }

                override fun areContentsTheSame(
                    oldUser: ItemLookupProgram,
                    newUser: ItemLookupProgram
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun getItemCount(): Int = listProgram.size

}