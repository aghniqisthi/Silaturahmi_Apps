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
import com.upnvjatim.silaturahmi.databinding.ActivityTambahLuaranBinding
import com.upnvjatim.silaturahmi.getFileName
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.response.DataLuaran
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.LuaranViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class TambahLuaranActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahLuaranBinding
    private var file: File? = null
    private var fileName: String? = null
    val luaranViewModel: LuaranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahLuaranBinding.inflate(layoutInflater)
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

        val getData = intent.getParcelableExtra("luaran") as DataLuaran?

        if(getData != null){
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(getData.tanggal.toString())
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)

            binding.editTgl.setText(formattedDate)
            binding.editKet.setText(getData.keterangan)
            binding.btnFilebukti.text = getData.link

            binding.btnFilebukti.visibility = View.VISIBLE
            binding.uploadContainer.visibility = View.GONE
        } else {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Timestamp.now().toDate())
            binding.editTgl.setText(formattedDate)
        }
        binding.editJenis.setText("Laporan")
        binding.btnFilebukti.setOnClickListener {
            openForResult()
        }

        binding.uploadContainer.setOnClickListener {
            openForResult()
        }

        binding.btnSimpan.setOnClickListener {
            PendaftaranDialogFragment(false, "Konfirmasi Luaran", null, false,
                { if(getData != null) editLuaran(getData) else saveLuaran() })
                .show(supportFragmentManager, "TAG KONFIRMASI LUARAN")
        }

        binding.btnBatal.setOnClickListener {
            PendaftaranDialogFragment(true, null, null, false, null)
                .show(supportFragmentManager, "TAMBAH LUARAN BACK")
        }
    }

    private fun editLuaran(getData: DataLuaran) {
        val npm = getUser(this)?.user?.username.toString()
        val id = getData.id.toString().toRequestBody("text/plain".toMediaType())
        val idJenisLuaran = "a4da1925-cd38-42a4-b30c-3cb12e2de94a".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val idPendaftar = getIdPendaftar(this)?.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ket = binding.editKet.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var fileMultipart: MultipartBody.Part? = null

        if(file?.exists() == true && file != null) {
            val requestImageFile = file!!.asRequestBody("application/pdf".toMediaTypeOrNull())
            fileMultipart = MultipartBody.Part.createFormData("file", fileName, requestImageFile)

            Log.d("cek data", "npm = ${getUser(this)?.user?.username.toString()}" +
                    " idPendaftar = ${getIdPendaftar(this)?.id.toString()} ;" +
                    " date = ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())} ;" +
                    " ket = ${binding.editKet.text.toString()} ;" +
                    " file = ${file?.path}")
        }

        luaranViewModel.putLuaran(fileMultipart, id, idJenisLuaran, idPendaftar, date, ket,this)
        luaranViewModel.liveDataPostLuaran.observe(this){
            Toast.makeText(this, "Edit Luaran berhasil", Toast.LENGTH_SHORT).show()

            FirebaseFirestore.getInstance()
                .collection("notification")
                .whereEqualTo("username", npm)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        Log.d("cek noti", "${it.size()}")
                        createNewNotif(npm,
                            "Luaran Kegiatan",
                            "Anda berhasil mengubah data luaran program. Mohon bersabar menunggu hasil verifikasi"
                            , getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this)
                    } else {
                        Log.d("cek notif else", "${it.size()}")
                        saveNotif(
                            npm,
                            "Luaran Kegiatan",
                            "Anda berhasil mengubah data luaran program. Mohon bersabar menunggu hasil verifikasi"
                            , getIdPendaftar(this)?.status.toString(),
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
        luaranViewModel.isLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        luaranViewModel.isError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveLuaran() {
        val npm = getUser(this)?.user?.username.toString()
        val idJenisLuaran = "a4da1925-cd38-42a4-b30c-3cb12e2de94a".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val idPendaftar = getIdPendaftar(this)?.id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ket = binding.editKet.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if(file?.exists() == true && file != null) {
            val requestImageFile = file!!.asRequestBody("application/pdf".toMediaTypeOrNull())
            val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file", fileName, requestImageFile
            )

            Log.d("cek data", "npm = ${getUser(this)?.user?.username.toString()}" +
                    " idPendaftar = ${getIdPendaftar(this)?.id.toString()} ;" +
                    " date = ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Timestamp.now().toDate())} ;" +
                    " ket = ${binding.editKet.text.toString()} ;" +
                    " file = ${file?.path}")

            luaranViewModel.postLuaran(fileMultipart, idJenisLuaran, idPendaftar, date, ket, this)
            luaranViewModel.liveDataPostLuaran.observe(this) {
                Toast.makeText(this, "Unggah Luaran berhasil", Toast.LENGTH_SHORT).show()

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", npm)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(npm, "Luaran Kegiatan",
                                "Anda berhasil mengunggah data luaran program. Mohon bersabar menunggu hasil verifikasi",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this)
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(npm, "Luaran Kegiatan",
                                "Anda berhasil mengunggah data luaran program. Mohon bersabar menunggu hasil verifikasi",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this)
                        }
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
            }
            luaranViewModel.isLoading.observe(this) {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
            luaranViewModel.isError.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        } else Toast.makeText(this, "File tidak dapat ditemukan", Toast.LENGTH_SHORT).show()
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
                file = File(cacheDir, getFileName(uri, this))
                inputStream?.use { input ->
                    file?.outputStream().use { output ->
                        input.copyTo(output!!)
                    }
                }
                fileName = getFileName(uri, this)
                Log.d("cek filename", "$fileName ; ${file?.path}")

                updateUIWithFile(file, fileName)
            }
        }
    }

    private fun updateUIWithFile(file: File?, fileName: String?) {
        if (file != null) {
            binding.btnFilebukti.text = fileName
            binding.btnFilebukti.visibility = View.VISIBLE
            binding.uploadContainer.visibility = View.GONE
        } else {
            binding.uploadContainer.visibility = View.VISIBLE
            binding.btnFilebukti.visibility = View.GONE
        }
    }
}