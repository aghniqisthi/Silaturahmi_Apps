package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardLuaranBinding
import com.upnvjatim.silaturahmi.model.response.DataLuaran
import com.upnvjatim.silaturahmi.openFile
import java.text.SimpleDateFormat
import java.util.Locale

class ListLuaranAdapter(private val listLuaran: List<DataLuaran?>) :
    ListAdapter<DataLuaran, ListLuaranAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(val binding: CardLuaranBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardLuaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.txtWaktuLuaran.text = listLuaran[position]?.tanggal
//        holder.binding.txtKetLuaran.text = listLuaran[position]?.keterangan
//        holder.binding.btnFileLuaran.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            val link = Uri.parse("https://silaturahmi.upnjatim.ac.id/api/files?path=${listLuaran[position]?.link}")
//            intent.setDataAndType(link, "application/pdf")
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

//            try {
//                holder.itemView.context.startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                Toast.makeText(holder.itemView.context, e.message.toString(), Toast.LENGTH_LONG).show()
//                Log.e("error start link daftar", "${e.message} ; ${e.cause} ${e.stackTrace}")
//            }
//        }



        if(listLuaran[position]?.isDeleted == false) {
            Log.d("cek itemluarna : ${listLuaran.size}", "${listLuaran[position]?.keterangan} ; ${listLuaran[position]?.link} ; ${listLuaran[position]?.isDeleted}")
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(listLuaran[position]?.tanggal)
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

            holder.binding.txtWaktuLuaran.text = formattedDate
            holder.binding.txtKetLuaran.text = listLuaran[position]?.keterangan

            holder.binding.btnFileLuaran.setOnClickListener {
                openFile(listLuaran[position]?.link.toString(), holder.itemView.context)
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