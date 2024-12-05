package com.upnvjatim.silaturahmi.dosen.pic

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentDetailRevisiNilaiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.openFile
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel

class DetailRevisiNilaiFragment : Fragment() {
    private var _binding: FragmentDetailRevisiNilaiBinding? = null
    private val binding get() = _binding!!

    private val viewModelPaket: PaketKonversiViewModel by activityViewModels()
    private val viewModelTranskrip: NilaiViewModel by activityViewModels()

    private var sks: MutableLiveData<Int> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRevisiNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?

        toolbar()

        if (getData != null) {
            bindingTxt(getData)

//            viewModelPaket.getliveDataPaket(
//                "bearer ${getUser(requireContext())?.token?.AccessToken}",
//                pageSize = null, pageNumber = null, idPendaftarMBKM = getData.id.toString(),
//                idProgramProdi = null, idJenisMbkm = null, idProgram = null, statusMbkm = null,
//            )
//            viewModelTranskrip.getliveDataTranskrip(
//                "bearer ${getUser(requireContext())?.token?.AccessToken}",
//                getData.npm.toString()
//            )
//
//            viewModelPaket.isLoading.observe(viewLifecycleOwner) {
//                if (it) binding.progressBar.visibility = View.VISIBLE
//                else binding.progressBar.visibility = View.INVISIBLE
//            }
//            viewModelPaket.isError.observe(viewLifecycleOwner) {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//            }
//            viewModelTranskrip.isLoading.observe(viewLifecycleOwner) {
//                if (it) binding.progressBar.visibility = View.VISIBLE
//                else binding.progressBar.visibility = View.INVISIBLE
//            }
//            viewModelTranskrip.isError.observe(viewLifecycleOwner) {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//            }

//            viewModelPaket.liveDataPaket.observe(viewLifecycleOwner) {
//                Log.d("cek data paket", it?.data?.namaPaketKonversi.toString())
//                if (it.data != null && it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
//                    Log.d("cek pakettt", it.data?.idPaketKonversi.toString())
//                    sks.value = 0
//                    it.data.listMatkul?.map {
//                        sks.value = (sks.value?.toInt() ?: 0) + (it?.sks?.toInt() ?: 0)
//                    }
                    bindingDetailPaket(getData) //it.data, viewModelTranskrip.liveDataTranskrip.value)
//                } else {
//                    Log.d("cek it slctdprogram null", it.data?.namaPaketKonversi.toString())
//                    Toast.makeText(requireContext(), "Paket konversi kosong!", Toast.LENGTH_SHORT).show()
//                }
//            }
        }

        /// TODO : testing run
        if (getData?.status == 1) {
            binding.btnSetujui.isEnabled = true
            binding.btnSetujui.setOnClickListener {
                PendaftaranDialogFragment(false, "Konfirmasi Revisi Penilaian", null,
                    true, { accRevisi(getData) }).show(
                    requireActivity().supportFragmentManager, "TAG DETAIL ACC PROGRAM TIMMBKM")
            }
        } else {
            binding.btnSetujui.isEnabled = false
        }

        binding.btnBatal.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun accRevisi(getData: ItemPendaftarProgram) {
        val viewmodel = ViewModelProvider(this)[ChangeStatusViewModel::class.java]

        viewmodel.changeStatus(ChangeStatus(getData.id.toString(), false, 3), requireContext())
        viewmodel.status.observe(viewLifecycleOwner) {
            if (it.data == "success") {
                Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()
                binding.btnSetujui.isEnabled = false
            }
            else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBarVerif.visibility = View.VISIBLE
            else binding.progressBarVerif.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun bindingDetailPaket(getData: ItemPendaftarProgram){ //dataPaket: DataPaketKonversi, listTranskrip: ResponseTranskrip?) {

        val viewmodel = ViewModelProvider(this)[PaketKonversiViewModel::class.java]
        viewmodel.getliveDataPaket(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftarMBKM = getData.id.toString(),
            idProgramProdi = getData.idProgramProdi.toString(),
            idJenisMbkm = getData.idJenisMbkm.toString(),
            idProgram = getData.idProgram.toString(),
            statusMbkm = getData.statusMbkm.toString()
        ).observe(viewLifecycleOwner) {
            binding.txtNamapaketkonversi.text = it.data?.namaPaketKonversi

            Log.d("cek data", "${it.data?.namaPaketKonversi} ; ${it.data?.listMatkul?.size}")

            binding.tableLayout.removeAllViews()
            binding.tableLayout.addView(binding.tableRow)
            val tableRow = LayoutInflater.from(requireContext())
                .inflate(R.layout.tablerow_paketnilaiverif, null) as TableRow

            val a = tableRow.findViewById<TextView>(R.id.txt_kodemk)
            a.text = "Kode"
            a.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

            val b = tableRow.findViewById<TextView>(R.id.txt_mk)
            b.text = "Mata Kuliah"
            b.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

            val c = tableRow.findViewById<TextView>(R.id.txt_nilaimk)
            c.text = "Nilai"
            c.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

            val e = tableRow.findViewById<TextView>(R.id.txt_verifnilai)
            e.text = "Verifikasi\nNilai"
            e.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

            val d = tableRow.findViewById<TextView>(R.id.txt_sksmk)
            d.text = "SKS"
            d.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

            binding.tableLayout.addView(tableRow)

            sks.value = 0
            it.data?.listMatkul?.map {
                val tableRow = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paketnilaiverif, null) as TableRow
                tableRow.findViewById<TextView>(R.id.txt_kodemk).text = it?.kodeMatkul
                tableRow.findViewById<TextView>(R.id.txt_mk).text = it?.namaMatkul
                tableRow.findViewById<TextView>(R.id.txt_nilaimk).text = it?.nilaiAngka
                tableRow.findViewById<TextView>(R.id.txt_verifnilai).text = it?.nilaiAngkaFinal
                tableRow.findViewById<TextView>(R.id.txt_sksmk).text = it?.sks

                sks.value = (sks.value?:0) + (it?.sks?.toInt()?:0)
                binding.tableLayout.addView(tableRow)
            }
            binding.txtTotalsks.text = "Total SKS : ${sks.value.toString()}"
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }


    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        binding.apply {
            txtNpm.text = getData.npm
            txtNama.text = getData.nama
            txtNamaprodi.text = getData.namaProdi
            txtNip.text = if (getData.nip.isNullOrEmpty()) "-" else getData.nip
            txtNamadospem.text = if (getData.namaPegawai.isNullOrEmpty()) "-" else getData.namaPegawai
            txtPosisi.text = getData.namaPosisiKegiatanTopik
            txtNamamitra.text = getData.namaMitra
            txtNamaprogram.text = getData.namaProgram
            txtJenismbkm.text = getData.namaJenisMbkm

            btnFilebuktidaftar.setOnClickListener {
                openFile(getData.link.toString(), requireContext())
            }

            if (getData.status == 1) {
                binding.txtStatus.text = "Menunggu Persetujuan Revisi"
                binding.txtStatus.setTextColor(Color.WHITE)
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                binding.txtStatus.text = "Persetujuan Revisi Disetujui"
                binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }
        }
    }

    private fun toolbar() {
        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

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