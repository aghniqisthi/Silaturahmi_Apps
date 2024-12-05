package com.upnvjatim.silaturahmi.mahasiswa.luaran

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.upnvjatim.silaturahmi.databinding.FragmentListNilaiAkhirBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.openFile
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class NilaiAkhirFragment : Fragment() {
    private var _binding: FragmentListNilaiAkhirBinding? = null
    private val binding get() = _binding!!
    private val viewModelPendaftarProgram: PendaftarProgramViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListNilaiAkhirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        viewModelPendaftarProgram.callApiProgramByIdPendaftar(
            authHeader = "bearer ${getUser(requireContext())?.token?.AccessToken}",
            idPendaftar = getIdPendaftar(requireContext())?.id.toString()
        )

        viewModelPendaftarProgram.liveDataProgramById.observe(viewLifecycleOwner) {
            if (it != null && it.data != null && it.data.linkPenilaian != null && it.data.linkPenilaian != "") {
                binding.cardNilai.visibility = View.VISIBLE
                binding.linearEmptydata.visibility = View.GONE

                binding.txtKetNilai.text = it.data.ketPenilaian
                binding.txtNilai.text = it.data.nilaiAngka
                binding.btnDownload.setOnClickListener { a ->
                    if (it.data.linkPenilaian.isNullOrBlank())
                        Toast.makeText(
                            requireContext(), "File nilai akhir tidak ditemukan!",
                            Toast.LENGTH_SHORT
                        ).show()
                    else openFile(it.data.linkPenilaian.toString(), requireContext())
                }

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = inputFormat.parse(it.data.tglPenilaian)
                val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                binding.txtWaktuNilai.text = formattedDate

                if (it.data.status == 3 && it.data.isFinish == false) {
                    binding.txtStatus.text = "Penilaian Disetujui Dosen Pembimbing"
                    binding.txtStatus.setTextColor(Color.BLACK)
                    binding.cardStatus.setCardBackgroundColor(Color.parseColor("#B8DE39"))
                } else if (it.data.status == 3 && it.data.isFinish == true) {
                    binding.txtStatus.text = "Penilaian Disetujui Penilai Konversi"
                    binding.txtStatus.setTextColor(Color.WHITE)
                    binding.cardStatus.setCardBackgroundColor(Color.parseColor("#97C307"))
                } else {
                    binding.txtStatus.text = "Penilaian Belum Disetujui"
                    binding.txtStatus.setTextColor(Color.WHITE)
                    binding.cardStatus.setCardBackgroundColor(Color.parseColor("#FF3B30"))
                }

                binding.fabAdd.visibility = View.GONE
                binding.btnEdit.setOnClickListener { a ->
                    val intent = Intent(requireActivity(), TambahNilaiActivity::class.java)
                    intent.putExtra("nilai", it.data)
                    startActivity(intent)
                }

            }
            else {
                binding.cardNilai.visibility = View.INVISIBLE
                binding.fabAdd.visibility = View.VISIBLE

                binding.fabAdd.setOnClickListener {
                    val intent = Intent(requireActivity(), TambahNilaiActivity::class.java)
                    startActivity(intent)
                }

                binding.linearEmptydata.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        }
        viewModelPendaftarProgram.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewModelPendaftarProgram.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

}