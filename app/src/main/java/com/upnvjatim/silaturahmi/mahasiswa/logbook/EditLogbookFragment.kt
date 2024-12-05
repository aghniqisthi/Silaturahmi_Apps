package com.upnvjatim.silaturahmi.mahasiswa.logbook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentEditLogbookBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.PostDataLogbook
import com.upnvjatim.silaturahmi.model.request.PostDocsLogbook
import com.upnvjatim.silaturahmi.model.request.PostLogbook
import com.upnvjatim.silaturahmi.model.response.DataDocsLogbook
import com.upnvjatim.silaturahmi.model.response.ItemLogbook
import com.upnvjatim.silaturahmi.viewmodel.LogbookViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.LogbookArrayDocsAdapter
import com.upnvjatim.silaturahmi.viewmodel.adapter.LogbookDocsAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditLogbookFragment : Fragment() {
    private var _binding: FragmentEditLogbookBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: LogbookViewModel by activityViewModels()

    private var tanggal: MutableLiveData<String?> = MutableLiveData()
    private var jam: MutableLiveData<String?> = MutableLiveData()

    private val listFile = mutableListOf<DataDocsLogbook>()
    private val listFileUnupload = mutableListOf<PostDocsLogbook>()
    private val listUploadedFile = mutableListOf<PostDataLogbook>()

    private lateinit var fileAdapter: LogbookDocsAdapter
    private lateinit var fileUnuploadAdapter: LogbookArrayDocsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar()

        val getData = arguments?.getParcelable<ItemLogbook>("logbook")

        tanggal.observe(viewLifecycleOwner) {
            binding.btnTanggal.text = it
        }

        jam.observe(viewLifecycleOwner) {
            binding.btnJam.text = it
        }

        binding.btnTanggal.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build()

            datePicker.show(requireActivity().supportFragmentManager, "Tag")
            datePicker.addOnPositiveButtonClickListener {
                val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
                Toast.makeText(requireContext(), formattedDate, Toast.LENGTH_SHORT).show()
                tanggal.value = formattedDate
            }
        }

        binding.btnJam.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().build()
            timePicker.show(requireActivity().supportFragmentManager, "Tag")
            timePicker.addOnPositiveButtonClickListener {
                val output = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
                jam.value = output
                Toast.makeText(requireContext(), "Time is: $output", Toast.LENGTH_SHORT).show()
            }
        }

        val date: Date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(getData?.tanggal)
        val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        val formattedTime: String = SimpleDateFormat("HH:mm").format(date)

        tanggal.value = formattedDate
        jam.value = formattedTime

        binding.editKet.setText(getData?.deskripsi)

        binding.rvDocslogbook.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        fileAdapter = LogbookDocsAdapter(listFile, true)
        fileAdapter.setOnItemClickListener { dt ->
            if(listFile.contains(dt)) {
                PendaftaranDialogFragment(
                    false,
                    "Konfirmasi Logbook",
                    "Apakah Anda yakin ingin menghapus dokumen pendukung Logbook?",
                    true, { deleteDocsLogbook(dt) }
                ).show(requireActivity().supportFragmentManager, "TAG DIALOG HAPUS")
            }
        }

        fileAdapter.submitList(listFile)
        binding.rvDocslogbook.adapter = fileAdapter

        viewmodel.getliveDataDocsLogbook(
            "bearer ${getUser(requireContext())?.token?.AccessToken}", getData?.id.toString()
        )
        viewmodel.liveDataDocsLogbook.observe(viewLifecycleOwner){
            if(it.data != null) {
                listFile.clear()
                it.data.map {
                    if(it != null){
                        listFile.add(it)
                        fileAdapter.notifyDataSetChanged()
                    }
                }
                listFile.map {
                    Log.d("cek submitlist", "${it.namaDokumen} ; ${it.fileDokumen}")
                }
            }
        }

        /// TODO : upload file after chosen, delete file after x clicked
        // make array list for file > if add docs then add to
        // make the icon for delete docs and remove from array different
        // one is trash and other is x
        // if simpan clicked, show popup

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        listFileUnupload.clear()
        fileUnuploadAdapter = LogbookArrayDocsAdapter(listFileUnupload)
        fileUnuploadAdapter.submitList(listFileUnupload)

        binding.rvArraydocs.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvArraydocs.adapter = fileUnuploadAdapter
        fileUnuploadAdapter.setOnItemClickListener {
            Log.d("cek unupfile", "${it.nama_dokumen} ; ${it.file.path}")
            if (listFileUnupload.contains(it)) listFileUnupload.remove(it)
            else Toast.makeText(requireContext(), "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            fileUnuploadAdapter.notifyDataSetChanged()
        }

        binding.uploadContainer.setOnClickListener {
            openForResult()
        }

        binding.btnSimpan.setOnClickListener {
            PendaftaranDialogFragment(false, "Konfirmasi Logbook", null,
                true, { uploadFilesSequentially(getData!!) }).show(
                requireActivity().supportFragmentManager, "TAG DETAIL ACC PROGRAM TIMMBKM")
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

    }

    private fun deleteDocsLogbook(dataDocsLogbook: DataDocsLogbook) {
        viewmodel.deleteDocsLogbook(dataDocsLogbook.fileDokumen.toString(), requireContext())
        viewmodel.liveDataDeleteDocsLogbook.observe(viewLifecycleOwner){
            if(it.data.success){
                Log.d("cek data del", "${dataDocsLogbook.namaDokumen} ; ${dataDocsLogbook.fileDokumen}")
                Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

                listFile.remove(dataDocsLogbook)
                listFile.map {
                    Log.d("cek listfile after remove", "${it.namaDokumen} ; ${it.fileDokumen}")
                }
                fileAdapter.notifyDataSetChanged()
            }
            else Toast.makeText(requireContext(), "${it.data.success} FAIL : ${
                it.data.message}", Toast.LENGTH_SHORT).show()
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }

        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            PendaftaranDialogFragment(true, null, null, true, null)
                .show(requireActivity().supportFragmentManager, "CustomDialog")
        }
    }

    fun openForResult() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("*/*")
        resultLauncher.launch(intent)
    }

    private var file: File? = null

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val uri: Uri = result.data?.data!!
                uri.let {
                    val inputStream = requireContext().contentResolver.openInputStream(it)
                    file = File(requireContext().cacheDir, getFileName(uri, requireContext()))
                    inputStream?.use { input ->
                        file?.outputStream().use { output ->
                            input.copyTo(output!!)
                        }
                    }

                    val fileName = getFileName(uri, requireContext())
                    listFileUnupload.add(
                        PostDocsLogbook(getIdPendaftar(requireContext())?.id.toString(), fileName, file!!)
                    )
                    fileUnuploadAdapter.notifyDataSetChanged()

                    var a = 0
                    listFileUnupload.map {
                        Log.d("cek filename - 1", "${a++} : ${it.nama_dokumen}")
                    }
                }
            }
        }
    }

    private fun uploadFilesSequentially(getData: ItemLogbook) {
        val totalFiles = listFileUnupload.size
//        var currentIndex = 0
        listUploadedFile.clear()

        // Function to handle the upload of a single file
        fun uploadNextFile() {
            if (listUploadedFile.size < totalFiles) {
                val fileItem = listFileUnupload[listUploadedFile.size]
                Log.d("cek listFileUnupload - 2", "${fileItem.nama_dokumen} ; ${fileItem.file.path}")
                Toast.makeText(requireContext(), "uploading file ${
                    fileItem.nama_dokumen}", Toast.LENGTH_SHORT).show()

                val requestImageFile = fileItem.file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file", fileItem.nama_dokumen, requestImageFile
                )
                val id = fileItem.idPendaftar.toRequestBody("text/plain".toMediaType())
                val namadocs = fileItem.nama_dokumen.toRequestBody("text/plain".toMediaType())

                // Call API to upload the file
                viewmodel.postDocsLogbook(fileMultipart, id, namadocs, requireContext())
                viewmodel.liveDataPostDocsLogbook.observe(viewLifecycleOwner) { data ->
                    if (data != null
                        && listUploadedFile.none { it.fileDokumen == data.data }
                        && listFile.none { it.fileDokumen == data.data }) {

                        listUploadedFile.add(PostDataLogbook(data.data, null, fileItem.nama_dokumen))
//                        currentIndex++
                        uploadNextFile()
                        Log.d("cek uploaded file - 4", "${listUploadedFile.size} -> ${data.data} ; ${
                            listUploadedFile} ; ${listFileUnupload.size}")
                    }
                }
            } else {
                saveLogbook(getData)
            }
        }

        if (listFileUnupload.isNotEmpty()) uploadNextFile()
        else {
            if(listUploadedFile.size == totalFiles) saveLogbook(getData)
        }
    }

//    private fun uploadFile(getData: ItemLogbook) {
//        listUploadedFile.clear()
//
//        if (listFileUnupload.size > 0) {
//            var totalFiles = listFileUnupload.size
//            var i = 0
//
//            listFileUnupload.forEach { fileItem ->
//                Log.d("cek listFileUnupload - 2", "${fileItem.nama_dokumen} ; ${fileItem.file.path}")
//                Toast.makeText(requireContext(), "upload file ${fileItem.nama_dokumen}", Toast.LENGTH_SHORT).show()
//
//                val requestImageFile = fileItem.file.asRequestBody("application/pdf".toMediaTypeOrNull())
//                val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//                    "file", fileItem.nama_dokumen, requestImageFile)
//                val id = fileItem.idPendaftar.toRequestBody("text/plain".toMediaType())
//                val namadocs = fileItem.nama_dokumen.toRequestBody("text/plain".toMediaType())
//
//                viewmodel.postDocsLogbook(fileMultipart, id, namadocs, requireContext())
//                viewmodel.liveDataPostDocsLogbook.observe(viewLifecycleOwner) { data ->
//                    if(data != null && listUploadedFile.none { it.fileDokumen == data.data }){
//                        listUploadedFile.add(PostDataLogbook(data.data, null, listFileUnupload[i].nama_dokumen))
//             Log.d("cek uploaded file - 4", "$i -> ${data.data} ; ${listUploadedFile} ; ${listFileUnupload.size}")
//                        i++
//                    }
//                }
//            }
//
//
//            /// krs ; kalender ; lembar nilai ; surat [ernyataan ; ak02
//            viewmodel.liveDataPostDocsLogbook.observe(viewLifecycleOwner) { data ->
//                Log.d("cek listupfile - 4", "${listUploadedFile.size} of ${totalFiles} files uploaded")
//                if (listUploadedFile.size == totalFiles) {
//                    saveLogbook(getData)
//                }
//            }
//
//            viewmodel.isLoading.observe(viewLifecycleOwner) {
//                if (it) binding.progressBar.visibility = View.VISIBLE
//                else binding.progressBar.visibility = View.INVISIBLE
//            }
//            viewmodel.isError.observe(viewLifecycleOwner) {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//            }
//        } else saveLogbook(getData)
//    }

    private fun saveLogbook(getData: ItemLogbook) {
        val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(tanggal.value)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        Log.d("cek data logbook - 5", "deskripsi = ${binding.editKet.text.toString()}, " +
                "jam = ${jam.value}:00, listDokumen = ${listUploadedFile} ${listUploadedFile.size
                }, tanggal = ${formattedDate}")

        var a = 0
        val listReadyUpFile = mutableListOf<PostDataLogbook>()

        listUploadedFile.map {
            Log.d("cek uploadfile - 6", "${a} : ${it.fileDokumen}")
            listReadyUpFile.add(
                PostDataLogbook(it.fileDokumen, getData.id.toString(), listFileUnupload[a].nama_dokumen))
            a++
        }

        viewmodel.liveDataPutDataLogbook(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            getData.id.toString(),
            PostLogbook(
                deskripsi = binding.editKet.text.toString(),
                idPendaftar = getIdPendaftar(requireContext())?.id.toString(),
                jam = "${jam.value}:00",
                link = "",
                listDokumen = listReadyUpFile,
                tanggal = formattedDate
            )
        ).observe(viewLifecycleOwner) {
            Log.d("cek data savelgbk - 7", "${it.data.deskripsi}")
            Toast.makeText(requireContext(), "Logbook berhasil diunggah", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
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

    private fun toolbar() {
        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnBatal.setOnClickListener {
            PendaftaranDialogFragment(true, "Konfirmasi Logbook", null, true, null)
                .show(requireActivity().supportFragmentManager, "TAG DETAIL ACC PROGRAM TIMMBKM")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
