package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.CardPilihpaketkonversiBinding
import com.upnvjatim.silaturahmi.model.response.DataTranskrip
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi

class ListPaketAdapter(
    private val listPaket: List<ItemLookupPaketKonversi?>,
    private val listTranskrip: List<DataTranskrip?>
) : RecyclerView.Adapter<ListPaketAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(item: ItemLookupPaketKonversi)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class ViewHolder(val binding: CardPilihpaketkonversiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardPilihpaketkonversiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNamapaket.text = listPaket[position]?.namaPaketKonversi
        holder.binding.txtDescpaketkonversi.text = listPaket[position]?.deskripsi

        holder.binding.tableLayout.removeAllViews()
        holder.binding.tableLayout.addView(holder.binding.tableRow)

        val tableRowHeader = LayoutInflater.from(holder.itemView.context)
            .inflate(R.layout.tablerow_paketnilai, null) as TableRow

        val kodeMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_kodemk)
        kodeMatkulHeader.text = "Kode"
        kodeMatkulHeader.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.poppins_semibold)

        val namaMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_mk)
        namaMatkulHeader.text = "Mata Kuliah"
        namaMatkulHeader.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.poppins_semibold)

        val sksHeader = tableRowHeader.findViewById<TextView>(R.id.txt_sksmk)
        sksHeader.text = "SKS"
        sksHeader.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.poppins_semibold)

        val nilaiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_nilaimk)
        nilaiHeader.text = "Nilai"
        nilaiHeader.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.poppins_semibold)

        holder.binding.tableLayout.addView(tableRowHeader)

        listPaket[position]?.detail?.forEach { mk ->
            val tableRowData = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.tablerow_paketnilai, null) as TableRow

            val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
            kodeMatkul.text = mk?.kodeMatkul

            val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
            namaMatkul.text = mk?.namaMatkul

            val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
            sks.text = mk?.sks

            val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
            val nilai = listTranskrip.firstOrNull { it?.kode_mata_kuliah == mk?.kodeMatkul }?.nilai
            nilaiTextView.text = nilai?.toString() ?: " "

            holder.binding.tableLayout.addView(tableRowData)
        }

        holder.binding.cardPaketkonversi.setOnClickListener {
            if (holder.binding.linearHidden.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    holder.binding.cardPaketkonversi,
                    AutoTransition()
                )
                holder.binding.linearHidden.visibility = View.GONE
                holder.binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(
                    holder.binding.cardPaketkonversi,
                    AutoTransition()
                )
                holder.binding.linearHidden.visibility = View.VISIBLE
                holder.binding.imageButton.rotation = 90F
            }
        }

        holder.binding.imageButton.setOnClickListener{
            if (holder.binding.linearHidden.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    holder.binding.cardPaketkonversi,
                    AutoTransition()
                )
                holder.binding.linearHidden.visibility = View.GONE
                holder.binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(
                    holder.binding.cardPaketkonversi,
                    AutoTransition()
                )
                holder.binding.linearHidden.visibility = View.VISIBLE
                holder.binding.imageButton.rotation = 90F
            }
        }

        holder.binding.btnPilih.setOnClickListener{
            if (onItemClickListener != null) {
                onItemClickListener?.onClick(listPaket[position]!!)
            }
        }
    }

    override fun getItemCount(): Int = listPaket.size

}