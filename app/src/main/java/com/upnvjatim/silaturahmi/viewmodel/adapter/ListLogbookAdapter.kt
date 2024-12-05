package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardListlogbookBinding
import com.upnvjatim.silaturahmi.model.response.ItemLogbook

class ListLogbookAdapter(private var listLogbook: List<ItemLogbook?>)
    : ListAdapter<ItemLogbook, ListLogbookAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: ItemLogbook)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun updateData(newList: List<ItemLogbook>) {
        listLogbook = newList
    }

    class ViewHolder(val binding: CardListlogbookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardListlogbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtKet.text = listLogbook[position]?.deskripsi
        holder.binding.txtTgl.text = listLogbook[position]?.tgl

        holder.binding.cardLogbook.setOnClickListener {
            if (onItemClickListener != null) onItemClickListener?.onClick(listLogbook[position]!!)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ItemLogbook> = object : DiffUtil.ItemCallback<ItemLogbook>() {
            override fun areItemsTheSame(oldUser: ItemLogbook, newUser: ItemLogbook): Boolean = oldUser.id == newUser.id
            override fun areContentsTheSame(oldUser: ItemLogbook, newUser: ItemLogbook): Boolean = oldUser == newUser
        }
    }

    override fun getItemCount(): Int = listLogbook.size

}