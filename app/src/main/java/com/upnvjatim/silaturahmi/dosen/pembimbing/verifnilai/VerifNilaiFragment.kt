package com.upnvjatim.silaturahmi.dosen.pembimbing.verifnilai

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentVerifNilaiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.AccVerifNilaiAdapter

class VerifNilaiFragment : Fragment() {
    private var _binding: FragmentVerifNilaiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerifNilaiBinding.inflate(inflater, container, false)
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

                binding.rvVerifNilai.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.rvVerifNilai.adapter = AccVerifNilaiAdapter(it.data?.items!!)
            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "kosong!", Toast.LENGTH_SHORT).show()
            }
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.swipeRefresh.isRefreshing = false
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
            pageNumber = 1, pageSize = 50,
            idPegawai = getUser(requireContext())?.user?.idPegawai.toString(),
            idProgramProdi = getUser(requireContext())?.user?.idProgramProdi.toString(),
            status = "2,3",
            idPeriode = null, // selectedPeriode
            query = null, //query
            statusMbkm = "MSIB,PMMB,MANDIRI", idJenisMbkm = null, idProgram = null,
            sortByStatus = null, sortBy = null, sortType = null,
        )
    }


}