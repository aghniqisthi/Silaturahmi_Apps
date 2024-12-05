package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.ButtonArraydocslogbookBinding
import com.upnvjatim.silaturahmi.model.request.PostDocsLogbook

class LogbookArrayDocsAdapter(private val listFile: List<PostDocsLogbook?>) :
    ListAdapter<PostDocsLogbook, LogbookArrayDocsAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: PostDocsLogbook)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: ButtonArraydocslogbookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ButtonArraydocslogbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.btnFileLogbook.text = listFile[position]?.nama_dokumen

        holder.binding.deleteLogbook.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener?.onClick(listFile[position]!!)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<PostDocsLogbook> =
            object : DiffUtil.ItemCallback<PostDocsLogbook>() {
                override fun areItemsTheSame(oldUser: PostDocsLogbook, newUser: PostDocsLogbook): Boolean {
                    return oldUser.file == newUser.file
                }

                override fun areContentsTheSame(oldUser: PostDocsLogbook, newUser: PostDocsLogbook): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun getItemCount(): Int = listFile.size

}