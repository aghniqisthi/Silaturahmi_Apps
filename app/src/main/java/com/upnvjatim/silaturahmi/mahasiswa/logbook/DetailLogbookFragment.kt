package com.upnvjatim.silaturahmi.mahasiswa.logbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentDetailLogbookBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.response.ItemLogbook
import com.upnvjatim.silaturahmi.viewmodel.LogbookViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.LogbookDocsAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class DetailLogbookFragment : Fragment() {
    private var _binding: FragmentDetailLogbookBinding? = null
    private val binding get() = _binding!!
    val logbookViewModel: LogbookViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar()

        val getData = arguments?.getParcelable("logbook") as ItemLogbook?
        bindingTxt(getData!!)

        binding.btnEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("logbook", getData)
            findNavController().navigate(
                R.id.action_detailLogbookFragment_to_editLogbookFragment,
                bundle
            )
        }

        binding.btnDelete.setOnClickListener {
            PendaftaranDialogFragment(
                false, "Konfirmasi Logbook", "Apakah Anda yakin ingin menghapus data Logbook?",
                true, { deleteDataLogbook(getData) }
            ).show(requireActivity().supportFragmentManager, "TAG DELETE LOGBOOK")
        }

    }

    private fun deleteDataLogbook(getData: ItemLogbook) {
        /// TODO : delete data logbook then navigate to list logbook
        logbookViewModel.liveDataDeleteDataLogbook(
            "bearer ${getUser(requireContext())?.token?.AccessToken}", getData.id.toString()
        ).observe(viewLifecycleOwner){
            if(it.data == "success") {
                Toast.makeText(requireContext(), "Logbook berhasil dihapus", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else Toast.makeText(requireContext(), "Failed: ${it.data}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindingTxt(getData: ItemLogbook) {
        binding.apply {

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
//            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Adjust for UTC if necessary
            val date = inputFormat.parse(getData.tanggal.toString())

            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

            txtTanggal.text = formattedDate
            txtJam.text = formattedTime
            txtKet.text = getData.deskripsi
            if (getData.link.isNullOrEmpty()) {
                emptydata.visibility = View.VISIBLE
            } else {
                emptydata.visibility = View.INVISIBLE
            }

            logbookViewModel.getliveDataDocsLogbook(
                "bearer ${getUser(requireContext())?.token?.AccessToken}", getData.id!!
            )
            logbookViewModel.liveDataDocsLogbook.observe(viewLifecycleOwner) {
                if (it != null && it.data != null && !it.data.isNullOrEmpty()) {
                    binding.emptydata.visibility = View.INVISIBLE
                    binding.rvDocslogbook.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    val adapter = it.data.let { it1 -> LogbookDocsAdapter(it1, false) }
                    binding.rvDocslogbook.adapter = adapter
                } else {
                    binding.emptydata.visibility = View.VISIBLE
                    binding.rvDocslogbook.visibility = View.INVISIBLE
                }
            }

            if(getUser(requireContext())?.user?.role?.id != "HA02"){
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }

    private fun toolbar() {
        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar
            ?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}