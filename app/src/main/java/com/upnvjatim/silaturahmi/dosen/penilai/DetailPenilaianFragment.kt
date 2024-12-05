package com.upnvjatim.silaturahmi.dosen.penilai

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
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentDetailPenilaianBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.EditNilaiDialogFragment
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.request.EditNilaiMk
import com.upnvjatim.silaturahmi.model.request.PostNilaiFinal
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.openFile
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.LuaranViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListLuaranAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class DetailPenilaianFragment : Fragment(), EditNilaiDialogFragment.DialogListener {
    private var _binding: FragmentDetailPenilaianBinding? = null
    private val binding get() = _binding!!

    //    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelNilai: NilaiViewModel by activityViewModels()
    private val viewModelChangeStatus: ChangeStatusViewModel by activityViewModels()

    private var sks: MutableLiveData<Int> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPenilaianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?

        toolbar()
        bindingTxt(getData!!)

        viewModelNilai.getliveNilaiFinal(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftar = getData.id.toString(),
        )
        viewModelNilai.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewModelNilai.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        bindingDetailPaket(getData)
    }

    private fun saveNilai(getData: ItemPendaftarProgram) {
        Toast.makeText(requireContext(), "Simpan semua nilai Berhasil", Toast.LENGTH_SHORT).show()

        viewModelNilai.getliveNilaiFinal(
            "bearer ${getUser(requireContext())?.token?.AccessToken.toString()}",
            getData.id.toString()
        )

        val it = viewModelNilai.liveDataNilaiFinal.value
            val data = it?.data?.listMatkul?.map {
                EditNilaiMk(
                    id = it?.id.toString(),
                    idMatkul = it?.idMatkul.toString(),
                    idPaketKonversi = it?.idPaketKonversi.toString(),
                    idPendaftarMbkm = it?.idPendaftarMbkm.toString(),
                    nilaiAngka =
                    if (it?.nilaiAngkaFinal.isNullOrBlank()) it?.nilaiAngka.toString()
                    else it?.nilaiAngka.toString(),
                    nilaiHuruf = null,
                    sks = it?.sks.toString()
                )
            }

            val postdata = PostNilaiFinal(data!!)

            viewModelNilai.postNilaiFinal(postdata, requireContext())

    }

    private fun bindingDetailPaket(getData: ItemPendaftarProgram) {
        viewModelNilai.liveDataNilaiFinal.observe(viewLifecycleOwner) {
            Log.d("cek data paket", it?.data?.namaPaketKonversi.toString())
            if (it.data != null && it.data.idPaketKonversi != null && it.data.idPaketKonversi != "") {
                Log.d("cek pakettt", it.data.idPaketKonversi.toString())

                sks.value = 0
                it.data.listMatkul?.map {
                    sks.value = (sks.value?.toInt() ?: 0) + (it?.sks?.toInt() ?: 0)
                }

                val dataPaket = it.data
                binding.txtNamapaketkonversi.text = dataPaket.namaPaketKonversi
                binding.txtDescpaketkonversi.text = dataPaket.deskripsi
                binding.txtTotalsks.text = "Total SKS : ${sks.value.toString()}"

                Log.d("cek data", "${dataPaket.namaPaketKonversi} ; ${
                    dataPaket.listMatkul?.first()?.namaMatkul} ; ${dataPaket.listMatkul?.size}")

                binding.tableLayout.removeAllViews()
                binding.tableLayout.addView(binding.tableRow)

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

                dataPaket.listMatkul?.forEach { mk ->
                    if (mk?.idMatkul != null && mk.kodeMatkul != "") {
                        val tableRowData = LayoutInflater.from(requireContext())
                            .inflate(R.layout.tablerow_paketnilaiedit, null) as TableRow

                        val kodeMatkul = tableRowData.findViewById<TextView>(R.id.txt_kodemk)
                        kodeMatkul.text = mk.kodeMatkul

                        val namaMatkul = tableRowData.findViewById<TextView>(R.id.txt_mk)
                        namaMatkul.text = mk.namaMatkul

                        val sks = tableRowData.findViewById<TextView>(R.id.txt_sksmk)
                        sks.text = mk.sks

                        val nilaiTextView = tableRowData.findViewById<TextView>(R.id.txt_nilaimk)
                        nilaiTextView.text = mk.nilaiAngkaFinal ?: mk.nilaiAngka

                        val btnAksi = tableRowData.findViewById<MaterialButton>(R.id.btn_edit)
                        btnAksi.isEnabled = getData.status == 3 && getData.isFinish == false

                        btnAksi.setOnClickListener {
                            Log.d("cek btnaksi clicker", "${mk.namaPaketKonversi} ; ${mk.namaMatkul}")
                            EditNilaiDialogFragment(mk, getData).apply {
                                listener = object : EditNilaiDialogFragment.DialogListener {
                                    override fun onDialogDismissed(getData: ItemPendaftarProgram) {
                                        viewModelNilai.getliveNilaiFinal(
                                            "bearer ${getUser(requireContext())?.token?.AccessToken}",
                                            idPendaftar = getData.id.toString()
                                        )
                                    }
                                }
                            }.show(
                                requireActivity().supportFragmentManager, "TAG ACC PROGRAM TIMMBKM"
                            )
                        }

                        binding.tableLayout.addView(tableRowData)
                    }
                }
            } else {
                Log.d("cek it slctdprogram null", it.toString())
                Toast.makeText(requireContext(), "Paket konversi kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        val viewmodelLuaran = ViewModelProvider(this)[LuaranViewModel::class.java]
        viewmodelLuaran.getliveDataAllLuaran(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            id = getData.id.toString()
        ).observe(viewLifecycleOwner) {
            if (it != null && it.data != null) {
                binding.txtEmptyluaran.visibility = View.GONE
                binding.rvLuaran.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                val adapter = ListLuaranAdapter(it.data.filter { it?.isDeleted == false })
                binding.rvLuaran.adapter = adapter

                var totalHeight = 0

                for (i in 0 until adapter.itemCount) {
                    val viewHolder = adapter.createViewHolder(binding.rvLuaran, adapter.getItemViewType(i))
                    adapter.onBindViewHolder(viewHolder, i)
                    viewHolder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(
                            binding.rvLuaran.width,
                            View.MeasureSpec.EXACTLY
                        ),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    totalHeight += viewHolder.itemView.measuredHeight // + (8 * adapter.itemCount)
                }

                binding.rvLuaran.layoutParams.height = totalHeight
                binding.rvLuaran.requestLayout()

            } else {
                binding.txtEmptyluaran.visibility = View.VISIBLE
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
            txtNamadospem.text =
                if (getData.namaPegawai.isNullOrEmpty()) "-" else getData.namaPegawai
            txtPosisi.text = getData.namaPosisiKegiatanTopik
            txtNamamitra.text = getData.namaMitra
            txtNamaprogram.text = getData.namaProgram
            txtJenismbkm.text = getData.namaJenisMbkm

            btnFilebuktidaftar.setOnClickListener {
                openFile(getData.link.toString(), requireContext())
            }

            txtWaktuNilai.text = getData.tglPenilaian
            txtNilaiakhir.text = getData.nilaiAngka
            txtKetNilai.text = getData.ketPenilaian

            if (getData.linkPenilaian == null || getData.linkPenilaian == "") btnFileNilai.visibility =
                View.INVISIBLE
            else {
                btnFileNilai.visibility = View.VISIBLE
                btnFileNilai.setOnClickListener {
                    openFile(getData.linkPenilaian.toString(), requireContext())
                }
            }

            if (getData.status == 3 && getData.isFinish == true) {
                binding.txtStatus.text = "Penilaian Disetujui"
                binding.txtStatus.setTextColor(Color.parseColor("#465A03"))
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            } else {
                binding.txtStatus.text = "Minta Persetujuan Nilai"
                binding.txtStatus.setTextColor(Color.WHITE)
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            }

            btnLogbook.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("pendaftar", getData)
                findNavController().navigate(
                    R.id.action_detailPenilaianFragment_to_listLogbookFragment,
                    bundle
                )
            }

            if (getData.status == 3 && getData.isFinish == true) {
                btnRevisi.isEnabled = true
                btnRevisi.setOnClickListener {
                    PendaftaranDialogFragment(
                        false,
                        "Ajukan Revisi Penilaian",
                        "Apakah Anda yakin ingin mengajukan revisi penilaian?",
                        true, { ajukanRevisi(getData) })
                        .show(
                            requireActivity().supportFragmentManager,
                            "TAG DETAIL ACC PROGRAM TIMMBKM"
                        )
                }
            } else btnRevisi.isEnabled = false

            if (getData.status == 3 && getData.isFinish == false) {
                binding.btnSimpanall.isEnabled = true
                binding.btnSimpanall.setOnClickListener {
                    PendaftaranDialogFragment(
                        false, "Konfirmasi Penilaian", null, true, { saveNilai(getData) })
                        .show(
                            requireActivity().supportFragmentManager,
                            "TAG DETAIL ACC PROGRAM TIMMBKM"
                        )
                }
            } else btnSimpanall.isEnabled = false

            viewModelNilai.liveDataPostNilaiFinal.observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModelChangeStatus.changeStatus(
                        ChangeStatus(getData.id.toString(), true, 3),
                        requireContext())
                }
            }

            viewModelChangeStatus.status.observe(viewLifecycleOwner) {
                if (it.data == "success") {
                    Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()

                    FirebaseFirestore.getInstance()
                        .collection("notification")
                        .whereEqualTo("username", getData.npm.toString())
                        .get()
                        .addOnSuccessListener {
                            Log.d("cek send notif", "${it.size()} ; ${getData.npm.toString()} ; ${
                                getData.status.toString()} ; ${getData.isFinish.toString().toBoolean()}")

                            if (it.isEmpty || it == null) {
                                createNewNotif(
                                    getData.npm.toString(),
                                    "Finalisasi Penilaian",
                                    "Selamat! Program MSIB Anda telah berakhir. Nilai Akhir berhasil difinalisasi. Apabila terjadi kesalahan, hubungi Pembimbing MBKM melalui fitur Group Chats.",
                                    getData.status.toString(),
                                    getData.isFinish.toString().toBoolean(),
                                    requireContext()
                                )
                            } else {
                                saveNotif(
                                    getData.npm.toString(),
                                    "Finalisasi Penilaian",
                                    "Selamat! Program MSIB Anda telah berakhir. Nilai Akhir berhasil difinalisasi. Apabila terjadi kesalahan, hubungi Pembimbing MBKM melalui fitur Group Chats.",
                                    getData.status.toString(),
                                    getData.isFinish.toString().toBoolean(),
                                    requireContext()
                                )
                            }
                            findNavController().popBackStack()
                        }.addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("error notif", "${e.message} ${e.cause} ${e.stackTrace}")
                        }
                }
            }

            viewModelChangeStatus.isLoading.observe(viewLifecycleOwner) {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
            viewModelChangeStatus.isError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            viewModelNilai.isLoading.observe(viewLifecycleOwner) {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
            viewModelNilai.isError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun ajukanRevisi(getData: ItemPendaftarProgram) {
        val viewmodel = ViewModelProvider(this)[ChangeStatusViewModel::class.java]

        viewmodel.changeStatus(ChangeStatus(getData.id.toString(), false, 1), requireContext())
        viewmodel.status.observe(viewLifecycleOwner) {
            if (it.data == "success") {
                Toast.makeText(
                    requireContext(),
                    "Revisi Penilaian berhasil diajukan",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnRevisi.isEnabled = false

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getData.npm)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            createNewNotif(
                                getData.npm.toString(),
                                "Revisi Penilaian",
                                "Revisi Penilaian berhasil diajukan. Mohon bersabar menunggu hasil verifikasi",
                                getData.status.toString(),
                                getData.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        } else {
                            saveNotif(
                                getData.npm.toString(),
                                "Revisi Penilaian",
                                "Revisi Penilaian berhasil diajukan. Mohon bersabar menunggu hasil verifikasi",
                                getData.status.toString(),
                                getData.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("error notif", "${e.message} ${e.cause} ${e.stackTrace}")
                    }

            } else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBarVerif.visibility = View.VISIBLE
            else binding.progressBarVerif.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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

        binding.btnBatal.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDialogDismissed(getData: ItemPendaftarProgram) {
        viewModelNilai.getliveNilaiFinal(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftar = getData.id.toString(),
        )
    }
}