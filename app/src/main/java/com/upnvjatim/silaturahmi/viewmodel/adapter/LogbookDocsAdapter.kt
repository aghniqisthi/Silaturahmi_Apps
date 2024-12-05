package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.ButtonDocslogbookBinding
import com.upnvjatim.silaturahmi.model.response.DataDocsLogbook
import com.upnvjatim.silaturahmi.openFile

class LogbookDocsAdapter(
    private val listLogbook: List<DataDocsLogbook?>,
    private val crud: Boolean
) :
    ListAdapter<DataDocsLogbook, LogbookDocsAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: DataDocsLogbook)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: ButtonDocslogbookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ButtonDocslogbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.btnFileLogbook.text = listLogbook[position]?.namaDokumen
        holder.binding.btnFileLogbook.setOnClickListener {
            openFile(listLogbook[position]?.fileDokumen.toString(), holder.itemView.context)
        }

        if (crud) {
            holder.binding.deleteLogbook.visibility = View.VISIBLE
            holder.binding.deleteLogbook.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onClick(listLogbook[position]!!)
                }
            }
        } else {
            holder.binding.deleteLogbook.visibility = View.GONE
        }

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataDocsLogbook> =
            object : DiffUtil.ItemCallback<DataDocsLogbook>() {
                override fun areItemsTheSame(
                    oldUser: DataDocsLogbook,
                    newUser: DataDocsLogbook
                ): Boolean {
                    return oldUser.id == newUser.id
                }

                override fun areContentsTheSame(
                    oldUser: DataDocsLogbook,
                    newUser: DataDocsLogbook
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun getItemCount(): Int = listLogbook.size

}