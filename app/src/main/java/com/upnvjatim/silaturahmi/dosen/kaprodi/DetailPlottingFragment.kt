package com.upnvjatim.silaturahmi.dosen.kaprodi

import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentDetailPlottingBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.GroupChats
import com.upnvjatim.silaturahmi.model.request.SetDospem
import com.upnvjatim.silaturahmi.model.response.ItemLookupDospem
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.semester
import com.upnvjatim.silaturahmi.viewmodel.LookupDospemViewModel
import com.upnvjatim.silaturahmi.viewmodel.PaketKonversiViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class DetailPlottingFragment : Fragment() {
    private var _binding: FragmentDetailPlottingBinding? = null
    private val binding get() = _binding!!

    private var selectedDospem: ItemLookupDospem? = null
    private var changedospem = false
    private val viewmodelDospem: LookupDospemViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailPlottingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar()

        val getData = arguments?.getParcelable("dataprogram") as ItemPendaftarProgram?
        val listDospem = mutableListOf<ItemLookupDospem>()

        if (getData != null) {
            bindingTxt(getData)
            tabelListMatkul(getData)
        }

        binding.btnDelete.setOnClickListener {
            binding.btnDelete.visibility = View.GONE
            binding.btnSimpan.visibility = View.GONE
            binding.editPembimbing.isEnabled = true
            binding.editPembimbing.setText("Pilih Dosen Pembimbing MBKM")
            selectedDospem = null
        }

        binding.editPembimbing.setOnClickListener {
            if(listDospem.size > 0) {
                setListDospem(listDospem)
                binding.editPembimbing.showDropDown()
            } else {
                Toast.makeText(requireContext(), "Dosen tidak tersedia", Toast.LENGTH_SHORT).show()
                Log.d("cek listDospem", listDospem.size.toString())
            }
        }

        binding.editPembimbing.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("cek data dospem", "${listDospem[i].nama.toString()
            } ; ${listDospem[i].nip.toString()} ; ${getData?.nama} ; ${getData?.nip}")

            if (getData?.nip != listDospem[i].nip) changedospem = true
            selectedDospem = listDospem[i]
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnSimpan.visibility = View.VISIBLE
            binding.editPembimbing.isEnabled = false
        }

        binding.btnSimpan.setOnClickListener {
            PendaftaranDialogFragment(false, "Konfirmasi Pembimbing", null,
                true, {
                    if (getData != null) {
                        setPembimbing(getData)
                    }
                }).show(
                requireActivity().supportFragmentManager, "TAG DETAIL ACC PROGRAM TIMMBKM")
        }

        viewmodelDospem.getliveDataLookupDospem(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            100, 1, getUser(requireContext())?.user?.idProgramProdi.toString()
        ).observe(viewLifecycleOwner) {
            if(it!=null && it.data != null && it.data.items != null) {
                setListDospem(it.data.items)
                it.data.items.map {
                    listDospem.add(it)
                }
            }
        }
    }

    private fun setListDospem(listDospem: List<ItemLookupDospem>){
        val a = listDospem.map { "${it.nip} - ${it.nama}" }
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, a)
        binding.editPembimbing.setAdapter(arrayAdapter)
    }

    private fun setPembimbing(getData: ItemPendaftarProgram) {
        Log.d("cek data dosen", "${selectedDospem?.nama.toString()} ${getData.id.toString()} ${getData.nama}")

        if (selectedDospem != null) {
            viewmodelDospem.setDospem(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                SetDospem(getData.id.toString(), selectedDospem?.id.toString())
            )
            viewmodelDospem.liveDataSetDospem.observe(viewLifecycleOwner) {
                if (it != null) {
                    FirebaseFirestore.getInstance()
                        .collection("chats")
                        .whereEqualTo("lecturerId", selectedDospem?.nip.toString())
                        .whereEqualTo("semester", getData.semester)
                        .whereEqualTo("tahun", getData.tahun)
                        .get().addOnSuccessListener {
                            if (it != null && !it.isEmpty
                                && it.documents != null && !it.documents.isEmpty()) {

                                Log.d("cek group exist", "${it.documents.first().data?.get("groupname")} ; ${changedospem}")
                                if (changedospem) {
                                    deleteMemberGroupChats(getData, false)
                                } else {
                                    addMemberToGroupChats(getData)
                                }
                            } else {
                                Log.d("cek is empty", it.size().toString())
                                if (changedospem) {
                                    deleteMemberGroupChats(getData, true)
                                } else {
                                    if(selectedDospem != null) addNewGroupChats(getData, selectedDospem!!)
                                }
                            }
                            addGroupChatsToUsers(getData)
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.e("error get chatsmsthn", "${it.message} ${it.cause} ${it.stackTrace}")
                        }
                }
            }
        }
    }

    private fun addGroupChatsToUsers(data: ItemPendaftarProgram) {
        val mDb = FirebaseFirestore.getInstance()

        mDb
            .collection("chats")
            .whereEqualTo("lecturerId", selectedDospem?.nip)
            .whereEqualTo("tahun", data.tahun)
            .whereEqualTo("semester", data.semester)
            .get()
            .addOnSuccessListener { dt ->
                val groupChatData = hashMapOf(
                    "idPendaftarMbkm" to data.id,
                    "semester" to data.semester,
                    "tahun" to data.tahun)

                if(dt!=null && !dt.isEmpty && dt.size() > 0){
                    mDb
                        .collection("users").document(data.npm.toString())
                        .collection("groupChats").document(dt.documents.first().id)
                        .set(groupChatData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Users added to Group Chats", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.e("error new group chats", "${it.message} ${it.cause} ${it.stackTrace}")
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error get group chats", "${it.message} ${it.cause} ${it.stackTrace}")
            }


    }

    private fun addNotif(getData: ItemPendaftarProgram, title: String, subtitle: String) {
        FirebaseFirestore.getInstance()
            .collection("notification")
            .whereEqualTo("username", getData.npm)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d("cek noti", "${it.size()}")
                    createNewNotif(
                        getData.npm.toString(),
                        title, subtitle,
                        getIdPendaftar(requireContext())?.status.toString(),
                        getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                        requireContext()
                    )
                } else {
                    Log.d("cek notif else", "${it.size()}")
                    saveNotif(
                        getData.npm.toString(),
                        "Pembimbing MBKM",
                        "Anda telah mendapatkan dosen pembimbing. Konsultasikan hal-hal terkait program dan paket konversi kepada pembimbing Anda melalui fitur Group Chats.",
                        getIdPendaftar(requireContext())?.status.toString(),
                        getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                        requireContext()
                    )
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

    private fun deleteMemberGroupChats(getData: ItemPendaftarProgram, buildNewGc: Boolean) {
        FirebaseFirestore.getInstance()
            .collection("users").document(getData.npm.toString())
            .collection("groupChats")
            .whereEqualTo("semester", getData.semester.toString())
            .whereEqualTo("tahun", getData.tahun.toString())
            .get()
            .addOnSuccessListener { data ->
                if (data != null && data.documents != null) {
                    Log.d("cek data before delete", data.documents.size.toString()) //.first().data?.get("groupName").toString())

                    data.documents.forEach { dt ->
                        Log.d("cek datadocs", dt.data?.get("groupName").toString())

                        FirebaseFirestore.getInstance()
                            .collection("chats").document(dt.id)
                            .collection("members").document(getData.npm.toString())
                            .delete()
                            .addOnSuccessListener {
                                Log.d("cek update del memb", getData.npm.toString())
                                Toast.makeText(
                                    requireContext(),
                                    "Remove users from groupchats ${data.documents.first().data?.get("group_name")}",
                                    Toast.LENGTH_LONG
                                ).show()

                                FirebaseFirestore.getInstance()
                                    .collection("users").document(getData.npm.toString())
                                    .collection("groupChats").document(dt.id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Remove groupchats from users ${getData.npm}", Toast.LENGTH_LONG).show()
                                }.addOnFailureListener {
                                    Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("error remove members", "${it.message} ${it.cause} ${it.stackTrace}")
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("error savebnotif", "${e.message} ${e.cause} ${e.stackTrace}")
                            }
                    }

                    if(buildNewGc && selectedDospem != null) addNewGroupChats(getData, selectedDospem!!)
                    else addMemberToGroupChats(getData)
                    addGroupChatsToUsers(getData)

                } else {
                    Toast.makeText(requireContext(), "isNull : ${data}", Toast.LENGTH_SHORT).show()
                    Log.e("null getdata membersmsthn", "${data}")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error getdata delete gc", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

    private fun addMemberToGroupChats(getData: ItemPendaftarProgram) {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .whereEqualTo("lecturerId", selectedDospem?.nip.toString())
            .whereEqualTo("semester", getData.semester.toString())
            .whereEqualTo("tahun", getData.tahun.toString())
            .get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    if (!data.isEmpty) {
                        FirebaseFirestore.getInstance()
                            .collection("chats").document(data.documents.first().id)
                            .collection("members").document(getData.npm.toString())
                            .set(
                                hashMapOf(
                                    "avatar" to null,
                                    "description" to "${getData.namaPosisiKegiatanTopik}, ${
                                        getData.namaMitra}, ${getData.statusMbkm}",
                                    "idProgramMbkm" to getData.id,
                                    "name" to getData.nama,
                                    "npm" to getData.npm
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Added to New Group Chats",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if(changedospem) addNotif(getData, "Perubahan Pembimbing MBKM",
                                    "Pembimbing MBKM anda berhasil diubah! Periksa data Pembimbing MBKM baru dan konsultasikan hal-hal terkait program MBKM kepada pembimbing Anda melalui fitur Group Chats.")
                                else addNotif(getData, "Pembimbing MBKM",
                                    "Anda telah mendapatkan dosen pembimbing. Konsultasikan hal-hal terkait program dan paket konversi kepada pembimbing Anda melalui fitur Group Chats.",)

                            }.addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Error : ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    "error new group chats",
                                    "${it.message} ${it.cause} ${it.stackTrace}"
                                )
                            }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "ItemPeriode Empty : ${data}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("empty getdata membersmsthn", "${data}")
                    }

                } else {
                    Toast.makeText(requireContext(), "isNull : ${data}", Toast.LENGTH_SHORT).show()
                    Log.e("null getdata membersmsthn", "${data}")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error getdata addmemb", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

    private fun addNewGroupChats(getData: ItemPendaftarProgram, selectedDospem: ItemLookupDospem) {
        val firestore = FirebaseFirestore.getInstance()
        val documentReference = firestore.collection("chats").document() // Generate document reference
        val documentId = documentReference.id // Get the document ID

        documentReference.set(
            GroupChats(
                groupId = documentId, // Set the groupId with the document ID
                groupName = "Bimbingan ${semester(getData.semester.toString())
                } ${getData.tahun} - ${selectedDospem.nama}",
                lastMessage = null,
                lecturerId = selectedDospem.nip.toString(),
//                members = listOf(selectedDospem.nip.toString(), getData.npm.toString()),
                timestamp = Timestamp.now().toDate(),
                semester = getData.semester.toString(),
                tahun = getData.tahun.toString(),
            )
        ).addOnSuccessListener {

            firestore
                .collection("chats").document(documentReference.id)
                .collection("members").document(getData.npm.toString())
                .set(
                    hashMapOf(
                        "avatar" to null,
                        "description" to "${getData.namaPosisiKegiatanTopik}, ${getData.namaMitra}, ${getData.statusMbkm}",
                        "idProgramMbkm" to getData.id,
                        "name" to getData.nama,
                        "npm" to getData.npm
                    )
                ).addOnSuccessListener {

                    firestore
                        .collection("users").document(selectedDospem.nip.toString())
                        .collection("groupChats").document(documentId)
                        .set(
                            hashMapOf(
                                "semester" to getData.semester,
                                "tahun" to getData.tahun
                            )
                        )

                    Log.d("cek Firestore", "Document ID successfully set with idChat: $documentId")
                    Toast.makeText(requireContext(), "Added to New Group Chats", Toast.LENGTH_SHORT).show()

                    if(changedospem) addNotif(getData, "Perubahan Pembimbing MBKM",
                        "Pembimbing MBKM anda berhasil diubah! Periksa data Pembimbing MBKM baru dan konsultasikan hal-hal terkait program MBKM kepada pembimbing Anda melalui fitur Group Chats.")
                    else addNotif(getData, "Pembimbing MBKM",
                        "Anda telah mendapatkan dosen pembimbing. Konsultasikan hal-hal terkait program dan paket konversi kepada pembimbing Anda melalui fitur Group Chats.",)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("error add members new group chats", "${it.message} ${it.cause} ${it.stackTrace}")
                }

            }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            Log.e("error new group chats", "${it.message} ${it.cause} ${it.stackTrace}")
        }
    }


//    private fun addNewGroupChats(getData: ItemPendaftarProgram, selectedDospem: ItemLookupDospem) {
//        FirebaseFirestore.getInstance()
//            .collection("chats")
//            .document()
//            .set(
//                GroupChats(
//                    groupId = null,
//                    groupName = "Bimbingan ${
//                        semester(getData.semester.toString())
//                    } ${getData.tahun} - ${selectedDospem.nama}",
//                    lastMessage = null,
//                    members = listOf(selectedDospem.nip.toString(), getData.npm.toString()),
//                    timestamp = Timestamp.now().toDate(),
//                    semester = getData.semester.toString(),
//                    tahun = getData.tahun.toString(),
//                )
//            ).addOnSuccessListener {
//                val documentId = FirebaseFirestore.getInstance().collection("chats").id
//
//                FirebaseFirestore.getInstance().collection("chats")
//                    .document().update("groupId", documentId)
//                    .addOnSuccessListener {
//                        Log.d(
//                            "cek Firestore", "Document ID successfully updated with idChat: $documentId"
//                        )
//                    }
//                    .addOnFailureListener { e ->
//                        // Handle any errors
//                        Log.e("Firestore", "Error updating idChat field: ", e)
//                    }
//
//                Toast.makeText(requireContext(), "Added to New Group Chats", Toast.LENGTH_SHORT)
//                    .show()
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
//                Log.e("error new group chats", "${it.message} ${it.cause} ${it.stackTrace}")
//            }
//    }

    private fun tabelListMatkul(getData: ItemPendaftarProgram) {
        val viewmodel = ViewModelProvider(this)[PaketKonversiViewModel::class.java]
        viewmodel.getliveDataPaket(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftarMBKM = getData.id.toString(),
            idProgramProdi = getData.idProgramProdi.toString(),
            idJenisMbkm = getData.idJenisMbkm.toString(),
            idProgram = getData.idProgram.toString(),
            statusMbkm = getData.statusMbkm.toString()
        ).observe(viewLifecycleOwner) {
            var sks = 0
            if(it!=null && it.data!=null) {
                binding.cardPaketkonversi.visibility = View.VISIBLE
                binding.txtEmptyPaket.visibility = View.GONE
                binding.tableLayout.removeAllViews()
                binding.tableLayout.addView(binding.tableRow)
                val tableRow = LayoutInflater.from(requireContext())
                    .inflate(R.layout.tablerow_paket, null) as TableRow

                val a = tableRow.findViewById<TextView>(R.id.txt_kodemk)
                a.text = "Kode"
                a.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

                val b = tableRow.findViewById<TextView>(R.id.txt_mk)
                b.text = "Mata Kuliah"
                b.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

                val c = tableRow.findViewById<TextView>(R.id.txt_sksmk)
                c.text = "SKS"
                c.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)

                binding.tableLayout.addView(tableRow)

                it.data.listMatkul?.map {
                    val tableRow = LayoutInflater.from(requireContext())
                        .inflate(R.layout.tablerow_paket, null) as TableRow
                    tableRow.findViewById<TextView>(R.id.txt_kodemk).text = it?.kodeMatkul
                    tableRow.findViewById<TextView>(R.id.txt_mk).text = it?.namaMatkul
                    tableRow.findViewById<TextView>(R.id.txt_sksmk).text = it?.sks
                    binding.tableLayout.addView(tableRow)
                    sks = sks + (it?.sks?.toInt() ?: 0)
                }
            } else {
                binding.cardPaketkonversi.visibility = View.GONE
                binding.txtEmptyPaket.visibility = View.VISIBLE
            }
            binding.txtTotalsks.text = "Total SKS : $sks"
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
            if (binding.linearHidden.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.cardPaketkonversi, AutoTransition())
                binding.linearHidden.visibility = View.GONE
                binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(binding.cardPaketkonversi, AutoTransition())
                binding.linearHidden.visibility = View.VISIBLE
                binding.imageButton.rotation = 90F
            }
        }

        binding.imageButton.setOnClickListener {
            if (binding.linearHidden.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.cardPaketkonversi, AutoTransition())
                binding.linearHidden.visibility = View.GONE
                binding.imageButton.rotation = 270F
            } else {
                TransitionManager.beginDelayedTransition(binding.cardPaketkonversi, AutoTransition())
                binding.linearHidden.visibility = View.VISIBLE
                binding.imageButton.rotation = 90F
            }
        }
    }

    private fun bindingTxt(getData: ItemPendaftarProgram) {
        binding.apply {
            txtNpm.text = getData.npm
            txtNama.text = getData.nama
            txtNip.text = if (getData.nip.isNullOrEmpty()) "-" else getData.nip
            txtPosisi.text = getData.namaPosisiKegiatanTopik
            txtAwalkeg.text =
                if (getData.awalKegiatan.isNullOrEmpty()) "-" else getData.awalKegiatan
            txtAkhirkeg.text =
                if (getData.akhirKegiatan.isNullOrEmpty()) "-" else getData.akhirKegiatan
            txtPeriode.text =
                "Semester ${semester(getData.semester.toString())} Tahun ${getData.tahun}"
            if (getData.namaPegawai.isNullOrEmpty()) {
                txtNamadospem.text = "-"
                txtStatus.text = "Belum Dosen Pembimbing"
                cardStatus.setCardBackgroundColor(Color.RED)
                txtStatus.setTextColor(Color.WHITE)
            } else {
                txtNamadospem.text = getData.namaPegawai
                txtStatus.text = "Sudah Dosen Pembimbing"
                cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
            }
            txtTotalsks.text

            if (getData.namaPegawai.isNullOrBlank()) {
                btnDelete.visibility = View.GONE
                editPembimbing.isEnabled = true
                btnSimpan.visibility = View.GONE
            } else {
                btnDelete.visibility = View.VISIBLE
                btnSimpan.visibility = View.GONE
                editPembimbing.setText(getData.namaPegawai)
                editPembimbing.isEnabled = false
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
//            requireActivity().onBackPressed()
//        fragmentManager?.popBackStack()
//            childFragmentManager.popBackStack()
            findNavController().popBackStack()
        }
    }
}