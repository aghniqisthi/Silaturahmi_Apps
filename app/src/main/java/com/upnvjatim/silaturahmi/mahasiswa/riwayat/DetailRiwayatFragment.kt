package com.upnvjatim.silaturahmi.mahasiswa.riwayat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentDetailRiwayatBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.DataPaketKonversi
import com.upnvjatim.silaturahmi.model.response.DataRiwayatNilai
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.RiwayatViewModel

class DetailRiwayatFragment : Fragment() {
    private var _binding: FragmentDetailRiwayatBinding? = null
    private val binding get() = _binding!!
    private val viewModelPaket: PaketKonversiViewModel by activityViewModels()
    private val viewModelRiwayat: RiwayatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar()

        val getData = arguments?.getParcelable("riwayat") as ItemPendaftarProgram?
        bindingTxt(getData!!)

        if(getData.status == 3 && getData.isFinish == true) binding.info.visibility = View.VISIBLE
        else binding.info.visibility = View.GONE

        viewModelPaket.getliveDataPaket(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            pageSize = null, pageNumber = null,
            idPendaftarMBKM = getData.id.toString(),
            idProgramProdi = null, idJenisMbkm = null, idProgram = null, statusMbkm = null,
        ).observe(viewLifecycleOwner){

            viewModelRiwayat.getliveDataRiwayatNilai(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                id = getData.id.toString(),
                status = if (getData.status == 3 && getData.isFinish == true) "all" else null
            ).observe(viewLifecycleOwner){
                bindingDetailPaket(getData, viewModelPaket.liveDataPaket.value?.data, it?.data)
            }

        }

        viewModelPaket.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        binding.apply {
            txtNamaprogram.text = getData.namaPosisiKegiatanTopik
            txtNamamitra.text = getData.namaMitra
            txtNip.text = getData.nip
            txtNamadospem.text = getData.namaPegawai
            txtProgram.text = getData.namaProgram
            txtJenismbkm.text = getData.namaJenisMbkm
            txtTglmulai.text = getData.awalKegiatan
            txtTglselesai.text = getData.akhirKegiatan
        }
    }

    private fun bindingDetailPaket(
        getData: ItemPendaftarProgram, dataPaket: DataPaketKonversi?, nilai: List<DataRiwayatNilai?>?) {

        binding.txtNamapaketkonversi.text = dataPaket?.namaPaketKonversi
        binding.txtDescpaketkonversi.text = dataPaket?.deskripsi

        binding.tableLayout.removeAllViews()
        binding.tableLayout.addView(binding.tableRow)

        val tableRowHeader = LayoutInflater.from(requireContext())
            .inflate(R.layout.tablerow_paketnilaiverif, null) as TableRow

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

        val verifHeader = tableRowHeader.findViewById<TextView>(R.id.txt_verifnilai)
        verifHeader.text = "Verifikasi Nilai"
        verifHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        binding.tableLayout.addView(tableRowHeader)

        var totalSks = 0
        nilai?.map { nl ->
            Log.d("cek nilai data",nl?.namaMatkul.toString())
            if (nl?.kodeMatkul != null && nl.kodeMatkul != "") {
                val tableRowData = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paketnilaiverif, null) as TableRow

                val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
                kodeMatkul.text = nl.kodeMatkul

                val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
                namaMatkul.text = nl.namaMatkul

                val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
                sks.text = nl.sks.toString()
                totalSks = totalSks + (nl.sks ?: 0)

                val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
                nilaiTextView.text = nl.nilaiAngka ?: " "

                val verifTextView = tableRowData.findViewById<TextView>(R.id.txt_verifnilai)
                verifTextView.text = nl.nilaiAngkaFinal ?: " "

                if(nl.idNilaiFinal.isNullOrBlank() && (getData.isFinish == true || getData.status == 1)){
                    kodeMatkul.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                    namaMatkul.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                    sks.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                    nilaiTextView.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                    verifTextView.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

                    kodeMatkul.setTextColor(Color.RED)
                    namaMatkul.setTextColor(Color.RED)
                    sks.setTextColor(Color.RED)
                    nilaiTextView.setTextColor(Color.RED)
                    verifTextView.setTextColor(Color.RED)
                }
                binding.tableLayout.addView(tableRowData)
            }
        }
        binding.txtTotalsks.text = "Total SKS : $totalSks"
    }

    private fun toolbar() {
        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}