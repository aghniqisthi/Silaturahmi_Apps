package com.upnvjatim.silaturahmi.dosen.pembimbing.message

import android.content.ActivityNotFoundException
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentRiwayatBimbinganBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.GroupChats
import com.upnvjatim.silaturahmi.model.request.UserGroupChats
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.model.response.ItemPeriode
import com.upnvjatim.silaturahmi.model.response.ResponsePendaftarProgram
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.semester
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelGroupChat
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.RiwayatBimbinganAdapter
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import kotlinx.coroutines.runBlocking

class RiwayatBimbinganFragment : Fragment() {
    private var _binding: FragmentRiwayatBimbinganBinding? = null
    private val binding get() = _binding!!
    private val viewmodelPendaftar: PendaftarProgramViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBimbinganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        val role = getUser(requireContext())?.user?.role?.id.toString()
        val nip = getUser(requireContext())?.user?.nip
        val username = getUser(requireContext())?.user?.username.toString()
        val viewmodelPendaftar = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]

        binding.rvRiwayatchats.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.swipeRefresh.setOnRefreshListener {
            getGroupChats(role, nip, username)
        }

        if (getUser(requireContext())?.user?.role?.id == "HA02") {
            (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            binding.toolbar.isTitleCentered = false
            binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
            binding.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        val idPegawai = getUser(requireContext())?.user?.idPegawai.toString()
//        "674c6930-80cf-11ed-8ea8-4ee667c77598"

        getGroupChats(role, nip, username)

        if (role == "HA02") binding.btnUpdategroup.visibility = View.GONE
        else {
            binding.btnUpdategroup.visibility = View.VISIBLE
            viewmodelPendaftar.callApiPeriodeActive("bearer ${getUser(requireContext())?.token?.AccessToken}")
            viewmodelPendaftar.liveDataPeriodeActive.observe(viewLifecycleOwner) { periode ->

                val idPeriode = periode.data.items?.firstOrNull()?.id
//                 "ef971e93-67fd-448e-bc3a-9f380d4223cf"

                binding.btnUpdategroup.setOnClickListener {
                    viewmodelPendaftar.callApiPendaftarProgram(
                        authHeader = "bearer ${getUser(requireContext())?.token}",
                        pageNumber = 1, pageSize = 50, status = null, query = null,
                        idPeriode = idPeriode,
                        idPegawai = idPegawai,
                        idProgramProdi = // null,
                        getUser(requireContext())?.user?.idProgramProdi.toString(),
                        statusMbkm = null, idJenisMbkm = null, idProgram = null,
                        sortByStatus = null, sortBy = null, sortType = null)
                    updateGroup(periode.data.items?.firstOrNull(), role, nip.toString(), username)
                }
            }
        }

//        viewModelGc.isLoading.observe(viewLifecycleOwner) {
//            if (it) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.GONE
//        }
//
//        viewModelGc.isError.observe(viewLifecycleOwner) {
//            MaterialAlertDialogBuilder(requireContext())
//                .setBackground(requireContext().getDrawable(R.drawable.modal_login_badreq))
//                .setTitle(it).setPositiveButton("Ok", null).show()
//        }
    }


    private fun getGroupChats(role: String, nip: String?, username: String) {
        val viewmodelGc = ViewModelProvider(this)[ViewModelGroupChat::class.java]

        FirebaseFirestore.getInstance()
            .collection("users").document(
                if (role == "HA02") username
                else if (nip != "" && nip != null && nip != "null") nip.toString()
                else username
            )
            .collection("groupChats").get()
            .addOnSuccessListener { gc ->
                Log.d("cek gccc [get data all gc ; 1/last]",
                    "${if (role == "HA02") "username"
                    else if (nip != "" && nip != null && nip != "null") "nip"
                    else "username"} : ${if (role == "HA02") username
                    else if (nip != "" && nip != null && nip != "null") nip.toString()
                    else username} ; ${gc.size()}") //; ${gc.documents.first().data?.get("groupName")} ${gc.documents.first().id}")

                if (gc.documents.isEmpty() || gc.documents.size < 1) binding.linearEmptydata.visibility = View.VISIBLE
                else {
                    binding.linearEmptydata.visibility = View.GONE
                    val groupChats = mutableListOf<GroupChats>()
                    runBlocking {
                        gc.documents.forEach {
                            val groupId = it.id
                            val groupChatDetails = viewmodelGc.fetchGroupChatDetails(groupId)
                            groupChatDetails?.let {
                                groupChats.add(it)
                                Log.d("cek data user groupchats [2/last+1]", "${groupId} ; ${it.groupName} ; ${it.lastMessage}")
                            }
                        }

                        val adapter = RiwayatBimbinganAdapter(groupChats)
                        adapter.notifyDataSetChanged()
                        binding.rvRiwayatchats.adapter = adapter
                    }
                }
                binding.swipeRefresh.isRefreshing = false
            }.addOnFailureListener { e ->
                MaterialAlertDialogBuilder(requireContext())
                    .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.modal_login_badreq))
                    .setTitle("Failed ${e.message}").setPositiveButton("Ok", null).show()
                Log.e("error fetch gc", "${e.message} ${e.cause} ${e.stackTrace}")
                binding.swipeRefresh.isRefreshing = false
            }

        viewmodelGc.isError.observe(viewLifecycleOwner){
            binding.linearEmptydata.visibility = View.VISIBLE
            binding.txtEmpty.text = it.toString()
        }
    }

    private var idGc : String? = null
    private val dataMembGc: MutableList<UserGroupChats> = mutableListOf()
    private val dataMhsApi: MutableList<ItemPendaftarProgram> = mutableListOf()

    private fun updateGroup(periode: ItemPeriode?, role: String, nip: String, username: String) {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .whereEqualTo("lecturerId",
                if (getUser(requireContext())?.user?.nip != null
                    && !getUser(requireContext())?.user?.nip.isNullOrBlank()
                ) getUser(requireContext())?.user?.nip
                else getUser(requireContext())?.user?.username.toString()
            ).whereEqualTo("semester", periode?.semester)
            .whereEqualTo("tahun", periode?.tahun)
            .get()
            .addOnSuccessListener { gc ->

                val listGc = gc.documents
                Log.d("cek getdata gc [1]", "${gc.documents.size}")

                if(view != null) {
                    viewmodelPendaftar.liveDataPendaftarProgram.observe(viewLifecycleOwner) { mhs ->

                        mhs.data?.items?.map {
                            if (it != null) {
                                dataMhsApi.add(it)
                            }
                        }

                        if (listGc != null && listGc.isNotEmpty() == true && listGc.size > 0) {

                            idGc = listGc.firstOrNull()?.id ?: ""

                            FirebaseFirestore.getInstance()
                                .collection("chats").document(idGc.toString())
                                .collection("members")
                                .get().addOnSuccessListener { memb ->
                                    memb.documents.map {
                                        dataMembGc.add(
                                            UserGroupChats(
                                                idProgramMbkm = it.data?.get("idProgramMbkm").toString(),
                                                name = it.data?.get("name").toString(),
                                                description = it.data?.get("description").toString(),
                                                npm = it.data?.get("npm").toString(),
                                                avatar = it.data?.get("avatar").toString()
                                            )
                                        )
                                    }

                                    Log.d("cek docs members ${memb.documents.size} [3]", dataMembGc.size.toString())

                                    if (dataMembGc.size > 0 && dataMhsApi.size > 0 && idGc != null
            //                                    mhs != null && mhs.data != null && (mhs.data.items?.size ?: 0) > 0
            //                                    && memb != null && memb.size() > 0 && mhs.data != null
            //                                    && mhs.data.items != null && mhs.data.items.size > 0
                                        && gc != null && !gc.isEmpty && periode != null) {

                                        Log.d("cek datas mhs [4]", "$idGc ; datamembgc ${dataMembGc.size}")
                                        val data = memb.map {
                                            UserGroupChats(
                                                idProgramMbkm = it.data.get("idProgramMbkm").toString(),
                                                name = it.data.get("name").toString(),
                                                description = it.data.get("description").toString(),
                                                npm = it.data.get("npm").toString(),
                                                avatar = it.data.get("avatar").toString()
                                            )
                                        }

                                        addMhsToFirestore(gc.first().id, periode)
                                        deleteMembersFromGc(gc.first().id)

                                    } else if (dataMembGc.size < 1 && dataMhsApi.size > 0
                                        && idGc != null && !listGc.isEmpty()) {
                                        Log.d(
                                            "cek data dataMembGc.size < 1 && dataMhsApi.size > 0  [4]",
                                            "membgc ${dataMembGc.size} ; mhsapi ${dataMhsApi.size}") // .first().npm} ; ${data.first().name}")
                                        addAllMembers(idGc.toString(), role, nip, username)

                                    } else if (dataMembGc.size > 0 && dataMhsApi.size < 1
                                        && idGc != null && !listGc.isEmpty()) {
            //                                } else if ((memb == null || memb.size() < 1) && !listGc.isEmpty()) {
            //                                    val data = memb.map {
            //                                        UserGroupChats(
            //                                            idProgramMbkm = it.data.get("idProgramMbkm").toString(),
            //                                            name = it.data.get("name").toString(),
            //                                            description = it.data.get("description").toString(),
            //                                            npm = it.data.get("npm").toString(),
            //                                            avatar = it.data.get("avatar").toString()
            //                                        )
            //                                    }
                                        Log.d("cek data dataMembGc.size > 0 && dataMhsApi.size < 1  [4]",
                                            "membgc ${dataMembGc.size} ; mhsapi ${dataMhsApi.size}")
                                        // .first().npm} ; ${data.first().name}")
                                        mhs.data?.items?.let { deleteMembersFromGc(idGc.toString()) }
                                    }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("error", "${e.message} ${e.cause} ${e.stackTrace}")
                                }
                        }
                        else newGroup(mhs)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("error get gc", "${e.message} ${e.cause} ${e.stackTrace}")
            }
    }

    private fun deleteMembersFromGc(idGc: String) {

        val firestore = FirebaseFirestore.getInstance()
        val batch = firestore.batch()

        val itemsToDelete = dataMembGc.filterNot { data ->
            data.npm != null && dataMhsApi.any { dt -> dt?.npm == data.npm }
        }

        if (itemsToDelete.isEmpty()) {
            Log.d("cek deleteMembersFromGc", "No items to delete.")
            return // Exit if there’s nothing to delete
        }

        itemsToDelete.map {
            Log.d("cek itemstodelete", it.npm.toString())
            batch.delete(
                firestore
                    .collection("chats").document(idGc)
                    .collection("members").document(it.npm.toString())
            )

            batch.delete(
                firestore
                    .collection("users").document(it.npm.toString())
                    .collection("groupChats").document(idGc)
            )
        }

        batch.commit().addOnSuccessListener { s ->
            Log.d("cek success removed [8]", "removed data from group chat")
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("error delete users from chats by npm", "${e.message} ${e.cause} ${e.stackTrace}")
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
                        getData.npm.toString(), title, subtitle,
                        getData.status.toString(), getData.isFinish.toString().toBoolean(),
                        requireContext()
                    )
                } else {
                    Log.d("cek notif else", "${it.size()}")
                    saveNotif(
                        getData.npm.toString(),
                        "Pembimbing MBKM",
                        "Anda telah mendapatkan dosen pembimbing. " +
                                "Konsultasikan hal-hal terkait program dan paket konversi kepada pembimbing Anda " +
                                "melalui fitur Group Chats.",
                        getData.status.toString(), getData.isFinish.toString().toBoolean(),
                        requireContext()
                    )
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

    private fun addMhsToFirestore(
//        mhs: List<ItemPendaftarProgram?>?, memb: List<UserGroupChats>,
        idGc: String, periode: ItemPeriode
    ) {

        val firestore = FirebaseFirestore.getInstance()
        val batch = firestore.batch()

        val getTasks = dataMhsApi.filterNot { item -> dataMembGc.map { it.npm }.contains(item.npm) }.map { item ->

            firestore
                .collection("users").document(item.npm.toString())
                .get().addOnSuccessListener { dataUser ->

                    val memberData =
                        if (dataUser != null && dataUser.data != null && dataUser.id == item.npm) {
                            hashMapOf(
                                "avatar" to dataUser.data?.get("avatar"),
                                "description" to "${item.namaPosisiKegiatanTopik}, ${item.namaMitra}, ${
                                    item.statusMbkm}",
                                "idProgramMbkm" to item.id,
                                "name" to item.nama,
                                "npm" to item.npm
                            )
                        } else {
                            hashMapOf(
                                "avatar" to null,
                                "description" to "${item.namaPosisiKegiatanTopik}, ${item.namaMitra}, ${item.statusMbkm}",
                                "idProgramMbkm" to item.id,
                                "name" to item.nama,
                                "npm" to item.npm
                            )
                        }

                    batch.set(
                        firestore
                            .collection("chats").document(idGc)
                            .collection("members").document(item.npm.toString()),
                        memberData
                    )

                    // Add to user's groupChats collection
                    batch.set(
                        firestore
                            .collection("users").document(item?.npm.toString())
                            .collection("groupChats").document(idGc),
                        hashMapOf(
                            "idPendaftarMbkm" to item?.id,
                            "semester" to item?.semester,
                            "tahun" to item?.tahun
                        )
                    )

                }.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("error add users to gc", "${e.message} ${e.cause} ${e.stackTrace}")
                }
        }

        Tasks.whenAllComplete(getTasks).addOnSuccessListener {

            batch.commit().addOnSuccessListener { s ->
                Log.d("cek success add to gc [5]", "$s")
                Toast.makeText(requireContext(), "success add data to group chats", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("error add users to gc", "${e.message} ${e.cause} ${e.stackTrace}")
            }

        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("error add users to gc", "${e.message} ${e.cause} ${e.stackTrace}")
        }


        FirebaseFirestore.getInstance()
            .collection("users")
            .document(
                if (getUser(requireContext())?.user?.nip.isNullOrBlank())
                    getUser(requireContext())?.user?.username.toString()
                else getUser(requireContext())?.user?.nip.toString()
            ).collection("groupChats").document(idGc)
            .set(hashMapOf("semester" to periode.semester, "tahun" to periode.tahun))
            .addOnSuccessListener { s ->

                Log.d("cek success add gc to users [6]", "$s ; ${
                    if (getUser(requireContext())?.user?.nip.isNullOrBlank())
                    getUser(requireContext())?.user?.username.toString()
                else getUser(requireContext())?.user?.nip.toString()}")
                Toast.makeText(requireContext(), "success add $idGc to users", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("error add gc to users", "${e.message} ${e.cause} ${e.stackTrace}")
            }
    }

    private fun newGroup(mhs: ResponsePendaftarProgram) {

        val firestore = FirebaseFirestore.getInstance()
        val documentId = firestore.collection("chats").document().id // Get the document ID
        val dataGc = mhs.data?.items?.firstOrNull()
        val batch = firestore.batch()

        if(dataGc != null){
            firestore
                .collection("chats").document(documentId)
                .set(
                    GroupChats(
                        groupId = documentId,
                        groupName = "Bimbingan ${semester(dataGc.semester.toString())} ${dataGc.tahun} - ${
                            getUser(requireContext())?.user?.name.toString()}",
                        lastMessage = null,
                        lecturerId = if (getUser(requireContext())?.user?.nip.isNullOrBlank())
                            getUser(requireContext())?.user?.username.toString()
                        else getUser(requireContext())?.user?.nip.toString(),
                        timestamp = Timestamp.now().toDate(),
                        semester = dataGc.semester,
                        tahun = dataGc.tahun
                    )
            ).addOnSuccessListener {

                Log.d("cek new group [3]", "${dataGc.npm} ; ${documentId}")

                val getUserTasks = mhs.data.items.map {

                    firestore
                        .collection("users").document(it?.npm.toString()) // buat get avatar
                        .get().addOnSuccessListener { dataUser ->

                            batch.set(
                                firestore
                                    .collection("chats").document(documentId)
                                    .collection("members").document(it?.npm.toString()),
                                hashMapOf(
                                "idProgramMbkm" to it?.id,
                                "name" to it?.nama,
                                "description" to "${it?.namaPosisiKegiatanTopik}, ${it?.namaMitra}, ${it?.statusMbkm}",
                                "npm" to it?.npm,
                                "avatar" to dataUser.data?.get("avatar")
                            ))

                            batch.set(
                                firestore
                                    .collection("users").document(it?.npm.toString())
                                    .collection("groupChats").document(documentId),
                                hashMapOf(
                                    "idPendaftarMbkm" to it?.id,
                                    "semester" to it?.semester,
                                    "tahun" to it?.tahun
                                )
                            )

                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT
                            ).show()
                            Log.e("error add members to new gc",
                                "${it.message} ${it.cause} ${it.stackTrace}")
                        }
                }

                val nip = getUser(requireContext())?.user?.nip
                val username = getUser(requireContext())?.user?.username

                Tasks.whenAllComplete(getUserTasks).addOnSuccessListener {

                    firestore
                        .collection("users")
                        .document(
                            if (nip != "" && nip != null && nip != "null") nip else username.toString()
                        ).collection("groupChats").document(documentId)
                        .set(hashMapOf("semester" to dataGc.semester, "tahun" to dataGc.tahun))

                    batch.commit().addOnSuccessListener { a ->
                        Log.d("cek success add memb to new gc and users [4]", "$a")
                        try {
                            if (isAdded && context != null) {
                                val role = getUser(requireContext())?.user?.role?.id.toString()
                                val username = getUser(requireContext())?.user?.username.toString()
                                val nip = getUser(requireContext())?.user?.nip
                                getGroupChats(role, nip, username)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error add members to users", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("error batch members to new gc", "${it.message} ${it.cause} ${it.stackTrace}")
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error add new gc", "${it.message} ${it.cause} ${it.stackTrace}")
            }
        }
        else Toast.makeText(
            requireContext(),
            "Data${
                if(mhs.data?.items == null || mhs.data.items.size < 1) " mahasiswa" 
                else if (dataGc?.idPeriode == null || dataGc?.idPeriode == "") " periode"
                else ""
            } tidak ditemukan", Toast.LENGTH_SHORT).show()
    }

    private fun addAllMembers(
//        mhs: ResponsePendaftarProgram, memb: List<UserGroupChats>,
        id: String, role: String, nip: String, username: String
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val batch = firestore.batch()
        Log.d("cek addallmemb [5]", dataMhsApi.size.toString())

        val taskAddMemb = dataMhsApi.map {
            FirebaseFirestore.getInstance()
                .collection("users").document(it?.npm.toString()).get()
                .addOnSuccessListener { user ->

                    batch.set(
                        firestore
                            .collection("chats").document(id)
                            .collection("members").document(it?.npm.toString()),
                        hashMapOf(
                            "idProgramMbkm" to it?.id,
                            "name" to it?.nama,
                            "description" to "${it?.namaPosisiKegiatanTopik}, ${it?.namaMitra}, ${it?.statusMbkm}",
                            "npm" to it?.npm,
                            "avatar" to user.data?.get("avatar")
                        ))
                }
        }

        Tasks.whenAllComplete(taskAddMemb).addOnSuccessListener {
        batch.commit().addOnSuccessListener {
            Log.d("cek add all mhs to chats [6]", "Document ID successfully add")
            getGroupChats(role, nip, username)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            Log.e("error add mhs to chats", "${it.message} ${it.cause} ${it.stackTrace}")
        }
    }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("error", "${e.message} ${e.cause} ${e.stackTrace}")
        }
    }

    override fun onDestroyView(){
        viewmodelPendaftar.liveDataPendaftarProgram.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }



}