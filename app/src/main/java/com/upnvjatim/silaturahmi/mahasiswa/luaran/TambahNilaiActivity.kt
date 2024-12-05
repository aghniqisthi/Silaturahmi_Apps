package com.upnvjatim.silaturahmi.mahasiswa.luaran

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.ActivityTambahNilaiBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class TambahNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahNilaiBinding
    private var file: File? = null
    private var fileName: String? = null
    val nilaiViewModel: NilaiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val getData = intent.getParcelableExtra("nilai") as ItemPendaftarProgram?

        if (getData != null) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(getData.tglPenilaian.toString())
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

            binding.editTgl.setText(formattedDate)
            binding.editKet.setText(getData.ketPenilaian)
            binding.editNilai.setText(getData.nilaiAngka)

            binding.uploadContainer.visibility = View.GONE
            binding.btnFilebukti.visibility = View.VISIBLE
            binding.btnFilebukti.text = getData.linkPenilaian
        } else {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                .format(Timestamp.now().toDate())
            binding.editTgl.setText(formattedDate)
            binding.uploadContainer.visibility = View.VISIBLE
            binding.btnFilebukti.visibility = View.GONE
        }

        binding.uploadContainer.setOnClickListener {
            openForResult()
        }

        binding.btnFilebukti.setOnClickListener {
            openForResult()
        }

        binding.btnSimpan.setOnClickListener {
            PendaftaranDialogFragment(false,"Konfirmasi Nilai Akhir",null, false,
                { simpanNilaiAkhir(getData != null) })
                .show(supportFragmentManager, "TAMBAH NILAI BACK")
        }

        binding.btnBatal.setOnClickListener {
            PendaftaranDialogFragment(true,null,null,false, null)
                .show(supportFragmentManager, "TAMBAH NILAI BACK")
        }

    }

    private fun simpanNilaiAkhir(isEdit: Boolean) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ket = binding.editKet.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val nilai = binding.editNilai.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val idPendaftar = getIdPendaftar(this)?.id.toString()
        val npm = getUser(this)?.user?.username.toString()

        if(file?.exists() == true && file != null) {
            val requestImageFile = file!!.asRequestBody("application/pdf".toMediaTypeOrNull())
            val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file", fileName, requestImageFile)

            Log.d("cek data", "date = ${
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())}" +
                    " ket = ${binding.editKet.text.toString()} ; nilai = ${binding.editNilai.text.toString()}" +
                    " idPendaftar = ${getIdPendaftar(this)?.id.toString()}")

            nilaiViewModel.putNilai(fileMultipart, idPendaftar, ket, date, nilai,this)
            nilaiViewModel.liveDataPutNilai.observe(this){

                Toast.makeText(this, "Unggah Nilai berhasil", Toast.LENGTH_SHORT).show()

            FirebaseFirestore.getInstance()
                .collection("notification")
                .whereEqualTo("username", npm)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Log.d("cek noti", "${it.size()}")
                        if(isEdit) createNewNotif(
                            npm, "Nilai Akhir",
                            "Anda berhasil mengubah data nilai akhir. Mohon bersabar menunggu hasil verifikasi",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this)

                        else createNewNotif(
                            npm, "Nilai Akhir",
                            "Anda berhasil mengunggah data nilai akhir. Mohon bersabar menunggu hasil verifikasi",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this)
                    } else {
                        Log.d("cek notif else", "${it.size()}")
                        if(isEdit) saveNotif(
                            npm,
                            "Nilai Akhir",
                            "Anda berhasil mengubah data nilai akhir. Mohon bersabar menunggu hasil verifikasi",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this
                        )
                        else saveNotif(
                            npm,
                            "Nilai Akhir",
                            "Anda berhasil mengunggah data nilai akhir. Mohon bersabar menunggu hasil verifikasi",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this
                        )
                    }
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
                }
        }
            }

        nilaiViewModel.isLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        nilaiViewModel.isError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
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
                val inputStream = contentResolver.openInputStream(uri)
                file = File(cacheDir, com.upnvjatim.silaturahmi.getFileName(uri, this))
                inputStream?.use { input ->
                    file?.outputStream().use { output ->
                        input.copyTo(output!!)
                    }
                }
                fileName = com.upnvjatim.silaturahmi.getFileName(uri, this)
                Log.d("cek filename", "$fileName ; ${file?.path}")

                updateUIWithFile(file, fileName)
            }
        }
    }

    private fun updateUIWithFile(file: File?, fileName: String?) {
        if (file != null) {
            Log.d("cek file upd ui", fileName.toString())
            binding.btnFilebukti.text = fileName
            binding.btnFilebukti.visibility = View.VISIBLE
            binding.uploadContainer.visibility = View.GONE
        } else {
            binding.uploadContainer.visibility = View.VISIBLE
            binding.btnFilebukti.visibility = View.GONE
        }
    }

}