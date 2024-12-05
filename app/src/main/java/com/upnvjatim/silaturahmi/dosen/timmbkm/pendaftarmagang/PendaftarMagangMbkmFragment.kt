package com.upnvjatim.silaturahmi.dosen.timmbkm.pendaftarmagang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentPendaftarMagangMbkmBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.ChangeStatusViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.AccPendaftarAdapter
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PendaftarMagangMbkmFragment : Fragment() {
    private var _binding: FragmentPendaftarMagangMbkmBinding? = null
    private val binding get() = _binding!!
    private var chosenMhs: MutableList<ItemPendaftarProgram> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPendaftarMagangMbkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()

        val viewmodel = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]
        viewmodel.callApiPendaftarProgram(
            authHeader = "bearer ${getUser(requireContext())?.token}",
            pageNumber = 1, pageSize = 100,
            idProgramProdi = getUser(requireContext())?.user?.idProgramProdi.toString(),

//            statusMbkm = selectedMbkm, idPeriode = selectedPeriode,
            statusMbkm = null, idPeriode = null, query = null,
            sortBy = "id", sortType = "asc", sortByStatus = "0,4",
            idJenisMbkm = null, idProgram = null, idPegawai = null, status = null,
            )

        viewmodel.liveDataPendaftarProgram.observe(viewLifecycleOwner) {
//        viewmodel.getliveDataPendaftarProgram().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                val adapter = AccPendaftarAdapter(it.data?.items!!)
                binding.recyclerView.adapter = adapter
                adapter.setOnItemClickListener { mhs ->
                    if (chosenMhs.contains(mhs)) chosenMhs.remove(mhs)
                    else chosenMhs.add(mhs)
                    chosenMhs.map {
                        Log.d("cek chosenmhs", "${it.nama}")
                    }

                    if(chosenMhs.isEmpty()) binding.btnVerifikasi.isEnabled = false
                    else {
                        binding.btnVerifikasi.isEnabled = true
                        binding.btnVerifikasi.setOnClickListener {
                            PendaftaranDialogFragment(false, "Konfirmasi Verifikasi Program", null,
                                true, { postData() }).show(requireActivity().supportFragmentManager,
                                "TAG ACC PROGRAM TIMMBKM")
                        }
                    }

                }
            } else {
                Toast.makeText(requireContext(), "ItemPeriode kosong!", Toast.LENGTH_SHORT).show()
            }
        }

        if(chosenMhs.isEmpty()) binding.btnVerifikasi.isEnabled = false
        else {
            binding.btnVerifikasi.isEnabled = true
            binding.btnVerifikasi.setOnClickListener {
                PendaftaranDialogFragment(false, "Konfirmasi Verifikasi Program", null, true, { postData() })
                    .show(requireActivity().supportFragmentManager, "TAG ACC PROGRAM TIMMBKM")
            }
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

//        Glide.with(this).load(
//            "https://cdn3d.iconscout.com/3d/premium/thumb/empty-box-9606753-7818459.png?f=webp"
//        ).into(binding.imgEmptydata)

    }

    private fun postData() {
        val viewmodel = ViewModelProvider(this)[ChangeStatusViewModel::class.java]

        /// TODO : [TESTING] verifikasi multiple pendaftar program
        chosenMhs.map { mhs ->
            viewmodel.changeStatus(ChangeStatus(mhs.id.toString(), false, 4), requireContext())
            viewmodel.status.observe(viewLifecycleOwner) {
                if (it.data == "success") {
                    FirebaseFirestore.getInstance()
                        .collection("notification")
                        .whereEqualTo("username", mhs.npm)
                        .get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                Log.d("cek noti", "${it.size()}")
                                createNewNotif(mhs.npm.toString(),
                                    "Pendaftaran Mandiri",
                                    "Pendaftaran program Mandiri berhasil diverifikasi! " +
                                            "Anda dapat melakukan perancangan paket konversi.",
                                    getIdPendaftar(requireContext())?.status.toString(),
                                    getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                    requireContext())
                            } else {
                                Log.d("cek notif else", "${it.size()}")
                                saveNotif(
                                    mhs.npm.toString(),
                                    "Pendaftaran Mandiri",
                                    "Pendaftaran program Mandiri berhasil diverifikasi! " +
                                            "Anda dapat melakukan perancangan paket konversi.",
                                    getIdPendaftar(requireContext())?.status.toString(),
                                    getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                    requireContext()
                                )
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.e("error notif mandiri", "${it.message} ${it.cause} ${it.stackTrace}")
                        }
                }
                else Toast.makeText(requireContext(), "Failed: ${it.data} ; ${mhs.nama}", Toast.LENGTH_SHORT).show()
            }
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }

        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }
}
