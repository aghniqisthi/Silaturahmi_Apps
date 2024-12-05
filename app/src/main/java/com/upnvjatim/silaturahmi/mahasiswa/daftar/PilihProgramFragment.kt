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
import com.upnvjatim.silaturahmi.databinding.FragmentPilihProgramBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.viewmodel.LookupProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListProgramAdapter

class PilihProgramFragment : Fragment() {
    private var _binding: FragmentPilihProgramBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private var query: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPilihProgramBinding.inflate(inflater, container, false)
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

        val viewmodel = ViewModelProvider(this)[LookupProgramViewModel::class.java]
        viewmodel.getliveDataLookupProgram(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            pageNumber = 1, pageSize = 100, q = query
        ).observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvListprogram.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                val adapter = ListProgramAdapter(it.data?.items!!)
                binding.rvListprogram.adapter = adapter
//                adapter.setOnItemClickListener({
//                    val bundle = Bundle()
//                    bundle.putParcelable("selected_program", it)
//                    setFragmentResult("requestKey", bundle)
//                    findNavController().popBackStack()
//                })

                adapter.setOnItemClickListener { program ->
                    msibViewModel.programData.value = program
//                    val bundle = Bundle().apply {
//                        putParcelable("selected_program", program)
//                    }
//                    setFragmentResult("requestKey", bundle)
                    findNavController().popBackStack()  // Navigate back to chooseProgramFragment
                }
            } else {
                Toast.makeText(requireContext(), "Program kosong!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSearch.setOnClickListener {
            query = binding.editSearch.text.toString()
            viewmodel.getliveDataLookupProgram(
                authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
                pageNumber = 1, pageSize = 100, q = query
            )
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