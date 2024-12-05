package com.upnvjatim.silaturahmi.dosen.pic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentRevisiNilaiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.AccVerifNilaiAdapter

class RevisiNilaiFragment : Fragment() {
    private var _binding: FragmentRevisiNilaiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRevisiNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()

        val viewmodel = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]
        viewmodel.callApiPendaftarProgram(
            authHeader = "bearer ${getUser(requireContext())?.token}",
            pageNumber = 1, pageSize = 100, status = "1",
            idPeriode = null, // selectedPeriode
            query = null, //query

            idPegawai = null, idProgramProdi = null, // getUser(requireContext())?.user?.idProgramProdi.toString(),
            statusMbkm = null, idJenisMbkm = null, idProgram = null,
            sortByStatus = null, sortBy = null, sortType = null,
        )

        viewmodel.liveDataPendaftarProgram.observe(viewLifecycleOwner) {
//        viewmodel.getliveDataPendaftarProgram().observe(viewLifecycleOwner) {
            if (it != null) {
                if((it.data?.meta?.totalItems ?: 0) < 1) binding.linearEmptydata.visibility = View.VISIBLE
                else binding.linearEmptydata.visibility = View.INVISIBLE

                binding.txtJmlhmhs.text = getString(R.string.jmlh_mhs, it.data?.meta?.totalItems)
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recyclerView.adapter = it.data?.items?.let { it1 -> AccVerifNilaiAdapter(it1) }
            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "kosong!", Toast.LENGTH_SHORT).show()
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
//
//        val viewmodel = ViewModelProvider(this)[ViewModelPenukaran::class.java]

    }
}