package com.upnvjatim.silaturahmi.mahasiswa.riwayat

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentRiwayatBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.RiwayatViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.RiwayatAdapter

class RiwayatFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayShowHomeEnabled(true)
        binding.toolbar.isTitleCentered = false
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val viewmodelRiwayat = ViewModelProvider(this)[RiwayatViewModel::class.java]

        viewmodelRiwayat.getliveDataRiwayat(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}", pageNumber = 1, pageSize = 10
        )

        viewmodelRiwayat.liveDataRiwayat.observe(viewLifecycleOwner) { list->
            if (list != null && (list.data?.meta?.totalItems?:0) > 0) {
                binding.linearEmptydata.visibility = View.INVISIBLE
                binding.rvRiwayat.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                val adapter = RiwayatAdapter(list.data?.items!!)
                binding.rvRiwayat.adapter = adapter
                adapter.setOnItemClickListener { riwayat ->
                    val bundle = Bundle()
                    bundle.putParcelable("riwayat", riwayat)
                    findNavController().navigate(R.id.action_riwayatFragment_to_detailRiwayatFragment , bundle)
                }
            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Riwayat program kosong!", Toast.LENGTH_SHORT).show()
            }
            binding.textView5.text = "Jumlah Program: ${list.data?.meta?.totalItems}"
        }
        viewmodelRiwayat.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodelRiwayat.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }



    }
}