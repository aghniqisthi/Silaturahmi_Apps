package com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentTambahMatkulBinding
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.LoadMessageAdapter
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.daftar.MsibViewModel
import com.upnvjatim.silaturahmi.model.response.DetailsPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ItemAllMatkul
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import com.upnvjatim.silaturahmi.viewmodel.LookupAllMatkulViewModel
import com.upnvjatim.silaturahmi.viewmodel.LookupMkViewModelFactory
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TambahMatkulFragment : Fragment() {
    private var _binding: FragmentTambahMatkulBinding? = null
    private val binding get() = _binding!!

    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewmodelTranskrip: NilaiViewModel by activityViewModels()

    private var query: String? = null
    private var sks: MutableLiveData<Int> = MutableLiveData()
    private var arrayMk: MutableList<ItemAllMatkul>? = mutableListOf()

    private val viewmodel: LookupAllMatkulViewModel by viewModels { LookupMkViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahMatkulBinding.inflate(inflater, container, false)
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

//        val getData = arguments?.getParcelable("paket") as ItemLookupPaketKonversi?

//        Log.d("cek getdata", "${getData?.namaPaketKonversi} ; ${
//            getData?.detail?.firstOrNull()?.namaMatkul}")

        sks.value = 0
        msibViewModel.paketData.value?.detail?.forEach { getData ->
            arrayMk?.add(
                ItemAllMatkul(
                    createdAt = getData?.createdAt,
                    createdBy = getData?.createdBy,
                    id = getData?.idMatkul,
                    idSiamikFakjur = null,
                    isDeleted = false,
                    kdFakjur = null,
                    kodeMatkul = getData?.kodeMatkul,
                    namaMatkul = getData?.namaMatkul,
                    namaProdi = null,
                    sks = getData?.sks?.toInt()?:0,
                    updatedAt = getData?.updatedAt,
                    updatedBy = getData?.createdBy
                )
            )

            sks.value = (sks.value?.toInt()?:0) + (getData?.sks?.toInt()?:0)
        }

        val adapter = ListMkAdapter(viewmodelTranskrip.liveDataTranskrip.value, msibViewModel)
//        getData, arrayMk)
        getDataMatkul(adapter)

        bindingDetailPaket()

        binding.btnSearch.setOnClickListener {
            query = binding.editSearch.text.toString()
            lifecycleScope.launch {
                viewmodel.dataMk(
                    "bearer ${getUser(requireContext())?.token?.AccessToken}",
                    getUser(requireContext())?.user?.idProgramProdi.toString(), query
                ).observe(viewLifecycleOwner) { pagingData ->
                    Log.d("cek submitData search", pagingData.toString())
                    adapter.submitData(lifecycle, pagingData)
                }
            }
        }


    }

    private fun bindingDetailPaket() {
        sks.observe(viewLifecycleOwner) {
            binding.txtTotalsks.text = "Total SKS : $it"
            if(it > 20) Toast.makeText(requireContext(), "Total SKS tidak boleh > 20", Toast.LENGTH_SHORT)
            .show()
        }

        binding.btnSimpan.setOnClickListener {
            if ((sks.value?.toInt() ?: 0) > 20) {
                Toast.makeText(
                    requireContext(), "Total Konversi SKS tidak boleh lebih dari 20", Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun getDataMatkul(adapter: ListMkAdapter){
        viewmodelTranskrip.getliveDataTranskrip(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getUser(requireContext())?.user?.username.toString()
        )

        viewmodelTranskrip.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }

        viewmodelTranskrip.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        adapter.setOnItemClickListener { item, checkbox ->
            if (checkbox && (sks.value ?: 0) + (item.sks ?: 0) <= 20) {
                arrayMk?.add(item)
                updateDataViewModel()
                sks.value = (sks.value ?: 0) + (item.sks ?: 0)
            } else {
                arrayMk?.remove(item)
                arrayMk = arrayMk?.filter {
                    it.namaMatkul != item.namaMatkul
                            && it.kodeMatkul != item.kodeMatkul
                            && it.id != item.id
                }?.toMutableList()
                sks.value = (sks.value ?: 0) - (item.sks ?: 0)
                updateDataViewModel()
                Log.d("cek arraymk ${arrayMk?.size}", "${arrayMk?.map { it.namaMatkul } }")
            }
//            adapter.notifyDataSetChanged()
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            LoadMessageAdapter {
                Log.d("cek retry", "retry adapter")
                adapter.retry()
            }
        )

        viewmodel.dataMk(
                "bearer ${getUser(requireContext())?.token?.AccessToken}",
                getUser(requireContext())?.user?.idProgramProdi.toString(), query
            ).observe(viewLifecycleOwner) {
                Log.d("cek datamk", it.toString())
                adapter.submitData(lifecycle, it)
            }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }

        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }


    }

    private fun updateDataViewModel() {
        val det = arrayMk?.mapNotNull {
            if(it.kodeMatkul != null && it.kodeMatkul != "" && it.namaMatkul != null && it.namaMatkul != "")
                DetailsPaketKonversi(
                    id = null, idMatkul = it.id,
                    idPaketKonversi = msibViewModel.paketData.value?.id,
                    kodeMatkul = it.kodeMatkul,
                    namaMatkul = it.namaMatkul,
                    namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi,
                    sks = it.sks.toString(),
                    updatedAt = it.updatedAt.toString(), updatedBy = it.updatedBy.toString(),
                    createdAt = it.createdAt, createdBy = it.createdBy,
                ) else null
        }
        msibViewModel.paketData.value = ItemLookupPaketKonversi(
            createdAt = msibViewModel.paketData.value?.createdAt,
            createdBy = msibViewModel.paketData.value?.createdBy,
            deskripsi = msibViewModel.paketData.value?.deskripsi,
            detail = det,
            id = msibViewModel.paketData.value?.id,
            idJenisMbkm = msibViewModel.paketData.value?.idJenisMbkm,
            idProgram = msibViewModel.paketData.value?.idProgram,
            idProgramProdi = msibViewModel.paketData.value?.idProgramProdi,
            isDeleted = msibViewModel.paketData.value?.isDeleted,
            kdProdi = msibViewModel.paketData.value?.kdProdi,
            namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi,
            namaProdi = msibViewModel.paketData.value?.namaProdi,
            namaProgram = msibViewModel.paketData.value?.namaProgram,
            updatedAt = msibViewModel.paketData.value?.updatedAt,
            updatedBy = msibViewModel.paketData.value?.updatedBy
        )

        Log.d("cek msibviemodel update ${msibViewModel.paketData.value?.detail?.size}", "${
            msibViewModel.paketData.value?.detail?.map { "${it?.idMatkul} = ${it?.kodeMatkul} = ${it?.namaMatkul}" }
        }")
    }
}