package com.upnvjatim.silaturahmi.dosen

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.CardRoleBinding
import com.upnvjatim.silaturahmi.model.response.DataValidasiLogin

class RoleAdapter(private var listAkses: List<DataValidasiLogin?>) :
    RecyclerView.Adapter<RoleAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(alarm: DataValidasiLogin)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(var binding: CardRoleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardRoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(
            if (listAkses[position]?.name == "TIM MBKM"
                || listAkses[position]?.name == "PENILAI KONVERSI"
            ) R.drawable.rounded_groups_24
            else R.drawable.rounded_person_24
        ).into(holder.binding.imgIcon)

        val array = holder.itemView.context.resources.getStringArray(R.array.role_color)
        val randomStr = array[if (position < 4) position else position / 4]
//        Log.d("cek randomstr", "${Random.nextInt(array.size)} ${array[0]} ${array.size}")

        holder.binding.cardView6.setCardBackgroundColor(Color.parseColor(randomStr))
        holder.binding.txtRole.text = listAkses[position]?.name

        holder.binding.cardRole.setOnClickListener {
            if (onItemClickListener != null) {
                listAkses[position]?.let { onItemClickListener?.onClick(it) }
            }
        }
    }

    override fun getItemCount(): Int = listAkses.size

}