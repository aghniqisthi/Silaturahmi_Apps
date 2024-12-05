package com.upnvjatim.silaturahmi.mahasiswa.luaran

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.FragmentListLuaranBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.response.DataLuaran
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.LuaranViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListLuaranMhsAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ListLuaranFragment : Fragment() {
    private var _binding: FragmentListLuaranBinding? = null
    private val binding get() = _binding!!
    private val luaranViewModel: LuaranViewModel by activityViewModels()
    private val listLuaran = mutableListOf<DataLuaran>()
    private lateinit var adapter: ListLuaranMhsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListLuaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireActivity(), TambahLuaranActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadData() {
        val viewmodel = ViewModelProvider(this)[LuaranViewModel::class.java]

        viewmodel.getliveDataAllLuaran(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            id = getIdPendaftar(requireContext())?.id.toString())

        viewmodel.liveDataAllLuaran.observe(viewLifecycleOwner) {
            listLuaran.clear()

            if (it != null && it.data != null && it.data.any { it?.isDeleted == false }) {
                binding.linearEmptydata.visibility = View.GONE
                it.data.sortedByDescending { it?.updatedAt ?: it?.createdAt }.map {
                    if (it != null) {
                        listLuaran.add(it)
                    }
                }

                binding.rvLuaran.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = ListLuaranMhsAdapter(listLuaran.filter { it.isDeleted == false })
                adapter.submitList(listLuaran.filter { it.isDeleted == false })
                binding.rvLuaran.adapter = adapter
                adapter.setOnItemClickListener { data ->
                    PendaftaranDialogFragment(
                        false, "Konfirmasi Luaran",
                        "Apakah Anda yakin ingin menghapus data Luaran?", true,
                        { deleteLuaran(data) }
                    ).show(requireActivity().supportFragmentManager, "TAG DELETE LUARAN")
                }

            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteLuaran(data: DataLuaran) {
        Toast.makeText(requireContext(), "hapus luaran", Toast.LENGTH_SHORT).show()
        /// TODO : hapus luaran
        val npm = getUser(requireContext())?.user?.username.toString()
        luaranViewModel.deleteLuaran(data.id.toString(), requireContext())
        luaranViewModel.liveDataDeleteLuaran.observe(viewLifecycleOwner){
            if(it.data == "success"){
                Toast.makeText(requireContext(), "Luaran berhasil dihapus", Toast.LENGTH_SHORT).show()
                listLuaran.remove(data)
                adapter.notifyDataSetChanged()

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", npm)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(
                                npm, "Luaran MBKM",
                                "Anda berhasil menghapus data luaran akhir.",
                                getIdPendaftar(requireContext())?.status.toString(),
                                getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                requireContext())
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(
                                npm, "Luaran MBKM", "Anda berhasil menghapus data nilai akhir. ",
                                getIdPendaftar(requireContext())?.status.toString(),
                                getIdPendaftar(requireContext())?.isFinish.toString().toBoolean(),
                                requireContext())
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
            } else Toast.makeText(requireContext(), "Failed : ${it.data}", Toast.LENGTH_SHORT).show()
        }
    }
}