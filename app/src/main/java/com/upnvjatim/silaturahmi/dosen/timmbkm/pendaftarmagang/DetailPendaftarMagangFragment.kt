package com.upnvjatim.silaturahmi.dosen.timmbkm.pendaftarmagang

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentDetailPendaftarMagangBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.LuaranViewModel
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListLuaranAdapter
import com.google.firebase.firestore.FirebaseFirestore


class DetailPendaftarMagangFragment : Fragment() {
    private var _binding: FragmentDetailPendaftarMagangBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPendaftarMagangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?

        toolbar()
        bindingTxt(getData!!)
        tabelListMatkul(getData)

        /// TODO : [TESTING] acc program pendaftar
        if (getData.status == 0) {
            binding.btnVerifikasi.isEnabled = true
            binding.btnVerifikasi.setOnClickListener {
                PendaftaranDialogFragment(false,
                    "Konfirmasi Verifikasi Program",
                    null,
                    true,
                    { verifikasiProgram(getData) }).show(
                    requireActivity().supportFragmentManager,
                    "TAG DETAIL ACC PROGRAM TIMMBKM"
                )
            }
        } else binding.btnVerifikasi.isEnabled = false

        binding.btnBatal.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun verifikasiProgram(getData: ItemPendaftarProgram) {
        val viewmodel = ViewModelProvider(this)[ChangeStatusViewModel::class.java]
        viewmodel.changeStatus(
            ChangeStatus(getData.id.toString(), false, 4),
            requireContext()
        )
        viewmodel.status.observe(viewLifecycleOwner) {
            if (it.data == "success") {
                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getData.npm)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(
                                getData.npm.toString(),
                                "Pendaftaran Mandiri",
                                "Pendaftaran program Mandiri berhasil diverifikasi! Anda dapat melakukan perancangan paket konversi.",
                                getIdPendaftar(requireContext())?.status.toString(),
                                getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(
                                getData.npm.toString(),
                                "Pendaftaran Mandiri",
                                "Pendaftaran program Mandiri berhasil diverifikasi! Anda dapat melakukan perancangan paket konversi.",

                                getIdPendaftar(requireContext())?.status.toString(),
                                getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                requireContext()
                            )
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error : ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("error notif mandiri", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
                binding.btnVerifikasi.isEnabled = false
            } else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnVerifikasi.text = ""
                binding.progressBarVerif.visibility = View.VISIBLE
            } else {
                binding.btnVerifikasi.text = "Verifikasi"
                binding.progressBarVerif.visibility = View.INVISIBLE
            }
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun tabelListMatkul(getData: ItemPendaftarProgram) {
        val viewmodel = ViewModelProvider(this)[PaketKonversiViewModel::class.java]
        viewmodel.getliveDataPaket(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftarMBKM = getData.id!!,
            idProgramProdi = getData.idProgramProdi!!,
            idJenisMbkm = getData.idJenisMbkm!!,
            idProgram = getData.idProgram!!,
            statusMbkm = getData.statusMbkm!!
        ).observe(viewLifecycleOwner) {
            if (it != null && it.data != null) {
                binding.txtEmptypaket.visibility = View.GONE
                binding.cardPaketkonversi.visibility = View.VISIBLE

                binding.txtNamapaketkonversi.text = it.data.namaPaketKonversi
                binding.txtDescpaketkonversi.text = it.data.deskripsi

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

                it.data.listMatkul?.map {
                    val tableRow = LayoutInflater.from(requireContext())
                        .inflate(R.layout.tablerow_paketnilaiverif, null) as TableRow
                    tableRow.findViewById<TextView>(R.id.txt_kodemk).text = it?.kodeMatkul
                    tableRow.findViewById<TextView>(R.id.txt_mk).text = it?.namaMatkul
                    tableRow.findViewById<TextView>(R.id.txt_nilaimk).text = it?.nilaiAngka
                    tableRow.findViewById<TextView>(R.id.txt_verifnilai).text = it?.nilaiAngkaFinal
                    tableRow.findViewById<TextView>(R.id.txt_sksmk).text = it?.sks
                    binding.tableLayout.addView(tableRow)
                }
            } else {
                binding.txtEmptypaket.visibility = View.VISIBLE
                binding.cardPaketkonversi.visibility = View.GONE
            }
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        binding.linearHidden.visibility = View.GONE
        binding.imageButton.rotation = 270F

        binding.cardPaketkonversi.setOnClickListener {
            if (binding.linearHidden.visibility === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    binding.cardPaketkonversi,
                    AutoTransition()
                )
                binding.linearHidden.visibility = View.GONE
                binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(
                    binding.cardPaketkonversi,
                    AutoTransition()
                )
                binding.linearHidden.visibility = View.VISIBLE
                binding.imageButton.rotation = 90F
            }
        }

        binding.imageButton.setOnClickListener {
            if (binding.linearHidden.visibility === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    binding.cardPaketkonversi,
                    AutoTransition()
                )
                binding.linearHidden.visibility = View.GONE
                binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(
                    binding.cardPaketkonversi,
                    AutoTransition()
                )
                binding.linearHidden.visibility = View.VISIBLE
                binding.imageButton.rotation = 90F
            }
        }
    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        val viewmodelLuaran = ViewModelProvider(this)[LuaranViewModel::class.java]
        viewmodelLuaran.getliveDataAllLuaran(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            id = getData.id.toString()
        ).observe(viewLifecycleOwner) {
            binding.rvLuaran.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            if (it.data != null) binding.rvLuaran.adapter = ListLuaranAdapter(it.data)
            else Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show()
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
                openFile(getData.link.toString())
            }

            txtWaktuNilai.text = getData.tglPenilaian
            txtNilaiakhir.text = getData.nilaiAngka
            txtKetNilai.text = getData.ketPenilaian

            btnFileNilai.setOnClickListener {
                openFile(getData.linkPenilaian.toString())
            }

            btnLogbook.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("pendaftar", getData)
                findNavController().navigate(
                    R.id.action_detailPendaftarMagangFragment_to_listLogbookFragment,
                    bundle
                )
            }

            if (getData.status == 0) {
                txtStatus.text = "Magang Belum Disetujui"
                txtStatus.setTextColor(Color.WHITE)
                cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
            } else {
                txtStatus.text = "Magang Telah Disetujui"
                txtStatus.setTextColor(Color.parseColor("#465A03"))
                cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }


        }
    }

    private fun openFile(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val link = Uri.parse("https://silaturahmi.upnjatim.ac.id/api/files?path=$url")
        intent.setDataAndType(link, "application/pdf")
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_LONG).show()
            Log.e("error start link daftar", "${e.message} ; ${e.cause} ${e.stackTrace}")
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
//            requireActivity().onBackPressed()
//        fragmentManager?.popBackStack()
//            childFragmentManager.popBackStack()
            findNavController().popBackStack()
        }
    }
}