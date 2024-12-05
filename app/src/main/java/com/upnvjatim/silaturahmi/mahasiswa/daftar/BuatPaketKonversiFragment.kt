package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentBuatPaketKonversiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.DetailsPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel

class BuatPaketKonversiFragment : Fragment() {
    private var _binding: FragmentBuatPaketKonversiBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private var sks: MutableLiveData<Int> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuatPaketKonversiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.editJurusan.setText("S1 | ${getUser(requireContext())?.user?.prodi?.namaProdi}")

        binding.btnTambahmk.setOnClickListener {
            msibViewModel.paketData.value = ItemLookupPaketKonversi(
                createdAt = null,
                createdBy = null,
                deskripsi = binding.editDescpaket.text.toString(),
                detail = msibViewModel.paketData.value?.detail?.map { pk ->
                    DetailsPaketKonversi(
                        id = pk?.id,
                        idMatkul = pk?.idMatkul,
                        idPaketKonversi = pk?.idPaketKonversi,
                        kodeMatkul = pk?.kodeMatkul,
                        namaMatkul = pk?.namaMatkul,
                        namaPaketKonversi = pk?.namaPaketKonversi,
                        sks = pk?.sks,
                        updatedAt = pk?.updatedAt, updatedBy = pk?.updatedBy,
                        createdAt = pk?.createdAt, createdBy = pk?.createdBy
                    )
                },
                id = null,
                idJenisMbkm = msibViewModel.paketData.value?.idJenisMbkm,
                idProgram = msibViewModel.paketData.value?.idProgram,
                idProgramProdi = msibViewModel.paketData.value?.idProgramProdi,
                isDeleted = false,
                kdProdi = msibViewModel.paketData.value?.kdProdi,
                namaPaketKonversi = binding.editNamapaket.text.toString(),
                namaProdi = msibViewModel.paketData.value?.namaProdi,
                namaProgram = msibViewModel.paketData.value?.namaProgram,
                updatedAt = null, updatedBy = null
            )
            findNavController().navigate(R.id.action_buatPaketKonversiFragment_to_tambahMatkulFragment)
        }

        val viewmodelTranskrip = ViewModelProvider(this)[NilaiViewModel::class.java]
        viewmodelTranskrip.getliveDataTranskrip(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getUser(requireContext())?.user?.username.toString()
        )

        msibViewModel.paketData.observe(viewLifecycleOwner) {
            if (it?.detail != null) bindingDetailPaket(
                it.detail,
                viewmodelTranskrip.liveDataTranskrip.value
            )
        }

        binding.btnSimpan.setOnClickListener {
            msibViewModel.paketData.value = ItemLookupPaketKonversi(
                createdAt = null,
                createdBy = null,
                deskripsi = binding.editDescpaket.text.toString(),
                detail = msibViewModel.paketData.value?.detail?.map { pk ->
                    DetailsPaketKonversi(
                        id = pk?.id,
                        idMatkul = pk?.idMatkul,
                        idPaketKonversi = pk?.idPaketKonversi,
                        kodeMatkul = pk?.kodeMatkul,
                        namaMatkul = pk?.namaMatkul,
                        namaPaketKonversi = pk?.namaPaketKonversi,
                        sks = pk?.sks,
                        updatedAt = pk?.updatedAt, updatedBy = pk?.updatedBy,
                        createdAt = pk?.createdAt, createdBy = pk?.createdBy
                    )
                },
                id = null,
                idJenisMbkm = msibViewModel.paketData.value?.idJenisMbkm,
                idProgram = msibViewModel.paketData.value?.idProgram,
                idProgramProdi = msibViewModel.paketData.value?.idProgramProdi,
                isDeleted = false,
                kdProdi = msibViewModel.paketData.value?.kdProdi,
                namaPaketKonversi = binding.editNamapaket.text.toString(),
                namaProdi = msibViewModel.paketData.value?.namaProdi,
                namaProgram = msibViewModel.paketData.value?.namaProgram,
                updatedAt = null, updatedBy = null
            )
            if ((sks.value?.toInt() ?: 0) > 20 && (sks.value?.toInt() ?: 0) < 0) {
                Toast.makeText(requireContext(), "Jumlah SKS belum memenuhi!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                findNavController().popBackStack()  // Navigate back to chooseProgramFragment
            }
        }
    }


    private fun bindingDetailPaket(
        listMk: List<DetailsPaketKonversi?>?,
        listTranskrip: ResponseTranskrip?
    ) {
        binding.tableLayout.removeAllViews()
        binding.tableLayout.addView(binding.tableRow)

        Log.d("cek listmk ${listMk?.size}", "${listMk?.map { "${it?.idMatkul} - ${it?.kodeMatkul} - ${it?.namaMatkul}"}}")

        val tableRowHeader = LayoutInflater.from(requireContext())
            .inflate(R.layout.tablerowheader_paketnilaiaksi, null) as TableRow

        val kodeMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_kodemk)
        kodeMatkulHeader.text = "Kode"
        kodeMatkulHeader.typeface = ResourcesCompat.getFont(
            requireContext(),
            R.font.poppins_semibold
        )

        val namaMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_mk)
        namaMatkulHeader.text = "Mata Kuliah"
        namaMatkulHeader.typeface = ResourcesCompat.getFont(
            requireContext(),
            R.font.poppins_semibold
        )

        val sksHeader = tableRowHeader.findViewById<TextView>(R.id.txt_sksmk)
        sksHeader.text = "SKS"
        sksHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        val nilaiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_nilaimk)
        nilaiHeader.text = "Nilai"
        nilaiHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        val aksiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_aksimk)
        aksiHeader.text = "Aksi"
        aksiHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

        binding.tableLayout.addView(tableRowHeader)

        sks.value = 0
        listMk?.forEach { mk ->
            if ( // mk?.idMatkul != null &&
                mk != null) {
                val tableRowData = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paketnilaiaksi, null) as TableRow

                val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
                kodeMatkul.text = mk.kodeMatkul

                val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
                namaMatkul.text = mk.namaMatkul

                val sksTxt = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
                sksTxt.text = mk.sks

                val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
                val nilai =
                    listTranskrip?.data?.firstOrNull { it?.kode_mata_kuliah == mk.kodeMatkul }?.nilai
                nilaiTextView.text = nilai ?: " "

                sks.value = (sks.value?.toInt() ?: 0) + (mk.sks?.toInt() ?: 0)

                val btnAksi = tableRowData.findViewById<ImageButton>(R.id.btn_aksi)
                btnAksi.setOnClickListener {

                    msibViewModel.paketData.value = ItemLookupPaketKonversi(
                        createdAt = null,
                        createdBy = null,
                        deskripsi = binding.editDescpaket.text.toString(),
                        detail = listMk.filter {
                            mk.kodeMatkul != it?.kodeMatkul
                                    && mk.idMatkul != it?.idMatkul
                                    && mk.namaMatkul != it?.namaMatkul
                        } .mapNotNull { pk ->
//                            if (
//                                mk.kodeMatkul != pk?.kodeMatkul && mk.idMatkul != pk?.idMatkul)
//                            .filter {
//                                  ) {
//                             }
                            Log.d(
                                "cek btnaksi clicker", "${mk.namaPaketKonversi} ; ${
                                    mk.kodeMatkul
                                } == ${pk?.kodeMatkul} ; ${mk.idMatkul} == ${
                                    pk?.idMatkul
                                } ; ${mk.namaMatkul} == ${pk?.namaMatkul}"
                            )

                            DetailsPaketKonversi(
                                id = pk?.id,
                                idMatkul = pk?.idMatkul,
                                idPaketKonversi = pk?.idPaketKonversi,
                                kodeMatkul = pk?.kodeMatkul,
                                namaMatkul = pk?.namaMatkul,
                                namaPaketKonversi = pk?.namaPaketKonversi,
                                sks = pk?.sks,
                                updatedAt = pk?.updatedAt, updatedBy = pk?.updatedBy,
                                createdAt = pk?.createdAt, createdBy = pk?.createdBy
                            )
//                            } else null
                        },
                        id = null,
                        idJenisMbkm = msibViewModel.paketData.value?.idJenisMbkm,
                        idProgram = msibViewModel.paketData.value?.idProgram,
                        idProgramProdi = msibViewModel.paketData.value?.idProgramProdi,
                        isDeleted = false,
                        kdProdi = msibViewModel.paketData.value?.kdProdi,
                        namaPaketKonversi = binding.editNamapaket.text.toString(),
                        namaProdi = msibViewModel.paketData.value?.namaProdi,
                        namaProgram = msibViewModel.paketData.value?.namaProgram,
                        updatedAt = null, updatedBy = null
                    )

                    msibViewModel.paketData.observe(viewLifecycleOwner) {
                        Log.d(
                            "cek all paket msib",
                            "${it?.namaPaketKonversi} ; ${it?.detail?.map { it?.namaMatkul }}"
                        )
                    }
                }
                binding.tableLayout.addView(tableRowData)
                sks.observe(viewLifecycleOwner) {
                    binding.txtTotalsks.text = "Total SKS : ${it}"
                }
            }
        }
    }
}