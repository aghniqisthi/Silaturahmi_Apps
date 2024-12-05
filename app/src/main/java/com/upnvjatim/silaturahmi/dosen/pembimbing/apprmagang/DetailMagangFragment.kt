package com.upnvjatim.silaturahmi.dosen.pembimbing.apprmagang

import android.graphics.Color
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentDetailMagangBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.daftar.MsibViewModel
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.request.ListMatkul
import com.upnvjatim.silaturahmi.model.request.PostPaketKonversi
import com.upnvjatim.silaturahmi.model.response.DetailsPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import com.upnvjatim.silaturahmi.openFile
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.DaftarMsibViewModel
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DetailMagangFragment : Fragment() {
    private var _binding: FragmentDetailMagangBinding? = null
    private val binding get() = _binding!!

    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelPaket: PaketKonversiViewModel by activityViewModels()
    private val viewModelTranskrip: NilaiViewModel by activityViewModels()
    private val viewModelDaftarMsib: DaftarMsibViewModel by activityViewModels()
    private val viewModelChangeStatus: ChangeStatusViewModel by activityViewModels()

    private var sks: MutableLiveData<Int> = MutableLiveData()
//    private var selectedPaket: ItemLookupPaketKonversi? = null
    private var initialDetailList: List<DetailsPaketKonversi?>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMagangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?
        val viewmodel = ViewModelProvider(this)[ChangeStatusViewModel::class.java]

        toolbar()
        bindingTxt(getData!!)

//        if (msibViewModel.paketData.value != null) {
//            Log.d("cek selectedPaket !null", msibViewModel.paketData.value?.namaPaketKonversi.toString())
//            bindingDetailPaket(
//                getData.status ?: -1,
//                getData.isFinish ?: false,
////                selectedPaket!!,
//                viewModelTranskrip.liveDataTranskrip.value
//            )
//        }
//        else {
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
//
//            viewModelPaket.liveDataPaket.observe(viewLifecycleOwner) {
//                if (it.data != null && it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
//                        Log.d("cek pakettt", it.data.idPaketKonversi.toString())
//                        val selectedPaket = ItemLookupPaketKonversi(
//                            createdAt = null, createdBy = null,
//                            deskripsi = it.data.deskripsi,
//                            detail = it.data.listMatkul?.map { mk ->
//                                DetailsPaketKonversi(
//
//                        msibViewModel.paketData.value = selectedPaket
//                } else {
//                    Log.d("cek paket ells", it.data?.namaPaketKonversi.toString())
//                }
//            }
//        }

        if (getData != null && msibViewModel.paketData.value == null) {
            viewModelPaket.getliveDataPaket(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                pageSize = null, pageNumber = null,
                idPendaftarMBKM = getData.id.toString(),
                idProgramProdi = null, idJenisMbkm = null, idProgram = null, statusMbkm = null)

            viewModelPaket.liveDataPaket.observe(viewLifecycleOwner) {
                Log.d("cek data paket", it?.data?.namaPaketKonversi.toString())
                if (it.data != null && it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
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
                            idJenisMbkm = getData.idJenisMbkm,
                            idProgram = getData.idProgram,
                            idProgramProdi = getData.idProgramProdi,
                            kdProdi = getData.kdPeriode,
                            namaPaketKonversi = it.data.namaPaketKonversi,
                            namaProdi = getData.namaProdi,
                            namaProgram = getData.namaProgram,
                            updatedAt = null, updatedBy = null, isDeleted = false)

                        msibViewModel.paketData.value = dataPaket
                    initialDetailList = dataPaket.detail
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
            getData.npm.toString()
        )

        if (msibViewModel.paketData.value != null) {
            Log.d("cek selectedPaket !null", msibViewModel.paketData.value?.namaPaketKonversi.toString())
            bindingDetailPaket(viewModelTranskrip.liveDataTranskrip.value!!, getData)
        } else {
            Log.d("cek selectedPaketnull", msibViewModel.paketData.value?.namaPaketKonversi.toString())
        }

        msibViewModel.paketData.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("cek it paket !null", it.namaPaketKonversi.toString())
                bindingDetailPaket(viewModelTranskrip.liveDataTranskrip.value, getData)
                binding.btnTambah.setOnClickListener {
                    if ((sks.value?.toInt() ?: 0) >= 20 && (sks.value?.toInt() ?: 0) > 0) {
                        Toast.makeText(requireContext(), "Jumlah SKS belum memenuhi!", Toast.LENGTH_SHORT).show()
                    } else {
                        findNavController().navigate(R.id.action_detailMagangFragment_to_tambahMatkulFragment)
                    }
                }
            } else {
                Log.d("cek it slctdprogram null", msibViewModel.paketData.value?.namaPaketKonversi.toString())
                binding.btnTambah.setOnClickListener {
                    findNavController().navigate(R.id.action_detailMagangFragment_to_tambahMatkulFragment)
                }
            }
        }

//        msibViewModel.paketData.observe(viewLifecycleOwner) {
//            sks.value = 0
//            if (it != null) {
//                 selectedPaket = it
//                it.detail?.map {
//                    sks.value = (sks.value?.toInt() ?: 0) + (it?.sks?.toInt() ?: 0)
//                }
//
//                Log.d("cek it paket !null", it?.namaPaketKonversi.toString())f
//
//                bindingDetailPaket(
//                    getData.status ?: -1,
//                    getData.isFinish ?: false,
//                    selectedPaket!!,
//                    viewModelTranskrip.liveDataTranskrip.value
//                )
//
//                binding.btnTambah.setOnClickListener {
//                    if ((sks.value?.toInt() ?: 0) >= 20 && (sks.value?.toInt() ?: 0) > 0) {
//                        Toast.makeText(requireContext(), "Jumlah SKS belum memenuhi!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Log.d("cek msibvm data", "${msibViewModel.paketData.value?.namaPaketKonversi
//                        } ; ${msibViewModel.paketData.value?.detail?.firstOrNull()?.namaMatkul}")
//                        findNavController().navigate(R.id.action_detailMagangFragment_to_tambahMatkulFragment)
//                    }
//                }
//            } else {
//                Log.d("cek it slctdprogram null", it?.namaPaketKonversi.toString())
//                Toast.makeText(requireContext(), "Paket konversi kosong!", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }

        msibViewModel.paketData.observe(viewLifecycleOwner) { newData ->
            // Compare the current detail list with the initial one
            if (initialDetailList == null)
                initialDetailList = viewModelPaket.liveDataPaket.value?.data?.listMatkul?.map {
                    DetailsPaketKonversi(
                        createdAt = it?.createdAt,
                        createdBy = it?.createdBy,
                        id = it?.id,
                        idMatkul = it?.idMatkul,
                        idPaketKonversi = it?.idPaketKonversi,
                        kodeMatkul = it?.kodeMatkul,
                        namaMatkul = it?.namaMatkul,
                        namaPaketKonversi = it?.namaPaketKonversi,
                        sks = it?.sks,
                        updatedAt = it?.updatedAt.toString(),
                        updatedBy = it?.updatedBy.toString()
                    )
                }

            val isChanged = newData?.detail != initialDetailList
            Log.d("cek ischanged $isChanged", "${newData?.detail?.size} ; ${initialDetailList?.size}")
            if (isChanged && (getData.status == 4 || getData.status == 0) && getData.isFinish == false) {
                // If data has changed
                binding.btnSetujui.isEnabled = false
                binding.btnSimpan.isEnabled = true

                binding.btnSimpan.setOnClickListener {
                    /// TODO : save the data to bulk
                    viewModelDaftarMsib.postPaketBulk(
                        "bearer ${getUser(requireContext())?.token?.AccessToken}",
                        PostPaketKonversi(
                            deskripsi = msibViewModel.paketData.value?.deskripsi.toString(),
                            idPaketKonversi = msibViewModel.paketData.value?.id,
                            idPendaftarMbkm = getData.id.toString(),
                            idProgram = msibViewModel.paketData.value?.idProgram.toString(),
                            idProgramProdi = msibViewModel.paketData.value?.idProgramProdi.toString(),
                            idRole = "HA02",
                            listMatkul = msibViewModel.paketData.value?.detail
                                ?.filter { it?.kodeMatkul != null && it.kodeMatkul != "null" && it.kodeMatkul != "" }!!
                                .map {
                                    ListMatkul(
                                        id = it?.id,
                                        idMatkul = it?.idMatkul.toString(),
                                        idPendaftarMbkm = getData.id.toString(),
                                        isMahasiswa = true,
                                        nilaiAngka = null,
                                        nilaiHuruf = null,
                                        sks = it?.sks.toString()
                                    )
                                },
                            namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi.toString()
                        ),
                        requireContext()
                    )
                    viewModelDaftarMsib.addPaketBulk.observe(viewLifecycleOwner) {
                        binding.btnSimpan.isEnabled = false

                        binding.btnTambah.isEnabled = true
                        binding.btnTambah.setOnClickListener {
                            if ((sks.value?.toInt() ?: 0) >= 20 && (sks.value?.toInt() ?: 0) > 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Jumlah SKS belum memenuhi!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                findNavController().navigate(R.id.action_detailMagangFragment_to_tambahMatkulFragment)
                            }
                        }

                        binding.btnSetujui.isEnabled = true
                        binding.btnSetujui.setOnClickListener {
                            PendaftaranDialogFragment(
                                false, "Konfirmasi Paket Konversi",
                                "Apakah Anda yakin ingin menyetujui paket konversi berikut?",
                                true, { postData(getData) }
                            ).show(
                                requireActivity().supportFragmentManager,
                                "TAG ACC PROGRAM TIMMBKM"
                            )
                        }

                        Toast.makeText(
                            requireContext(),
                            "Paket konversi berhasil disimpan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    viewModelDaftarMsib.isLoading.observe(viewLifecycleOwner) {
                        if (it) binding.progressBar.visibility = View.VISIBLE
                        else binding.progressBar.visibility = View.INVISIBLE
                    }
                    viewModelDaftarMsib.isError.observe(viewLifecycleOwner) {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // If data has not changed
                binding.btnSetujui.isEnabled = true
                binding.btnSimpan.isEnabled = false

                binding.btnSetujui.setOnClickListener {
                    PendaftaranDialogFragment(
                        false, "Konfirmasi Paket Konversi",
                        "Apakah Anda yakin ingin menyetujui paket konversi berikut?",
                        true, { postData(getData) }
                    ).show(requireActivity().supportFragmentManager, "TAG ACC PROGRAM TIMMBKM")
                }
            }
        }

        /// TODO : testing run
        if ((getData.status == 4 || getData.status == 0) && getData.isFinish == false) {
            binding.btnTambah.isEnabled = true
//            binding.btnTambah.setOnClickListener {
//                if ((sks.value?.toInt() ?: 0) >= 20 && (sks.value?.toInt() ?: 0) > 0) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Jumlah SKS belum memenuhi!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    findNavController().navigate(R.id.action_detailMagangFragment_to_tambahMatkulFragment)
//                }
//            }
        } else {
            binding.btnTambah.isEnabled = false
        }
    }

    private fun postData(getData: ItemPendaftarProgram) {
        /// TODO : simpan permanen paket awal konversi , move status to paket disetujui - 2
        Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()

        viewModelChangeStatus.changeStatus(ChangeStatus(getData.id.toString(), false, 2), requireContext())
        viewModelChangeStatus.status.observe(viewLifecycleOwner) {
            if (it.data == "success") {
                Toast.makeText(requireContext(), "Paket konversi berhasil disetujui", Toast.LENGTH_SHORT).show()

                binding.btnSimpan.isEnabled = false
                binding.btnSetujui.isEnabled = false
                binding.btnTambah.isEnabled = false

                binding.txtStatus.text = "Telah Disetujui Pembimbing MBKM"
                binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getData.npm)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(
                                getData.npm.toString(),
                                "Paket Konversi",
                                "Paket konversi telah disetujui oleh Pembimbing. Anda dapat melanjutkan ke tahap pengisian log kegiatan.",
                                getData.status.toString(),
                                getData.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(
                                getData.npm.toString(),
                                "Paket Konversi",
                                "Paket konversi telah disetujui oleh Pembimbing. Anda dapat melanjutkan ke tahap pengisian log kegiatan.",
                                getData.status.toString(),
                                getData.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
                    }

            } else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }

        viewModelChangeStatus.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBarVerif.visibility = View.VISIBLE
            else binding.progressBarVerif.visibility = View.INVISIBLE
        }
        viewModelChangeStatus.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

    }

//    private fun bindingDetailPaket(
//        status: Int,
//        isFinish: Boolean,
//        dataPaket: ItemLookupPaketKonversi,
//        listTranskrip: ResponseTranskrip?
//    ) {
//        val dataPaket = msibViewModel.paketData.value
//
//        binding.txtNamapaketkonversi.text = dataPaket?.namaPaketKonversi
//        binding.txtTotalsks.text = "Total SKS : ${sks.value.toString()}"
//
//        Log.d(
//            "cek data",
//            "${dataPaket?.namaPaketKonversi} ; ${dataPaket?.detail?.first()?.namaMatkul} ; ${
//                listTranskrip?.data?.size}"
//        )
//        binding.tableLayout.removeAllViews()
//        binding.tableLayout.addView(binding.tableRow)
//
//        val tableRowHeader = LayoutInflater.from(requireContext()).inflate(
//            R.layout.tablerowheader_paketnilaiaksi, null) as TableRow
//
//        val kodeMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_kodemk)
//        kodeMatkulHeader.text = "Kode"
//        kodeMatkulHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
//
//        val namaMatkulHeader = tableRowHeader.findViewById<TextView>(R.id.txt_mk)
//        namaMatkulHeader.text = "Mata Kuliah"
//        namaMatkulHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
//
//        val sksHeader = tableRowHeader.findViewById<TextView>(R.id.txt_sksmk)
//        sksHeader.text = "SKS"
//        sksHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
//
//        val nilaiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_nilaimk)
//        nilaiHeader.text = "Nilai"
//        nilaiHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
//
//        val aksiHeader = tableRowHeader.findViewById<TextView>(R.id.txt_aksimk)
//        aksiHeader.text = "Aksi"
//        aksiHeader.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
//
//        binding.tableLayout.addView(tableRowHeader)
//
//        dataPaket?.detail?.forEach { mk ->
//            if (mk?.idMatkul != null && mk.kodeMatkul != "") {
//                val tableRowData = LayoutInflater.from(requireContext())
//                    .inflate(R.layout.tablerow_paketnilaiaksi, null) as TableRow
//
//                val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
//                kodeMatkul.text = mk.kodeMatkul
//
//                val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
//                namaMatkul.text = mk.namaMatkul
//
//                val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
//                sks.text = mk.sks
//
//                val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
//                val nilai = listTranskrip?.data?.firstOrNull { it?.kode_mata_kuliah == mk.kodeMatkul }?.nilai
//                nilaiTextView.text = nilai ?: " "
//
//                val btnAksi = tableRowData.findViewById<ImageButton>(R.id.btn_aksi)
//                if ((status == 4 || status == 0) && isFinish == false) btnAksi.isEnabled = true
//                else {
//                    btnAksi.isEnabled = false
//                    btnAksi.imageTintList = ColorStateList.valueOf(Color.parseColor("#C1C1C1"))
//                }
//
//                viewModelChangeStatus.status.observe(viewLifecycleOwner) {
//                    if (it.data == "success") {
//                        btnAksi.isEnabled = false
//                        btnAksi.imageTintList = ColorStateList.valueOf(Color.parseColor("#C1C1C1"))
//                    }
//                }
//
//                btnAksi.setOnClickListener {
//                    Log.d("cek btnaksi clicker", "${mk.namaPaketKonversi} ; ${mk.namaMatkul}")
//
//                    msibViewModel.paketData.value = ItemLookupPaketKonversi(
//                        createdAt = null,
//                        createdBy = null,
//                        deskripsi = dataPaket.deskripsi,
//                        detail = dataPaket.detail?.map { pk ->
//                            if (mk.kodeMatkul != pk?.kodeMatkul && mk.namaMatkul != pk?.namaMatkul)
//                                DetailsPaketKonversi(
//
//                    )
//
//                    msibViewModel.paketData.observe(viewLifecycleOwner) {
//                        Log.d("cek all paket msib",
//                            "${it?.namaPaketKonversi} ; ${it?.detail?.map { it?.namaMatkul }}")
//                    }
//                }
//                binding.tableLayout.addView(tableRowData)
//            }
//        }
//    }

//    private fun tabelListMatkul(getData: ItemPendaftarProgram) {
//        val viewmodel = ViewModelProvider(this)[PaketKonversiViewModel::class.java]
//        viewmodel.getliveDataPaket(
//            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
//            idPendaftarMBKM = getData.id!!,
//            idProgramProdi = getData.idProgramProdi!!,
//            idJenisMbkm = getData.idJenisMbkm!!,
//            idProgram = getData.idProgram!!,
//            statusMbkm = getData.statusMbkm!!
//        ).observe(viewLifecycleOwner) {
//            binding.tableLayout.removeAllViews()
//            binding.tableLayout.addView(binding.tableRow)
//            val tableRow = LayoutInflater.from(requireContext()).inflate(R.layout.tablerowheader_paketnilaiaksi, null) as TableRow
//
//            val a = tableRow.findViewById<TextView>(R.id.txt_kodemk)
//            a.setText("Kode")
//            a.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold))
//
//            val b = tableRow.findViewById<TextView>(R.id.txt_mk)
//            b.setText("Mata Kuliah")
//            b.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold))
//
//            val c = tableRow.findViewById<TextView>(R.id.txt_sksmk)
//            c.setText("SKS")
//            c.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold))
//
//            val d = tableRow.findViewById<TextView>(R.id.txt_nilaimk)
//            d.setText("Nilai")
//            d.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold))
//
//            val e = tableRow.findViewById<TextView>(R.id.txt_aksimk)
//            e.setText("Aksi")
//            e.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold))
//
//            binding.tableLayout.addView(tableRow)
//
//            it.data?.listMatkul?.map {
//                val tableRow = LayoutInflater.from(requireContext()).inflate(R.layout.tablerow_paketnilaiaksi, null) as TableRow
//                tableRow.findViewById<TextView>(R.id.txt_kodemk).setText(it?.kodeMatkul)
//                tableRow.findViewById<TextView>(R.id.txt_mk).setText(it?.namaMatkul)
//                tableRow.findViewById<TextView>(R.id.txt_sksmk).setText(it?.sks)
//                tableRow.findViewById<TextView>(R.id.txt_nilaimk).setText(it?.nilaiAngka)
//
//                tableRow.findViewById<TextView>(R.id.txt_aksimk).setText(it?.sks)
//                binding.tableLayout.addView(tableRow)
//            }
//        }
//        viewmodel.isLoading.observe(viewLifecycleOwner) {
//            if (it) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.INVISIBLE
//        }
//        viewmodel.isError.observe(viewLifecycleOwner) {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//        }
//
//        binding.linearHidden.setVisibility(View.GONE)
//        binding.imageButton.rotation = 270F
//
//        binding.cardPaketkonversi.setOnClickListener {
//            if (binding.linearHidden.getVisibility() === View.VISIBLE) {
//                TransitionManager.beginDelayedTransition(
//                    binding.cardPaketkonversi,
//                    AutoTransition()
//                )
//                binding.linearHidden.setVisibility(View.GONE)
//                binding.imageButton.rotation = 270F
//            } else {
//                TransitionManager.beginDelayedTransition(binding.cardPaketkonversi, AutoTransition())
//                binding.linearHidden.setVisibility(View.VISIBLE)
//                binding.imageButton.rotation = 90F
//            }
//        }
//    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {

//        val viewmodelLuaran = ViewModelProvider(this)[LuaranViewModel::class.java]
//        viewmodelLuaran.getliveDataAllLuaran(
//            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
//            id = getData.id.toString()
//        ).observe(viewLifecycleOwner) {
//            binding.rvLuaran.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            binding.rvLuaran.adapter = ListLuaranAdapter(it.data!!)
//        }
//        viewmodelLuaran.isLoading.observe(viewLifecycleOwner) {
//            if (it) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.INVISIBLE
//        }
//        viewmodelLuaran.isError.observe(viewLifecycleOwner) {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//        }

        binding.apply {
            txtNpm.text = getData.npm
            txtNama.text = getData.nama
            txtNamaprodi.text = getData.namaProdi
            txtNip.text = if (getData.nip.isNullOrEmpty()) "-" else getData.nip
            txtNamadospem.text =
                if (getData.namaPegawai.isNullOrEmpty()) "-" else getData.namaPegawai
            txtPosisi.text = getData.namaPosisiKegiatanTopik
            txtNamamitra.text = getData.namaMitra
            txtNamaprogram.text = getData.namaProgram
            txtJenismbkm.text = getData.namaJenisMbkm

            btnFilebuktidaftar.setOnClickListener {
                openFile(getData.link.toString(), requireContext())
            }

//            txtWaktuNilai.text = getData.tglPenilaian
//            txtNilaiakhir.text = getData.nilaiAngka
//            txtKetNilai.text = getData.ketPenilaian
//
//            btnFileNilai.setOnClickListener {
//                openFile(getData.link.toString())
//            }

            if ((getData.status == 4 || getData.status == 0) && getData.isFinish == false) {
                binding.txtStatus.text = "Belum Verifikasi Paket Konversi"
                binding.txtStatus.setTextColor(Color.WHITE)
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                binding.txtStatus.text = "Telah Disetujui Pembimbing MBKM"
                binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))

                binding.btnSimpan.isEnabled = false
                binding.btnSetujui.isEnabled = false
                binding.btnTambah.isEnabled = false
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

    private fun bindingDetailPaket(listTranskrip: ResponseTranskrip?, getData: ItemPendaftarProgram) {
        val dataPaket = msibViewModel.paketData.value
        binding.txtNamapaketkonversi.text = dataPaket?.namaPaketKonversi
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
                        createdAt = null, createdBy = null,
                        deskripsi = dataPaket.deskripsi,
                        detail = dataPaket.detail?.mapNotNull { pk ->
                            if (mk.kodeMatkul != pk?.kodeMatkul)
                                DetailsPaketKonversi(
                                    idPaketKonversi = pk?.idPaketKonversi,
                                    id = pk?.id, idMatkul = pk?.idMatkul,
                                    kodeMatkul = pk?.kodeMatkul, namaMatkul = pk?.namaMatkul,
                                    namaPaketKonversi = pk?.namaPaketKonversi, sks = pk?.sks,
                                    updatedAt = pk?.updatedAt, updatedBy = pk?.updatedBy,
                                    createdAt = pk?.createdAt, createdBy = pk?.createdBy)
                            else null
                        },
                        id = dataPaket.id,
                        idJenisMbkm = getData.idJenisMbkm,
                        idProgram = getData.idProgram,
                        idProgramProdi = getData.idProgramProdi,
                        isDeleted = false,
                        kdProdi = getData.kdPeriode,
                        namaPaketKonversi = dataPaket.namaPaketKonversi,
                        namaProdi = getData.namaProdi,
                        namaProgram = getData.namaProgram,
                        updatedAt = null, updatedBy = null)

//                    msibViewModel.paketData.observe(viewLifecycleOwner){
//                        Log.d("cek all paket msib", "${it?.namaPaketKonversi} ; ${
//                            it?.detail?.map { it?.namaMatkul }}")
//                    }
                }
                binding.tableLayout.addView(tableRowData)
            }

            sks.value = (sks.value?.toInt()?:0) + (mk?.sks?.toIntOrNull()?:0)
        }
        binding.txtTotalsks.text = "Total SKS : ${sks.value}"
    }

}