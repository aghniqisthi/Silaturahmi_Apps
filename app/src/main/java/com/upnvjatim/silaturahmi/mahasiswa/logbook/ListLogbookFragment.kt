package com.upnvjatim.silaturahmi.mahasiswa.logbook

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentListLogbookBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemLogbook
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.viewmodel.LogbookViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.ListLogbookAdapter

class ListLogbookFragment : Fragment() {
    private var _binding: FragmentListLogbookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.isTitleCentered = false
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val viewmodelLogbook = ViewModelProvider(this)[LogbookViewModel::class.java]
        val getData = arguments?.getParcelable("pendaftar") as ItemPendaftarProgram?

        val listLogbook = mutableListOf<ItemLogbook>()
        val adapter = ListLogbookAdapter(listLogbook)

//        binding.btnSearch.setOnClickListener {
//            query = binding.editSearch.text.toString()
//            listLogbook.filter {
//                it.deskripsi == query
//            }
//            adapter.notifyDataSetChanged()
//            viewmodelLogbook.getliveDataListLogbook(
//                authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
//                pageNumber = 1, pageSize = 100, q = query,
//                idPendaftar = if(getData != null) getData.id.toString()
//                else getIdPendaftar(requireContext())?.id.toString(),
//            )
//        }

        binding.btnSearch.setOnClickListener {
            val query = binding.editSearch.text.toString()
            val filteredList = listLogbook.filter { logbook ->
                logbook.deskripsi?.contains(query, ignoreCase = true) == true // Case-insensitive search
            }

            adapter.updateData(filteredList)
            adapter.notifyDataSetChanged()
            binding.textView5.text = "Jumlah Kegiatan: ${if(filteredList.isNullOrEmpty()) 0 else filteredList.size}"
        }

        viewmodelLogbook.getliveDataAllListLogbook(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftar = //"b66e67e2-5140-11ee-829b-005056a0cd5a"
            if(getData != null) getData.id.toString()
            else getIdPendaftar(requireContext())?.id.toString(),
        )

        viewmodelLogbook.liveDataAllListLogbook.observe(viewLifecycleOwner) { list->
            if (list != null) {
                if((list.data?.size ?:0) < 1 || list.data == null){
                    binding.linearEmptydata.visibility = View.VISIBLE
                    if(getUser(requireContext())?.user?.role?.id == "HA02")
                        binding.txtEmpty.text = "Logbook Anda masih kosong.\nSilahkan isi dengan kegiatan Anda"
                    else binding.txtEmpty.text = "Logbook kosong"
                }
                else {
                    binding.linearEmptydata.visibility = View.GONE
                    binding.rvLogbook.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    listLogbook.clear()

                    list.data.map {
                        listLogbook.add(it)
                    }

                    adapter.submitList(listLogbook)
                    binding.rvLogbook.adapter = adapter
                    adapter.setOnItemClickListener { lgbk ->
                        val bundle = Bundle()
                        bundle.putParcelable("logbook", lgbk)
                        findNavController().navigate(
                            R.id.action_listLogbookFragment_to_detailLogbookFragment,
                            bundle
                        )
                    }
                }
                binding.textView5.text = "Jumlah Kegiatan: ${list.data?.size ?: 0}"
            } else {
                binding.linearEmptydata.visibility = View.VISIBLE
                binding.txtEmpty.text = "Logbook tidak dapat ditemukan"
            }
        }
        viewmodelLogbook.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodelLogbook.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            binding.txtEmpty.text = it
        }

        if(getData != null){
            requireActivity().enableEdgeToEdge()
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

            ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }

            binding.fabAdd.visibility = View.GONE
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_listLogbookFragment_to_tambahLogbookFragment)
        }
    }
}