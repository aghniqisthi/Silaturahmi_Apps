package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentStFourKonfirmasiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemLookupProgram
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel

class StFourKonfirmasiFragment : Fragment() {
    private var _binding: FragmentStFourKonfirmasiBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelTranskrip: NilaiViewModel by activityViewModels()
    private var sks: MutableLiveData<Int> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStFourKonfirmasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelTranskrip.getliveDataTranskrip(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getUser(requireContext())?.user?.username.toString()
        )

        bindingProgramDetail(msibViewModel.programData.value)

        bindingPaketDetail(msibViewModel.paketData.value, viewModelTranskrip.liveDataTranskrip.value)
    }


    private fun bindingProgramDetail(getData: ItemLookupProgram?) {
        binding.txtPosisi.text = getData?.namaPosisiKegiatanTopik
        binding.txtProgram.text = getData?.namaProgram
        binding.txtJenis.text = getData?.namaJenisMbkm
        binding.txtMitra.text = getData?.namaMitra
        binding.txtTglmulaikeg.text = getData?.awalKegiatan
        binding.txtTglakhirkeg.text = getData?.akhirKegiatan
    }

    private fun bindingPaketDetail(dataPaket: ItemLookupPaketKonversi?, listTranskrip: ResponseTranskrip?) {
        binding.txtNamapaket.text = dataPaket?.namaPaketKonversi
        binding.txtDescpaketkonversi.text = dataPaket?.deskripsi

        binding.tableLayout.removeAllViews()
        binding.tableLayout.addView(binding.tableRow)

        val tableRowHeader = LayoutInflater.from(requireContext())
            .inflate(R.layout.tablerow_paketnilai, null) as TableRow

        val kodeMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_kodemk)
        kodeMatkulHeader.text = "Kode"
        kodeMatkulHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        val namaMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_mk)
        namaMatkulHeader.text = "Mata Kuliah"
        namaMatkulHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        val sksHeader = tableRowHeader.findViewById<TextView>(R.id.txt_sksmk)
        sksHeader.text = "SKS"
        sksHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        val nilaiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_nilaimk)
        nilaiHeader.text = "Nilai"
        nilaiHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        binding.tableLayout.addView(tableRowHeader)

        sks.value = 0
        dataPaket?.detail?.forEach { mk ->
            if (mk != null && mk.kodeMatkul != null && mk.kodeMatkul != "") {
                val tableRowData = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paketnilai, null) as TableRow

                val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
                kodeMatkul.text = mk.kodeMatkul

                val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
                namaMatkul.text = mk.namaMatkul

                val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
                sks.text = mk.sks

                val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
                val nilai = listTranskrip?.data?.firstOrNull { it?.kode_mata_kuliah == mk.kodeMatkul }?.nilai
                nilaiTextView.text = nilai ?: " "

                binding.tableLayout.addView(tableRowData)
            }
            sks.value = (sks.value?.toInt()?:0) + (mk?.sks?.toIntOrNull()?:0)
        }
        binding.txtTotalsks.text = "Total SKS : ${sks.value}"
    }



}