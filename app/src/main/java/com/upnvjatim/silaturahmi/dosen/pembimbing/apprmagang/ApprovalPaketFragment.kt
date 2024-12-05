package com.upnvjatim.silaturahmi.dosen.pembimbing.apprmagang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentApprovalPaketBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.AccPaketAdapter

class ApprovalPaketFragment : Fragment() {
    private var _binding: FragmentApprovalPaketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentApprovalPaketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().enableEdgeToEdge()
        loadData()

        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        val viewmodel = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]
        viewmodel.liveDataPendaftarProgram.observe(viewLifecycleOwner) {
//        viewmodel.getliveDataPendaftarProgram().observe(viewLifecycleOwner) {
            if (it != null) {
                if((it.data?.meta?.totalItems ?: 0) < 1) binding.linearEmptydata.visibility = View.VISIBLE
                else binding.linearEmptydata.visibility = View.INVISIBLE

                binding.rvAccpaket.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val adapter = AccPaketAdapter(it.data?.items!!)
                binding.rvAccpaket.adapter = adapter
            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "kosong!", Toast.LENGTH_SHORT).show()
            }
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else {
                binding.swipeRefresh.isRefreshing = false
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    fun loadData() {
        val viewmodel = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]
        viewmodel.callApiPendaftarProgram(
            authHeader = "bearer ${getUser(requireContext())?.token}",
            pageNumber = 1, pageSize = 10,
            sortByStatus = "4,2,3", statusMbkm = "MSIB,PMMB,MANDIRI",
            /// TODO : CHECK AGAIN
            idPegawai = getUser(requireContext())?.user?.idPegawai.toString(),
            sortBy = "status", // statusselected > id ; notselected > status
            status = "0,2,4", // status = selectedStatus, //024 ; 2 telah ; 4 belum
            sortType = null, // statusselected > asc ; notselected > null
            idPeriode = null, // selectedPeriode
            query = null, //query
            idProgramProdi = null, idJenisMbkm = null, idProgram = null,
        )
    }
}