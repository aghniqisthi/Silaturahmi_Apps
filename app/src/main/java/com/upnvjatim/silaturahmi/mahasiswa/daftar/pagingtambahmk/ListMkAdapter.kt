package com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.TablerowCheckboxpaketnilaiBinding
import com.upnvjatim.silaturahmi.mahasiswa.daftar.MsibViewModel
import com.upnvjatim.silaturahmi.model.response.DataValidasiLogin
import com.upnvjatim.silaturahmi.model.response.ItemAllMatkul
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip

class ListMkAdapter(
    private val listTranskrip: ResponseTranskrip?,
    private val msibViewModel: MsibViewModel
//    private val getData: ItemLookupPaketKonversi?,
//    private val arrayMk: List<ItemAllMatkul>?,
//    private val onItemCheckedChange: (ItemAllMatkul, Boolean) -> Unit
)
    : PagingDataAdapter<ItemAllMatkul, ListMkAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(alarm: ItemAllMatkul, isChecked: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TablerowCheckboxpaketnilaiBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        val arrayMk = msibViewModel.paketData.value?.detail?.map {
            ItemAllMatkul(
                createdAt = it?.createdAt,
                createdBy = it?.createdBy,
                id = it?.id,
                idSiamikFakjur = null,
                isDeleted = false,
                kdFakjur = null,
                kodeMatkul = it?.kodeMatkul,
                namaMatkul = it?.namaMatkul,
                namaProdi = null,
                sks = it?.sks.toString().toIntOrNull(),
                updatedAt = it?.updatedAt,
                updatedBy = it?.updatedBy
            )
        }
        if (data != null) holder.bind(
            data, listTranskrip, arrayMk, // getData?.detail?.any { it?.kodeMatkul == data.kodeMatkul } == true,
            onItemClickListener
        )
//            onItemCheckedChange)
    }

    class MyViewHolder(private val binding: TablerowCheckboxpaketnilaiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: ItemAllMatkul,
            listTranskrip: ResponseTranskrip?,
//            isChecked: Boolean,
            arrayMk: List<ItemAllMatkul>?,
            onItemClickListener: OnItemClickListener?,
//            onItemCheckedChange: (ItemAllMatkul, Boolean) -> Unit
        ) {
            binding.txtKodemk.text = data.kodeMatkul
            binding.txtMk.text = data.namaMatkul
            binding.txtSksmk.text = data.sks.toString()
            binding.txtNilaimk.text =
                (listTranskrip?.data?.firstOrNull { it?.kode_mata_kuliah == data.kodeMatkul }?.nilai) ?: ""

            val isChecked = arrayMk?.any { it.kodeMatkul == data.kodeMatkul }
            binding.cbMk.setOnCheckedChangeListener(null) // Remove listener
            binding.cbMk.isChecked = isChecked ?: false
            binding.cbMk.setOnCheckedChangeListener { _, isChecked ->
//                onItemCheckedChange(data, isChecked)
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(data, isChecked)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemAllMatkul>() {
            override fun areItemsTheSame(oldItem: ItemAllMatkul, newItem: ItemAllMatkul): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ItemAllMatkul, newItem: ItemAllMatkul): Boolean {
                return oldItem.id == newItem.id && oldItem.kodeMatkul ==  newItem.kodeMatkul
            }
        }
    }
}

//class ListMkAdapter : PagingDataAdapter<ItemAllMatkul, ListMkAdapter.MyViewHolder>(DIFF_CALLBACK) {
//
//    private var onItemClickListener: OnItemClickListener? = null
//
//    fun interface OnItemClickListener {
//        fun onClick(alarm: ItemAllMatkul)
//    }
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        onItemClickListener = listener
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = TablerowCheckboxpaketnilaiBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return MyViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val data = getItem(position)
//        if (data != null) {
//            holder.bind(data, onItemClickListener)
//        }
//    }
//
//    class MyViewHolder(private val binding: TablerowCheckboxpaketnilaiBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(data: ItemAllMatkul, onItemClickListener: OnItemClickListener?) {
//            binding.txtKodemk.text = data.kodeMatkul
//            binding.txtMk.text = data.namaMatkul
//            binding.txtSksmk.text = data.sks.toString()
//            binding.txtNilaimk.text = ""
//            binding.cbMk.setOnClickListener {
//                onItemClickListener?.onClick(data)
//            }
//        }
//    }
//
//    companion object {
//        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemAllMatkul>() {
//            override fun areItemsTheSame(oldItem: ItemAllMatkul, newItem: ItemAllMatkul): Boolean {
//                return oldItem == newItem
//            }
//
//            override fun areContentsTheSame(oldItem: ItemAllMatkul, newItem: ItemAllMatkul): Boolean {
//                return oldItem.id == newItem.id
//            }
//        }
//    }
//}