package com.upnvjatim.silaturahmi.mahasiswa.logbook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentTambahLogbookBinding
import com.upnvjatim.silaturahmi.getFileName
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.PostDataLogbook
import com.upnvjatim.silaturahmi.model.request.PostDocsLogbook
import com.upnvjatim.silaturahmi.model.request.PostLogbook
import com.upnvjatim.silaturahmi.viewmodel.LogbookViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.LogbookArrayDocsAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TambahLogbookFragment : Fragment() {
    private var _binding: FragmentTambahLogbookBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: LogbookViewModel by activityViewModels()

    private var tanggal: MutableLiveData<String?> = MutableLiveData()
    private var jam: MutableLiveData<String?> = MutableLiveData()
    private val listFile = mutableListOf<PostDocsLogbook>()
    private val listUploadedFile = mutableListOf<PostDataLogbook>()
    private lateinit var fileAdapter: LogbookArrayDocsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar()

        //  final data = LogbookRequest(
        //      deskripsi: _keterangan,
        //      idPendaftar: idPendaftar!,
        //      jam: DateFormat('HH:mm:ss').format(DateTime(2024, selectedTime.hour,)),
        //      link: '',
        //      listDokumen: [
        //         ListDokumen(
        //             fileDokumen: valueDocs.data,
        //             idLogbookMagang: null,
        //             namaDokumen: fileName
        //         )],
        //      tanggal: DateFormat('yyyy-MM-dd').format(selectedDate)
        //   );

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
                val formattedDate =
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
                Toast.makeText(requireContext(), formattedDate, Toast.LENGTH_SHORT).show()
                tanggal.value = formattedDate
            }
        }

        binding.btnJam.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTheme(R.style.TimePickerTheme).build()
            timePicker.show(requireActivity().supportFragmentManager, "Tag")
            timePicker.addOnPositiveButtonClickListener {
                val output = String.format("%02d:%02d", timePicker.hour, timePicker.minute)
                jam.value = output
                Toast.makeText(requireContext(), "Time is: $output", Toast.LENGTH_SHORT).show()
            }
        }
        binding.toolbar.title = "Tambah Logbook"

        val calendar = Calendar.getInstance()
        val formattedDate =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(calendar.time)
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

        tanggal.value = formattedDate
        jam.value = formattedTime

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        fileAdapter = LogbookArrayDocsAdapter(listFile)
        fileAdapter.submitList(listFile)

        binding.rvDocslogbook.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvDocslogbook.adapter = fileAdapter
        fileAdapter.setOnItemClickListener {
            Log.d("cek delete file", "${it.nama_dokumen} ; ${it.file.path}")
            if (listFile.contains(it)) listFile.remove(it)
            else Toast.makeText(requireContext(), "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            fileAdapter.notifyDataSetChanged()
        }

        binding.uploadContainer.setOnClickListener {
            openForResult()
        }

        binding.btnSimpan.setOnClickListener {
            PendaftaranDialogFragment(
                false, "Konfirmasi Logbook", null, true, { uploadFilesSequentially() }
            ).show(requireActivity().supportFragmentManager, "TAG DELETE LUARAN")
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
                    Log.d("cek filename", fileName)
                    listFile.add(
                        PostDocsLogbook(getIdPendaftar(requireContext())?.id.toString(), fileName, file!!)
                    )

                    fileAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun uploadFilesSequentially() {
        val totalFiles = listFile.size
        listUploadedFile.clear()

        // Function to handle the upload of a single file
        fun uploadNextFile() {
            if (listUploadedFile.size < totalFiles) {
                val fileItem = listFile[listUploadedFile.size]
                Log.d("cek listFileUnupload - 2", "${fileItem.nama_dokumen} ; ${fileItem.file.path}")
                Toast.makeText(requireContext(), "uploading file ${
                    fileItem.nama_dokumen}", Toast.LENGTH_SHORT).show()

                val requestImageFile = fileItem.file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file", fileItem.nama_dokumen, requestImageFile)
                val id = fileItem.idPendaftar.toRequestBody("text/plain".toMediaType())
                val namadocs = fileItem.nama_dokumen.toRequestBody("text/plain".toMediaType())

                // Call API to upload the file
                viewmodel.postDocsLogbook(fileMultipart, id, namadocs, requireContext())
                viewmodel.liveDataPostDocsLogbook.observe(viewLifecycleOwner) { data ->
                    if (data != null && listUploadedFile.none { it.fileDokumen == data.data }) {
                        listUploadedFile.add(PostDataLogbook(data.data, null, fileItem.nama_dokumen))
//                        currentIndex++
                        uploadNextFile()
                        Log.d("cek uploaded file - 4", "${listUploadedFile.size} -> ${data.data} ; ${
                            listUploadedFile} ; ${listFile.size}")
                    }
                }
            } else {
                saveLogbook()
            }
        }

        if (listFile.isNotEmpty()) uploadNextFile()
        else if(listUploadedFile.size == totalFiles) saveLogbook()
    }

    private fun saveLogbook() {
        val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(tanggal.value)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        Log.d("cek data logbook", "deskripsi = ${binding.editKet.text.toString()}, " +
                "jam = ${jam.value}:00, listDokumen = $listUploadedFile : ${
                    listUploadedFile.size}, tanggal = ${formattedDate}")

        var i = 0
        val listReadyUpFile = mutableListOf<PostDataLogbook>()

        listUploadedFile.map {
            Log.d("cek uploadfile", "$i : ${it.fileDokumen}")
            listReadyUpFile.add(
                PostDataLogbook(it.fileDokumen, it.idLogbookMagang, listFile[i].nama_dokumen))
            i++
        }

        viewmodel.liveDataPostDataLogbook(
            "bearer ${getUser(requireContext())?.token?.AccessToken}",
            PostLogbook(
                deskripsi = binding.editKet.text.toString(),
                idPendaftar = getIdPendaftar(requireContext())?.id.toString(),
                jam = "${jam.value}:00",
                link = "",
                listDokumen = listReadyUpFile,
                tanggal = formattedDate
            )
        ).observe(viewLifecycleOwner) {
            Log.d("cek data savelgbk", "${it.data.deskripsi}")
            Toast.makeText(requireContext(), "Logbook berhasil diunggah", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

//    private fun requirePermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                requireActivity(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), Constant.PERMISSION_REQUEST_FILE
//            )
//        } else {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), Constant.PERMISSION_REQUEST_FILE
//            )
//        }
//    }

}
