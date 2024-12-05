package com.upnvjatim.silaturahmi.mahasiswa.homenavbar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.mahasiswa.daftar.DaftarMsibActivity
import com.upnvjatim.silaturahmi.databinding.FragmentHomeMhsBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.luaran.LuaranNilaiActivity
import com.upnvjatim.silaturahmi.saveIdPendaftar
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.viewmodel.RiwayatViewModel


class HomeMhsFragment : Fragment() {
    private var _binding: FragmentHomeMhsBinding? = null
    private val binding get() = _binding!!
    private val viewModelPendaftarProgram: PendaftarProgramViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeMhsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtFullname.text = getUser(requireContext())?.user?.name
        binding.txtNpm.text = getUser(requireContext())?.user?.username

        val viewmodelRiwayat = ViewModelProvider(this)[RiwayatViewModel::class.java]
        viewmodelRiwayat.getliveDataActiveBySiamik(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idSiamik = getUser(requireContext())?.user?.idSiamikMahasiswa.toString()
        )
//        viewmodelRiwayat.getliveDataRiwayat(
//            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
//            pageNumber = 1, pageSize = 1
//        )
//        viewModelPendaftarProgram.getliveDataActiveProgram(
//            "bearer ${getUser(requireContext())?.token?.AccessToken}",
//            getUser(requireContext())?.user?.idSiamikMahasiswa.toString()
//        )
//        viewmodelRiwayat.liveDataRiwayat
            .observe(viewLifecycleOwner) {

                val item = it.data

                Log.d("cek livedatariwyat", "id: ${item?.id} ; namaJenisMbkm ${item?.namaJenisMbkm
                } ; namaJenisMitra ${item?.namaJenisMitra} ; namaMitra ${item?.namaMitra
                } ; namaPegawai ${item?.namaPegawai} ; namaPosisiKegiatanTopik ${item?.namaPosisiKegiatanTopik
                } ; namaProdi ${item?.namaProdi} ; namaProgram ${item?.namaProgram}")

                saveIdPendaftar(
                    ItemPendaftarProgram(
                        akhirKegiatan = item?.akhirKegiatan,
                        akhirPendaftaran = item?.akhirPendaftaran,
                        awalKegiatan = item?.awalKegiatan,
                        awalPendaftaran = item?.awalPendaftaran,
                        createdAt = item?.createdAt,
                        createdBy = item?.createdBy,
                        id = item?.id,
                        idJenisMbkm = item?.idJenisMbkm,
                        idJenisMitra = item?.idJenisMitra,
                        idMitra = item?.idMitra,
                        idMitraTerlibatProgram = item?.idMitraTerlibatProgram,
                        idPegawai = item?.idPegawai,
                        idPeriode = item?.idPeriode,
                        idPosisiKegiatanTopik = item?.idPosisiKegiatanTopik,
                        idProgram = item?.idProgram,
                        idProgramDitawarkan = item?.idProgramDitawarkan,
                        idProgramProdi = item?.idProgramProdi,
                        idSiamikMahasiswa = item?.idSiamikMahasiswa,
                        isDeleted = item?.isDeleted,
                        isFinish = item?.isFinish,
                        isMagangMandiri = item?.isMagangMandiri,
                        kdJenisMbkm = item?.kdJenisMbkm,
                        kdPeriode = item?.kdPeriode,
                        ketPenilaian = item?.ketPenilaian,
                        kuotaPerMitra = item?.kuotaPerMitra,
                        link = item?.link,
                        linkPenilaian = item?.linkPenilaian,
                        maxKrs = item?.maxKrs,
                        maxSks = item?.maxSks,
                        nama = item?.nama,
                        namaJenisMbkm = item?.namaJenisMbkm,
                        namaJenisMitra = item?.namaJenisMitra,
                        namaMitra = item?.namaMitra,
                        namaPegawai = item?.namaPegawai,
                        namaPosisiKegiatanTopik = item?.namaPosisiKegiatanTopik,
                        namaProdi = item?.namaProdi,
                        namaProgram = item?.namaProgram,
                        nilaiAngka = item?.nilaiAngka,
                        nip = item?.nip,
                        npm = item?.npm,
                        semester = item?.semester,
                        status = item?.status,
                        statusApproval = item?.statusApproval,
                        statusMbkm = item?.statusMbkm,
                        tahun = item?.tahun,
                        tglPenilaian = item?.tglPenilaian,
                        updatedAt = item?.updatedAt,
                        updatedBy = item?.updatedBy
                    ),
                    requireContext()
                )
            }

        binding.btnNotif.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_homeMhsFragment_to_notificationFragment)
        }

        // program mandiri dgn status == 0 => buar baru kosongan
        // program mandiri dgn status == 4 dst => gabole ngisi

        // program msib dgn status == 0  => buat baru kosongan
        // program msib dgn status == 4 => masuk edit; program fix
        // program msib dgn status == 2 => gabole ubah

        binding.linearLayoutDaftarMsib.setOnClickListener {
            if (getIdPendaftar(requireContext())?.status == 0 ||
                (getIdPendaftar(requireContext())?.status == 4
                        && getIdPendaftar(requireContext())?.statusMbkm == "MSIB")
                || getIdPendaftar(requireContext()) == null
                || getIdPendaftar(requireContext())?.status == null
            ) {
                val intent = Intent(requireActivity(), DaftarMsibActivity::class.java)
                intent.putExtra("program", "msib")
                startActivity(intent)
            } else Toast.makeText(
                requireContext(), "Anda tidak dapat mengisi program ini!", Toast.LENGTH_SHORT
            ).show()
        }

        binding.linearLayoutDaftarMandiri.setOnClickListener {
            if (getIdPendaftar(requireContext())?.status == 0 ||
                (getIdPendaftar(requireContext())?.status == 4
                        && getIdPendaftar(requireContext())?.statusMbkm == "MANDIRI")
                || getIdPendaftar(requireContext()) == null
                || getIdPendaftar(requireContext())?.status == null
            ) {
                val intent = Intent(requireActivity(), DaftarMsibActivity::class.java)
                intent.putExtra("program", "mandiri")
                startActivity(intent)
            } else Toast.makeText(
                requireContext(),
                "Anda tidak dapat mengisi program ini!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.linearLayoutLog.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_homeMhsFragment_to_listLogbookFragment)
        }

        binding.linearLayoutLuaran.setOnClickListener {
            val intent = Intent(requireActivity(), LuaranNilaiActivity::class.java)
            startActivity(intent)
        }

        binding.linearLayoutRiwayatProgram.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_homeMhsFragment_to_riwayatFragment)
        }

        binding.linearLayoutRiwayatBimbingan.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_homeMhsFragment_to_riwayatBimbinganFragment)
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(getUser(requireContext())?.user?.username.toString())
            .get()
            .addOnSuccessListener {
                if (it.exists() && it.getString("avatar") != null && it.getString("avatar") != "") {
                    val avatar = it.getString("avatar").toString()
                    Glide.with(this).load(avatar).into(binding.imgAvatar)
                } else {
                    Log.d("cek data users not found", "${it.data?.size}")
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error getAvatar : ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("error getavatar", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

}