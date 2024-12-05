package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardLuaranMhsBinding
import com.upnvjatim.silaturahmi.mahasiswa.luaran.TambahLuaranActivity
import com.upnvjatim.silaturahmi.model.response.DataLuaran
import java.text.SimpleDateFormat
import java.util.Locale

class ListLuaranMhsAdapter(private val listLuaran: List<DataLuaran?>) :
    ListAdapter<DataLuaran, ListLuaranMhsAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: DataLuaran)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardLuaranMhsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardLuaranMhsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(listLuaran[position]?.isDeleted == false) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(listLuaran[position]?.tanggal)
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

            holder.binding.txtWaktuLuaran.text = formattedDate
            holder.binding.txtJenisLuaran.text = listLuaran[position]?.namaLuaran
            holder.binding.txtKetLuaran.text = listLuaran[position]?.keterangan

            holder.binding.btnDownload.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                val link =
                    Uri.parse("https://silaturahmi.upnjatim.ac.id/api/files?path=${listLuaran[position]?.link}")
                intent.setDataAndType(link, "application/pdf")
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                try {
                    holder.itemView.context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(holder.itemView.context, e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.e("error start link daftar", "${e.message} ; ${e.cause} ${e.stackTrace}")
                }
            }

            holder.binding.btnEdit.setOnClickListener {
                val intent = Intent(holder.itemView.context, TambahLuaranActivity::class.java)
                intent.putExtra("luaran", listLuaran[position])
                holder.itemView.context.startActivity(intent)
            }

            holder.binding.btnDelete.setOnClickListener {
                if (onItemClickListener != null) {
                    onItemClickListener?.onClick(listLuaran[position]!!)
                }
            }
        } else holder.binding.cardLuaran.visibility = View.GONE
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataLuaran> =
            object : DiffUtil.ItemCallback<DataLuaran>() {
                override fun areItemsTheSame(oldUser: DataLuaran, newUser: DataLuaran): Boolean {
                    return oldUser.id == newUser.id
                }

                override fun areContentsTheSame(oldUser: DataLuaran, newUser: DataLuaran): Boolean {
                    return oldUser == newUser
                }
            }
    }

    override fun getItemCount(): Int = listLuaran.size
}