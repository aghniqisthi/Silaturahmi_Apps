package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentPilihPaketKonversiBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.LookupPaketKonversiViewModel
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListPaketAdapter

class PilihPaketKonversiFragment : Fragment() {
    private var _binding: FragmentPilihPaketKonversiBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private var query: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPilihPaketKonversiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val viewmodel = ViewModelProvider(this)[LookupPaketKonversiViewModel::class.java]
        val viewmodelTranskrip = ViewModelProvider(this)[NilaiViewModel::class.java]

        viewmodel.getliveDataLookupPaketKonversi(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            pageNumber = 1, pageSize = 100,
            idProgramProdi = getUser(requireContext())?.user?.idProgramProdi.toString(),
            q = query
        )

        viewmodelTranskrip.getliveDataTranskrip(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getUser(requireContext())?.user?.username.toString()
        )
        viewmodel.liveDataLookupPaketKonversi.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvListpaket.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                viewmodelTranskrip.liveDataTranskrip.observe(viewLifecycleOwner) { tr ->
                    val adapter = ListPaketAdapter(it.data?.items!!, tr.data!!)
                    binding.rvListpaket.adapter = adapter
                    adapter.setOnItemClickListener { paket ->
                        msibViewModel.paketData.value = paket
                        findNavController().popBackStack()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Program kosong!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSearch.setOnClickListener {
            query = binding.editTextbox.text.toString()
            viewmodel.getliveDataLookupPaketKonversi(
                authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
                pageNumber = 1, pageSize = 100,
                idProgramProdi = getUser(requireContext())?.user?.idProgramProdi.toString(),
                q = query
            )
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