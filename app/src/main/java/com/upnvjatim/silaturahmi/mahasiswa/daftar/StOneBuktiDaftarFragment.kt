package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.upnvjatim.silaturahmi.databinding.FragmentStOneBuktiDaftarBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import java.io.File


class StOneBuktiDaftarFragment : Fragment() {
    private var _binding: FragmentStOneBuktiDaftarBinding? = null
    private val binding get() = _binding!!
    private val msibViewModel: MsibViewModel by activityViewModels()
    private val viewModelPendaftarProgram: PendaftarProgramViewModel by activityViewModels()
    private var file: File? = null
    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStOneBuktiDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            "cek data pendaftar", "${
                getIdPendaftar(requireContext())?.link
            } ; ${
                getIdPendaftar(requireContext())?.nama
            } ; ${
                getIdPendaftar(requireContext())?.namaProgram
            } ; ${getIdPendaftar(requireContext())?.status}"
        )

        if (msibViewModel.fileName.value == null && getIdPendaftar(requireContext())?.link != null) {
            msibViewModel.fileName.value = getIdPendaftar(requireContext())?.link.toString()
            fileName = getIdPendaftar(requireContext())?.link.toString()
        } else {
            binding.uploadContainer.visibility = View.VISIBLE
            binding.btnFilebukti.visibility = View.GONE
        }

        msibViewModel.fileName.observe(viewLifecycleOwner) {
            if (it != null) {
                updateUIWithFile(it)
                fileName = it
            }
        }

        binding.uploadContainer.setOnClickListener {
            openForResult()
        }

        updateUIWithFile(fileName)

        msibViewModel.fileName.observe(viewLifecycleOwner) {
            fileName?.let {
                updateUIWithFile(it)
            }
        }
    }

    fun openForResult() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("*/*")
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri = result.data?.data!!
            uri.let {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                file = File(requireContext().cacheDir, getFileName(uri, requireContext()))
                inputStream?.use { input ->
                    file?.outputStream().use { output ->
                        input.copyTo(output!!)
                    }
                }
//                file = File(it.toString())
                msibViewModel.file.value = file

                val fileName = getFileName(uri, requireContext())
                msibViewModel.fileName.value = fileName
                Log.d("cek filename", "$fileName ; ${msibViewModel.file.value?.path}")

                updateUIWithFile(fileName)
            }
        }
    }
    private fun updateUIWithFile(fileName: String?) {
        if (fileName != null) {
            Log.d("cek file upd ui", fileName)
            binding.btnFilebukti.text = fileName
            binding.btnFilebukti.visibility = View.VISIBLE
            binding.btnFilebukti.setOnClickListener {
                openForResult()
            }
            binding.uploadContainer.visibility = View.GONE
        } else {
//            if(getIdPendaftar(requireContext())?.status != 0){
//                binding.btnFilebukti.isEnabled = false
//                binding.uploadContainer.visibility = View.GONE
//                binding.btnFilebukti.visibility = View.VISIBLE
//                binding.btnFilebukti.text = "Dokumen Pendaftaran tidak ditemukan"
//            }
//            else{
            binding.uploadContainer.visibility = View.VISIBLE
            binding.btnFilebukti.visibility = View.GONE
//            }
        }
    }

    private fun getFileName(uri: Uri, context: Context): String {
        var fileName = ""

        // Check if the URI scheme is "content"
        if (uri.scheme == "content") {
            // Query the content resolver for the display name
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                }
            }
        }

        // If the file name wasn't found, fall back to extracting it from the URI path
        if (fileName.isEmpty()) {
            fileName = uri.path?.substringAfterLast('/').toString()
        }

        return fileName
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


