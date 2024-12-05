package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentStTwoProgramBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemLookupProgram
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.saveIdPendaftar
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.RiwayatViewModel

class StTwoProgramFragment : Fragment() {
    private var selectedProgram: ItemLookupProgram? = null
    private var _binding: FragmentStTwoProgramBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelPendaftarProgram: PendaftarProgramViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStTwoProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModelPendaftarProgram.getliveDataActiveProgram(
//            "bearer ${getUser(requireContext())?.token?.AccessToken}",
//            getUser(requireContext())?.user?.idSiamikMahasiswa.toString()
//        )

        if(msibViewModel.jenisMsib.value == "msib") binding.cardStatus.visibility = View.GONE
        else binding.cardStatus.visibility = View.VISIBLE

        val viewmodelRiwayat = ViewModelProvider(this)[RiwayatViewModel::class.java]
        viewmodelRiwayat.getliveDataRiwayat(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            pageNumber = 1,
            pageSize = 1
        )
        viewmodelRiwayat.liveDataRiwayat.observe(viewLifecycleOwner) {
            Log.d("cek data pendaftar", it?.data?.items?.firstOrNull()?.link.toString())
            saveIdPendaftar(
                ItemPendaftarProgram(
                    akhirKegiatan = it.data?.items?.firstOrNull()?.akhirKegiatan,
                    akhirPendaftaran = it.data?.items?.firstOrNull()?.akhirPendaftaran,
                    awalKegiatan = it.data?.items?.firstOrNull()?.awalKegiatan,
                    awalPendaftaran = it.data?.items?.firstOrNull()?.awalPendaftaran,
                    createdAt = it.data?.items?.firstOrNull()?.createdAt,
                    createdBy = it.data?.items?.firstOrNull()?.createdBy,
                    id = it.data?.items?.firstOrNull()?.id,
                    idJenisMbkm = it.data?.items?.firstOrNull()?.idJenisMbkm,
                    idJenisMitra = it.data?.items?.firstOrNull()?.idJenisMitra,
                    idMitra = it.data?.items?.firstOrNull()?.idMitra,
                    idMitraTerlibatProgram = it.data?.items?.firstOrNull()?.idMitraTerlibatProgram,
                    idPegawai = it.data?.items?.firstOrNull()?.idPegawai,
                    idPeriode = it.data?.items?.firstOrNull()?.idPeriode,
                    idPosisiKegiatanTopik = it.data?.items?.firstOrNull()?.idPosisiKegiatanTopik,
                    idProgram = it.data?.items?.firstOrNull()?.idProgram,
                    idProgramDitawarkan = it.data?.items?.firstOrNull()?.idProgramDitawarkan,
                    idProgramProdi = it.data?.items?.firstOrNull()?.idProgramProdi,
                    idSiamikMahasiswa = it.data?.items?.firstOrNull()?.idSiamikMahasiswa,
                    isDeleted = it.data?.items?.firstOrNull()?.isDeleted,
                    isFinish = it.data?.items?.firstOrNull()?.isFinish,
                    isMagangMandiri = it.data?.items?.firstOrNull()?.isMagangMandiri,
                    kdJenisMbkm = it.data?.items?.firstOrNull()?.kdJenisMbkm,
                    kdPeriode = it.data?.items?.firstOrNull()?.kdPeriode,
                    ketPenilaian = it.data?.items?.firstOrNull()?.ketPenilaian,
                    kuotaPerMitra = it.data?.items?.firstOrNull()?.kuotaPerMitra,
                    link = it.data?.items?.firstOrNull()?.link,
                    linkPenilaian = it.data?.items?.firstOrNull()?.linkPenilaian,
                    maxKrs = it.data?.items?.firstOrNull()?.maxKrs,
                    maxSks = it.data?.items?.firstOrNull()?.maxSks,
                    nama = it.data?.items?.firstOrNull()?.nama,
                    namaJenisMbkm = it.data?.items?.firstOrNull()?.namaJenisMbkm,
                    namaJenisMitra = it.data?.items?.firstOrNull()?.namaJenisMitra,
                    namaMitra = it.data?.items?.firstOrNull()?.namaMitra,
                    namaPegawai = it.data?.items?.firstOrNull()?.namaPegawai,
                    namaPosisiKegiatanTopik = it.data?.items?.firstOrNull()?.namaPosisiKegiatanTopik,
                    namaProdi = it.data?.items?.firstOrNull()?.namaProdi,
                    namaProgram = it.data?.items?.firstOrNull()?.namaProgram,
                    nilaiAngka = it.data?.items?.firstOrNull()?.nilaiAngka,
                    nip = it.data?.items?.firstOrNull()?.nip,
                    npm = it.data?.items?.firstOrNull()?.npm,
                    semester = it.data?.items?.firstOrNull()?.semester,
                    status = it.data?.items?.firstOrNull()?.status,
                    statusApproval = it.data?.items?.firstOrNull()?.statusApproval,
                    statusMbkm = it.data?.items?.firstOrNull()?.statusMbkm,
                    tahun = it.data?.items?.firstOrNull()?.tahun,
                    tglPenilaian = it.data?.items?.firstOrNull()?.tglPenilaian,
                    updatedAt = it.data?.items?.firstOrNull()?.updatedAt,
                    updatedBy = it.data?.items?.firstOrNull()?.updatedBy
                ),
                requireContext()
            )

            if (it.data != null && (it.data.items?.size?:0) > 0) {
                    if (it.data.items?.firstOrNull() != null
                        && it.data.items.firstOrNull()?.idProgram != "") {
                        Log.d("cek pendaftarrr", it.data.items?.firstOrNull()?.idProgram.toString())
                        selectedProgram = ItemLookupProgram(
                            akhirKegiatan = it.data.items?.firstOrNull()?.akhirKegiatan,
                            akhirPendaftaran = it.data.items?.firstOrNull()?.akhirPendaftaran,
                            awalKegiatan = it.data.items?.firstOrNull()?.awalKegiatan,
                            awalPendaftaran = it.data.items?.firstOrNull()?.awalPendaftaran,
                            createdAt = it.data.items?.firstOrNull()?.createdAt,
                            createdBy = it.data.items?.firstOrNull()?.createdBy,
                            id = it.data?.items?.firstOrNull()?.id,
                            idJenisMbkm = it.data?.items?.firstOrNull()?.idJenisMbkm,
                            idMitra = it.data?.items?.firstOrNull()?.idMitra,
                            idPeriode = it.data?.items?.firstOrNull()?.idPeriode,
                            idPosisiKegiatanTopik = it.data?.items?.firstOrNull()?.idPosisiKegiatanTopik,
                            idProgram = it.data?.items?.firstOrNull()?.idProgram,
                            idProgramDitawarkan = it.data?.items?.firstOrNull()?.idProgramDitawarkan,
                            isDeleted = it.data?.items?.firstOrNull()?.isDeleted,
                            kodePeriode = it.data?.items?.firstOrNull()?.kdPeriode,
                            kuotaPerMitra = it.data?.items?.firstOrNull()?.kuotaPerMitra,
                            namaJenisMbkm = it.data?.items?.firstOrNull()?.namaJenisMbkm,
                            namaMitra = it.data?.items?.firstOrNull()?.namaMitra,
                            namaPosisiKegiatanTopik = it.data?.items?.firstOrNull()?.namaPosisiKegiatanTopik,
                            namaProgram = it.data?.items?.firstOrNull()?.namaProgram,
                            semester = it.data?.items?.firstOrNull()?.semester,
                            statusApproval = it.data?.items?.firstOrNull()?.statusApproval,
                            tahun = it.data?.items?.firstOrNull()?.tahun,
                            updatedAt = it.data?.items?.firstOrNull()?.updatedAt,
                            updatedBy = it.data?.items?.firstOrNull()?.updatedBy,
                            email = null, alamat = null, jenisKelamin = null, kuota = null, nomorWA = null,
                            kuotaProgramDitawarkan = null, namaPic = null, nik = null, nomorTelegram = null,
                        )
                        msibViewModel.programData.value = selectedProgram

                        if(msibViewModel.jenisMsib.value == "mandiri"){
                            if(it.data?.items?.firstOrNull()?.status == 0){
                                binding.txtStatus.text = "Program Anda Belum Diverifikasi"
                                binding.txtStatus.setTextColor(Color.BLACK)
                                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FFCC00"))
                                binding.btnGanti.visibility = View.VISIBLE
                            } else {
                                binding.txtStatus.text = "Program Anda Telah Disetujui"
                                binding.txtStatus.setTextColor(Color.WHITE)
                                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#34C759"))
                                binding.btnGanti.visibility = View.GONE
                            }
                        } else {
                            binding.cardStatus.visibility = View.GONE
                            if(it.data?.items?.firstOrNull()?.status == 0) binding.btnGanti.visibility = View.VISIBLE
                            else binding.btnGanti.visibility = View.GONE
                        }

                    } else {
                        Log.d("cek pendaftar elss notnull", it.data?.items?.firstOrNull()?.idProgram.toString())
                    }
                } else {
                    Log.d("cek pendaftar ells", it.data?.items?.firstOrNull()?.idProgram.toString())
                }
        }


        viewModelPendaftarProgram.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        if(selectedProgram != null){
            Log.d("cek selectedprogram !null", selectedProgram?.namaPosisiKegiatanTopik.toString())
            binding.cardDetailprogram.visibility = View.VISIBLE
            binding.btnPilihprogram.visibility = View.GONE
            bindingDetailProgram(selectedProgram!!)
            binding.btnGanti.setOnClickListener {
                findNavController().navigate(R.id.action_stTwoProgramFragment_to_pilihProgramFragment)
            }
        } else {
            Log.d("cek selectedprogramnull", selectedProgram?.namaPosisiKegiatanTopik.toString())
            binding.cardDetailprogram.visibility = View.GONE
            binding.btnPilihprogram.visibility = View.VISIBLE
            binding.btnPilihprogram.setOnClickListener {
                findNavController().navigate(R.id.action_stTwoProgramFragment_to_pilihProgramFragment)
            }
        }

        msibViewModel.programData.observe(viewLifecycleOwner){ pr ->
            if(pr != null){
                selectedProgram = pr
                Log.d("cek it slctdprogram !null", selectedProgram?.namaPosisiKegiatanTopik.toString())
                binding.cardDetailprogram.visibility = View.VISIBLE
                binding.btnPilihprogram.visibility = View.GONE
                bindingDetailProgram(selectedProgram!!)
                binding.btnGanti.visibility = View.VISIBLE
                binding.btnGanti.setOnClickListener {
                    findNavController().navigate(R.id.action_stTwoProgramFragment_to_pilihProgramFragment)
                }

                if(msibViewModel.jenisMsib.value == "mandiri"){
                        binding.txtStatus.text = "Program Anda Belum Diverifikasi"
                        binding.txtStatus.setTextColor(Color.BLACK)
                        binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FFCC00"))
                }

            } else {
                Log.d("cek it slctdprogram null", selectedProgram?.namaPosisiKegiatanTopik.toString())
                binding.cardDetailprogram.visibility = View.GONE
                binding.btnPilihprogram.visibility = View.VISIBLE
                binding.btnPilihprogram.setOnClickListener {
                    findNavController().navigate(R.id.action_stTwoProgramFragment_to_pilihProgramFragment)
                }
            }
        }
    }

    private fun bindingDetailProgram(dataProgram: ItemLookupProgram) {
        binding.txtPosisi.text = dataProgram.namaPosisiKegiatanTopik
        binding.txtProgram.text = dataProgram.namaProgram
        binding.txtJenis.text = dataProgram.namaJenisMbkm
        binding.txtMitra.text = dataProgram.namaMitra
        binding.txtPic.text = dataProgram.namaPic
        binding.txtTgldftar.text = dataProgram.awalPendaftaran
        binding.txtTgldftartutup.text = dataProgram.akhirPendaftaran
        binding.txtTglmulaikeg.text = dataProgram.awalKegiatan
        binding.txtTglakhirkeg.text = dataProgram.akhirKegiatan
    }

}