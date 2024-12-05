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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentStThreePaketBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.DetailsPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel

class StThreePaketFragment : Fragment() {
    private var _binding: FragmentStThreePaketBinding? = null
    private val binding get() = _binding!!

    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelPaket: PaketKonversiViewModel by activityViewModels()
    private val viewModelTranskrip: NilaiViewModel by activityViewModels()

//    private var selectedPaket: ItemLookupPaketKonversi? = null
    private var sks: MutableLiveData<Int> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStThreePaketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!getIdPendaftar(requireContext())?.id.isNullOrEmpty() && msibViewModel.paketData.value == null) {
            viewModelPaket.getliveDataPaket(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                pageSize = null, pageNumber = null,
                idPendaftarMBKM = getIdPendaftar(requireContext())?.id.toString(),
                idProgramProdi = null, idJenisMbkm = null, idProgram = null, statusMbkm = null,
            )

            viewModelPaket.liveDataPaket.observe(viewLifecycleOwner) {
                Log.d("cek data paket", it?.data?.namaPaketKonversi.toString())
                if (it.data != null) {
                    if (it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
                        Log.d("cek pakettt", it.data.idPaketKonversi.toString())
                        val dataPaket = ItemLookupPaketKonversi(
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
                                    updatedAt = mk?.updatedAt.toString(), updatedBy = mk?.updatedBy.toString(),
                                    createdAt = mk?.createdAt, createdBy = mk?.createdBy
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
                        msibViewModel.paketData.value = dataPaket
                    } else {
                        Log.d("cek pakt elss notnull", it.data.namaPaketKonversi.toString())
                    }
                } else {
                    Log.d("cek paket ells", it.data?.namaPaketKonversi.toString())
                }
            }

            viewModelPaket.isError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModelTranskrip.getliveDataTranskrip(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getUser(requireContext())?.user?.username.toString()
        )

        if (msibViewModel.paketData.value != null) {
            Log.d("cek selectedPaket !null", msibViewModel.paketData.value?.namaPaketKonversi.toString())
            binding.cardPaket.visibility = View.VISIBLE
            binding.linearLayoutBtnpaket.visibility = View.GONE
            bindingDetailPaket(viewModelTranskrip.liveDataTranskrip.value!!)
            binding.btnGantipaket.setOnClickListener {
                msibViewModel.paketData.value = null
            }
        } else {
            Log.d("cek selectedPaketnull", msibViewModel.paketData.value?.namaPaketKonversi.toString())
            binding.linearLayoutBtnpaket.visibility = View.VISIBLE
            binding.cardPaket.visibility = View.GONE
            binding.btnBuatpaket.setOnClickListener {
                findNavController().navigate(R.id.action_stThreePaketFragment_to_buatPaketKonversiFragment)
            }
            binding.btnPilihpaket.setOnClickListener {
                findNavController().navigate(R.id.action_stThreePaketFragment_to_pilihPaketKonversiFragment)
            }
        }

        msibViewModel.paketData.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("cek it paket !null", it.namaPaketKonversi.toString())
                binding.cardPaket.visibility = View.VISIBLE
                binding.linearLayoutBtnpaket.visibility = View.GONE
                bindingDetailPaket(viewModelTranskrip.liveDataTranskrip.value)
                binding.btnAddmk.setOnClickListener {
                    if ((sks.value?.toInt() ?: 0) >= 20 && (sks.value?.toInt() ?: 0) > 0) {
                        Toast.makeText(requireContext(), "Jumlah SKS belum memenuhi!", Toast.LENGTH_SHORT).show()
                    } else {
                        findNavController().navigate(R.id.action_stThreePaketFragment_to_tambahMatkulFragment)
                    }
    //                    if(selectedPaket != null) {
    //                        val bundle = Bundle()
    //                        bundle.putParcelable("paket", selectedPaket)
    //                        findNavController().navigate(R.id.action_stThreePaketFragment_to_tambahMatkulFragment, bundle)
    //                    } else
                }
                binding.btnGantipaket.setOnClickListener {
                    msibViewModel.paketData.value = null
                }
            } else {
                Log.d("cek it slctdprogram null", msibViewModel.paketData.value?.namaPaketKonversi.toString())
                binding.linearLayoutBtnpaket.visibility = View.VISIBLE
                binding.cardPaket.visibility = View.GONE
                binding.btnBuatpaket.setOnClickListener {
                    findNavController().navigate(R.id.action_stThreePaketFragment_to_buatPaketKonversiFragment)
                }
                binding.btnPilihpaket.setOnClickListener {
                    findNavController().navigate(R.id.action_stThreePaketFragment_to_pilihPaketKonversiFragment)
                }
            }
        }
    }

    private fun bindingDetailPaket(listTranskrip: ResponseTranskrip?) {
        val dataPaket = msibViewModel.paketData.value
        binding.txtNamapaket.text = dataPaket?.namaPaketKonversi
        binding.txtDescpaketkonversi.text = dataPaket?.deskripsi

        binding.tableLayout.removeAllViews()
        binding.tableLayout.addView(binding.tableRow)

//        val tableRow = LayoutInflater.from(requireContext())
//            .inflate(R.layout.tablerow_paketnilaiaksi, null) as TableRow
//        binding.tableLayout.removeAllViews()
//        binding.tableLayout.addView(binding.tableRow)

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
        dataPaket?.detail?.forEach { mk ->
            Log.d("cek datapake detail", "${mk?.namaMatkul} ; ${mk?.kodeMatkul}")
            if (mk != null && mk.kodeMatkul != null && mk.kodeMatkul != "") {
                val tableRowData = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paketnilaiaksi, null) as TableRow

                val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
                kodeMatkul.text = mk.kodeMatkul

                val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
                namaMatkul.text = mk.namaMatkul

                val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
                sks.text = mk.sks

                val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
                val nilai = listTranskrip?.data?.firstOrNull { it?.kode_mata_kuliah == mk.kodeMatkul }?.nilai
                nilaiTextView.text = nilai ?: " "

                val btnAksi = tableRowData.findViewById<ImageButton>(R.id.btn_aksi)
                btnAksi.setOnClickListener {
                    Log.d("cek btnaksi clicker", "${mk.namaPaketKonversi} ; ${mk.namaMatkul}")

                    msibViewModel.paketData.value = ItemLookupPaketKonversi(
                        createdAt = null,
                        createdBy = null,
                        deskripsi = dataPaket.deskripsi,
                        detail = dataPaket.detail?.map { pk ->
                            if (mk.kodeMatkul != pk?.kodeMatkul && mk.namaMatkul != pk?.namaMatkul)
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
                            else null
                        },
                        id = dataPaket.id,
                        idJenisMbkm = getIdPendaftar(requireContext())?.idJenisMbkm,
                        idProgram = getIdPendaftar(requireContext())?.idProgram,
                        idProgramProdi = getIdPendaftar(requireContext())?.idProgramProdi,
                        kdProdi = getIdPendaftar(requireContext())?.kdPeriode,
                        namaPaketKonversi = dataPaket.namaPaketKonversi,
                        namaProdi = getIdPendaftar(requireContext())?.namaProdi,
                        namaProgram = getIdPendaftar(requireContext())?.namaProgram,
                        updatedAt = null, updatedBy = null, isDeleted = false,
                        )

                    msibViewModel.paketData.observe(viewLifecycleOwner){
                        Log.d("cek all paket msib", "${it?.namaPaketKonversi} ; ${
                            it?.detail?.map { it?.namaMatkul }}")
                    }
                }
                binding.tableLayout.addView(tableRowData)
            }

            sks.value = (sks.value?.toInt()?:0) + (mk?.sks?.toIntOrNull()?:0)
        }
        binding.txtTotalsks.text = "Total SKS : ${sks.value}"
    }
}
