package com.upnvjatim.silaturahmi.dosen.pembimbing.verifnilai

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentDetailVerifNilaiBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.daftar.MsibViewModel
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.response.DetailsPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import com.upnvjatim.silaturahmi.openFile
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.LuaranViewModel
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListLuaranAdapter

class DetailVerifNilaiFragment : Fragment() {
    private var _binding: FragmentDetailVerifNilaiBinding? = null
    private val binding get() = _binding!!

    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelPaket: PaketKonversiViewModel by activityViewModels()
    private val viewModelNilai: NilaiViewModel by activityViewModels()
    private val viewModelStatus: ChangeStatusViewModel by activityViewModels()

    private var sks: MutableLiveData<Int> = MutableLiveData()
    private var selectedPaket: ItemLookupPaketKonversi? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailVerifNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?

        toolbar()
        bindingTxt(getData!!)

        if (selectedPaket != null) {
            Log.d("cek selectedPaket !null", selectedPaket?.namaPaketKonversi.toString())
            bindingDetailPaket(selectedPaket!!, viewModelNilai.liveDataTranskrip.value)
        } else {
            viewModelPaket.getliveDataPaket(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                pageSize = null, pageNumber = null, idPendaftarMBKM = getData.id.toString(),
                idProgramProdi = null, idJenisMbkm = null, idProgram = null, statusMbkm = null)
            viewModelNilai.getliveDataTranskrip(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                getData.npm.toString())

            viewModelPaket.isLoading.observe(viewLifecycleOwner) {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
            viewModelPaket.isError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            viewModelNilai.isLoading.observe(viewLifecycleOwner) {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
            viewModelNilai.isError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            viewModelPaket.liveDataPaket.observe(viewLifecycleOwner) {
                Log.d("cek data paket", it?.data?.namaPaketKonversi.toString())
                if (it.data != null) {
                    if (it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
                        Log.d("cek pakettt", it.data.idPaketKonversi.toString())
                        selectedPaket = ItemLookupPaketKonversi(
                            createdAt = null,
                            createdBy = null,
                            deskripsi = it.data.deskripsi,
                            detail = it.data.listMatkul?.map { mk ->
                                DetailsPaketKonversi(
                                    id = mk?.id,
                                    idMatkul = mk?.idMatkul,
                                    idPaketKonversi = mk?.idPaketKonversi,
                                    kodeMatkul = mk?.kodeMatkul,
                                    namaMatkul = mk?.namaMatkul,
                                    namaPaketKonversi = mk?.namaPaketKonversi,
                                    sks = mk?.sks,
                                    updatedAt = mk?.updatedAt.toString(),
                                    updatedBy = mk?.updatedBy.toString(),
                                    createdAt = mk?.createdAt,
                                    createdBy = mk?.createdBy
                                )
                            },
                            id = it.data.idPaketKonversi,
                            idJenisMbkm = getIdPendaftar(requireContext())?.idJenisMbkm,
                            idProgram = getIdPendaftar(requireContext())?.idProgram,
                            idProgramProdi = getIdPendaftar(requireContext())?.idProgramProdi,
                            isDeleted = false,
                            kdProdi = getIdPendaftar(requireContext())?.kdPeriode,
                            namaPaketKonversi = it.data.namaPaketKonversi,
                            namaProdi = getIdPendaftar(requireContext())?.namaProdi,
                            namaProgram = getIdPendaftar(requireContext())?.namaProgram,
                            updatedAt = null, updatedBy = null
                        )
                        msibViewModel.paketData.value = selectedPaket
                    } else {
                        Log.d("cek pakt elss notnull", it.data.namaPaketKonversi.toString())
                    }
                } else {
                    Log.d("cek paket ells", it.data?.namaPaketKonversi.toString())
                }
            }
        }

        msibViewModel.paketData.observe(viewLifecycleOwner) {
            sks.value = 0
            if (it != null) {
                selectedPaket = it
                selectedPaket?.detail?.map {
                    sks.value = (sks.value?.toInt()?:0) + (it?.sks?.toInt()?:0)
                }

                Log.d("cek it paket !null", selectedPaket?.namaPaketKonversi.toString())

                bindingDetailPaket(selectedPaket!!, viewModelNilai.liveDataTranskrip.value)

            } else {
                Log.d("cek it slctdprogram null", selectedPaket?.namaPaketKonversi.toString())
                Toast.makeText(requireContext(), "Paket konversi kosong!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (getData.status != 0 && getData.status != 4 && getData.isFinish == false
            && getData.linkPenilaian != null && getData.linkPenilaian != "") {
            binding.btnSetujui.isEnabled = true
            binding.btnSetujui.setOnClickListener {
                PendaftaranDialogFragment(false, "Konfirmasi Verifikasi Nilai dan Luaran",
                    null, true, { accNilai(getData) })
                    .show(requireActivity().supportFragmentManager, "TAG ACC NILAI")
            }
        } else {
            binding.btnSetujui.isEnabled = false
        }
    }

    private fun accNilai(getData: ItemPendaftarProgram) {
        viewModelStatus.changeStatus(ChangeStatus(getData.id.toString(), false, 3), requireContext())
        viewModelStatus.status.observe(viewLifecycleOwner) {
            if (it.data == "success") {
                Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }

        viewModelStatus.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBarVerif.visibility = View.VISIBLE
            else binding.progressBarVerif.visibility = View.INVISIBLE
        }
        viewModelStatus.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun bindingDetailPaket(dataPaket: ItemLookupPaketKonversi, listTranskrip: ResponseTranskrip?) {
        binding.txtNamapaketkonversi.text = dataPaket.namaPaketKonversi
        binding.txtTotalsks.text = "Total SKS : ${sks.value.toString()}"

        Log.d("cek data", "${dataPaket.namaPaketKonversi} ; ${
            dataPaket.detail?.first()?.namaMatkul} ; ${listTranskrip?.data?.size}")
        binding.tableLayout.removeAllViews()
        binding.tableLayout.addView(binding.tableRow)

        val tableRowHeader = LayoutInflater.from(requireContext()).inflate(R.layout.tablerow_paketnilai, null)
                as TableRow

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

        dataPaket.detail?.forEach { mk ->
            if (mk?.idMatkul != null && mk.kodeMatkul != "") {
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
        }
    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        val viewmodelLuaran = ViewModelProvider(this)[LuaranViewModel::class.java]
        viewmodelLuaran.getliveDataAllLuaran(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            id = getData.id.toString()
        )

        viewmodelLuaran.liveDataAllLuaran.observe(viewLifecycleOwner) {
//            if(it.data != null) {
//                it.data.map {
//                    Log.d("cek luran", "${it?.link} ; ${it?.keterangan} ; ${it?.isDeleted}")
//                }
//                binding.rvLuaran.layoutManager =
//                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//                binding.rvLuaran.adapter = ListLuaranAdapter(it.data.filter { it?.isDeleted == false })
//                binding.emptydataLuaran.visibility = View.GONE
//            } else {
//                binding.emptydataLuaran.visibility = View.VISIBLE
//            }


            it.data?.let { data ->
                val filteredData = data.filter { it?.isDeleted == false }
                if (filteredData.isNotEmpty()) {
                    val adapter = ListLuaranAdapter(filteredData)
                    binding.rvLuaran.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvLuaran.adapter = adapter
                    adapter.submitList(filteredData)
                    binding.emptydataLuaran.visibility = View.GONE

                    var totalHeight = 0
                    for (i in 0 until adapter.itemCount) {
                        val viewHolder = adapter.createViewHolder(binding.rvLuaran, adapter.getItemViewType(i))
                        adapter.onBindViewHolder(viewHolder, i)
                        viewHolder.itemView.measure(
                            View.MeasureSpec.makeMeasureSpec(binding.rvLuaran.width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        totalHeight += viewHolder.itemView.measuredHeight + (8 * adapter.itemCount)
                    }

                    binding.rvLuaran.layoutParams.height = totalHeight
                    binding.rvLuaran.requestLayout()

                } else {
                    binding.emptydataLuaran.visibility = View.VISIBLE
                }
            } ?: run {
                binding.emptydataLuaran.visibility = View.VISIBLE
            }
        }

        viewmodelLuaran.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodelLuaran.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

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

            if(getData.linkPenilaian == null || getData.linkPenilaian == ""){
                emptydataNilai.visibility = View.VISIBLE
                cardPenilaian.visibility = View.GONE
            } else {
                emptydataNilai.visibility = View.GONE
                cardPenilaian.visibility = View.VISIBLE

                txtWaktuNilai.text = getData.tglPenilaian
                txtNilaiakhir.text = getData.nilaiAngka
                txtKetNilai.text = getData.ketPenilaian

                btnFileNilai.setOnClickListener {
                    openFile(getData.linkPenilaian, requireContext())
                }
            }

            if (getData.status != 3 && getData.isFinish == false) {
                binding.txtStatus.text = "Minta Persetujuan Nilai"
                binding.txtStatus.setTextColor(Color.WHITE)
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                binding.txtStatus.text = "Penilaian Disetujui"
                binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }

            btnLogbook.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("pendaftar", getData)
                findNavController().navigate(R.id.action_detailVerifNilaiFragment_to_listLogbookFragment, bundle)
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
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}