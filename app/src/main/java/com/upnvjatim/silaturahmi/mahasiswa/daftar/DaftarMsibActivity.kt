package com.upnvjatim.silaturahmi.mahasiswa.daftar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.createNewNotif
import com.upnvjatim.silaturahmi.databinding.ActivityDaftarMsibBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.mahasiswa.dialogpopup.PendaftaranDialogFragment
import com.upnvjatim.silaturahmi.model.request.DataNotif
import com.upnvjatim.silaturahmi.model.request.ListMatkul
import com.upnvjatim.silaturahmi.model.request.PostPaketKonversi
import com.upnvjatim.silaturahmi.saveNotif
import com.upnvjatim.silaturahmi.viewmodel.DaftarMsibViewModel
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DaftarMsibActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarMsibBinding
    val msibViewModel: MsibViewModel by viewModels()
    val viewmodel: DaftarMsibViewModel by viewModels()

    var descriptionData: Array<String> = arrayOf(
        "Bukti\nPenerimaan", "Program\nMagang/Stupen",
        "Paket\nKonversi", "Konfirmasi"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarMsibBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.isTitleCentered = false
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            PendaftaranDialogFragment(true, null, null, false, null)
                .show(supportFragmentManager, "CustomDialog")
        }

        val program = intent.getStringExtra("program")

        val stateProgressBar = binding.statePendaftaran
        stateProgressBar.setStateDescriptionData(descriptionData)
//        stateProgressBar.setStateDescriptionTypeface(
//            ResourcesCompat.getFont(this, R.font.poppins_semibold).toString());
//        stateProgressBar.setStateNumberTypeface("font/poppins_semibold")
        stateProgressBar.stateDescriptionSize = 12F

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up StateProgressBar with fragments navigation
        stateProgressBar.setOnStateItemClickListener { _, _, stateNumber, isCurrent ->
            if (isCurrent) {
                navigateToState(navController, stateNumber)
            }
        }

        if (program == "msib") {
            binding.toolbar.title = "Pendaftaran Program MSIB"

            msibViewModel.jenisMsib.value = "msib"

            binding.btnNext.setOnClickListener {
                val currentStateNumber = stateProgressBar.currentStateNumber
                when (currentStateNumber) {
                    1 -> {
                        if (msibViewModel.file.value != null) {
                            stateProgressBar.setCurrentStateNumber(2)
                            navigateToState(navController, 2)
                        } else {
                            if (getIdPendaftar(this)?.status != 0) {
                                stateProgressBar.setCurrentStateNumber(2)
                                navigateToState(navController, 2)
                            } else Toast.makeText(
                                this, "Bukti pendaftaran belum diunggah!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    2 -> {
                        if (msibViewModel.programData.value != null) {
                            stateProgressBar.setCurrentStateNumber(3)
                            navigateToState(navController, 3)
                        } else {
                            Toast.makeText(this, "Data program kosong!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    3 -> {
                        if (msibViewModel.paketData.value != null) {
                            stateProgressBar.setCurrentStateNumber(4)
                            navigateToState(navController, 4)
                            binding.btnNext.text = "Ajukan"
                        } else {
                            Toast.makeText(this, "Paket konversi kosong!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    4 -> {
                        stateProgressBar.setAllStatesCompleted(true)
                        if (getIdPendaftar(this)?.status == 0
                            || getIdPendaftar(this)?.isFinish == true
                            || getIdPendaftar(this) == null
                            || getIdPendaftar(this)?.status == null
                        ) PendaftaranDialogFragment(false, null, null, false, { savePendaftaranMsib() })
                            .show(supportFragmentManager, "CustomDialog")
                        else PendaftaranDialogFragment(false, null, null, false, { saveFilePendaftaran() })
                            .show(supportFragmentManager, "CustomDialog")
                    }
                }
            }

        } else {
            binding.toolbar.title = "Pendaftaran Program Mandiri"

            msibViewModel.jenisMsib.value = "mandiri"

            binding.btnNext.setOnClickListener {
                val currentStateNumber = stateProgressBar.currentStateNumber
                when (currentStateNumber) {
                    1 -> {
                        if (msibViewModel.file.value != null) {
                            if (getIdPendaftar(this)?.status == 0
                                || getIdPendaftar(this) == null
                                || getIdPendaftar(this)?.status == null
                            ) {
                                binding.btnNext.text = "Ajukan"
                            } else {
                                binding.btnNext.text = "Lanjutkan"
                            }
                            stateProgressBar.setCurrentStateNumber(2)
                            navigateToState(navController, 2)
                        } else {
                            if (getIdPendaftar(this)?.status != 0) {
                                stateProgressBar.setCurrentStateNumber(2)
                                navigateToState(navController, 2)
                            } else Toast.makeText(
                                this, "Bukti pendaftaran belum diunggah!", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    2 -> {
                        if (msibViewModel.programData.value != null) {
                            if (getIdPendaftar(this)?.status == 0
                                || getIdPendaftar(this)?.isFinish == true
                                || getIdPendaftar(this) == null
                                || getIdPendaftar(this)?.status == null
                            ) {
                                PendaftaranDialogFragment(
                                    false,
                                    null,
                                    null, false,
                                    { saveProgramMandiri() })
                                    .show(supportFragmentManager, "CustomDialog")
                            } else {
                                binding.btnNext.text = "Lanjutkan"
                                stateProgressBar.setCurrentStateNumber(3)
                                navigateToState(navController, 3)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Data program belum disetujui!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    3 -> {
                        if (msibViewModel.paketData.value != null) {
                            stateProgressBar.setCurrentStateNumber(4)
                            navigateToState(navController, 4)
                            binding.btnNext.text = "Ajukan"
                        } else {
                            Toast.makeText(this, "Paket konversi kosong!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    4 -> {
                        stateProgressBar.setAllStatesCompleted(true)
                        if (
                            getIdPendaftar(this)?.status == 4
                            && getIdPendaftar(this)?.idSiamikMahasiswa != null
                            && getIdPendaftar(this)?.idSiamikMahasiswa != ""
                            && getIdPendaftar(this)?.id != null
                            && getIdPendaftar(this)?.id != ""
                        ) {
                            PendaftaranDialogFragment(false, null, null, false, {
                                saveFilePendaftaran()
                                savePaketMandiri()
                            })
                                .show(supportFragmentManager, "CustomDialog")
                        } else PendaftaranDialogFragment(false, null, null, false, {
                            saveProgramMandiri()
                            savePaketMandiri()
                        }).show(supportFragmentManager, "CustomDialog")
                    }

                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.pilihProgramFragment, R.id.pilihPaketKonversiFragment,
                R.id.buatPaketKonversiFragment, R.id.tambahMatkulFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.statePendaftaran.visibility = View.GONE
                    binding.linearlayout.visibility = View.GONE
                }

                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.statePendaftaran.visibility = View.VISIBLE
                    binding.linearlayout.visibility = View.VISIBLE
                }
            }
        }

        binding.btnBatal.setOnClickListener {
            val currentStateNumber = stateProgressBar.currentStateNumber
            Log.d("cek currentstate", currentStateNumber.toString())
            when (currentStateNumber) {
                1 -> {
                    PendaftaranDialogFragment(true, null, null, false,null)
                        .show(supportFragmentManager, "CustomDialog")
                }

                2 -> {
                    stateProgressBar.setCurrentStateNumber(1)
                    navigateToState(navController, 1)
                    binding.btnNext.text = "Lanjutkan"
                }

                3 -> {
                    stateProgressBar.setCurrentStateNumber(2)
                    navigateToState(navController, 2)
                }

                4 -> {
                    stateProgressBar.setAllStatesCompleted(false)
                    stateProgressBar.setCurrentStateNumber(3)
                    navigateToState(navController, 3)
                    binding.btnNext.text = "Lanjutkan"
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            PendaftaranDialogFragment(true, null, null, false, null)
                .show(supportFragmentManager, "CustomDialog")
        }
    }

    private fun saveFilePendaftaran() {
        Toast.makeText(this, "simpan file", Toast.LENGTH_SHORT).show()

        val file = msibViewModel.file.value
        if (file?.exists() == true) {
            val id = getIdPendaftar(this)?.id.toString()
            val idPegawai = getIdPendaftar(this)?.idPegawai.toString()
            val status = getIdPendaftar(this)?.status.toString()
            val idSiamik = getUser(this)?.user?.idSiamikMahasiswa.toString()
            val idMitraTerlibatProgram = getIdPendaftar(this)?.idMitraTerlibatProgram.toString()

            Log.d("cek data daftar", "${getUser(this)?.user?.idSiamikMahasiswa
                } ; ${msibViewModel.programData.value?.idMitra}  ; ${msibViewModel.fileName.value
                } ; ${msibViewModel.jenisMsib.value} ; file = ${msibViewModel.file.value?.path
                }, id = ${getIdPendaftar(this)?.id
                }, idSiamikMahasiswa = ${getUser(this)?.user?.idSiamikMahasiswa.toString()
                }, idPegawai = ${getIdPendaftar(this)?.idPegawai
                }, status = ${getIdPendaftar(this)?.status
                }, idMitraTerlibatProgram = ${getIdPendaftar(this)?.idMitraTerlibatProgram}")

            viewmodel.putProgram(
                files = msibViewModel.file.value!!, id = id, idSiamikMahasiswa = idSiamik, idPegawai = idPegawai,
                status = status, idMitraTerlibatProgram = idMitraTerlibatProgram, isFinish = false, context = this
            )
            viewmodel.putFileProgram.observe(this) { vl ->
                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getUser(this)?.user?.username)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran Mandiri",
                                "Anda berhasil mengunggah dokumen bukti pendaftaran.",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran Mandiri",
                                "Anda berhasil mengunggah dokumen bukti pendaftaran.",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif mandiri", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
                Log.d("cek data vl", "statusmbkm ${vl.data?.statusMbkm} ; id ${vl.data?.id
                    } ; link ${vl.data?.link} ; ${vl.data?.createdAt} ; ${vl.data?.updatedAt}")

                Toast.makeText(this, "Pendaftaran program ${msibViewModel.jenisMsib.value} berhasil",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Log.e("File Error", "File does not exist")
        }
    }

    private fun saveProgramMandiri() {
        Toast.makeText(this, "simpan pendaftaran mandiri", Toast.LENGTH_SHORT).show()

        val file = msibViewModel.file.value
        if (file?.exists() == true) {
            val requestImageFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "pdf", msibViewModel.fileName.value, requestImageFile
            )
            val idSiamik = getUser(this)?.user?.idSiamikMahasiswa.toString()
                .toRequestBody("text/plain".toMediaType())
            val idMitra = msibViewModel.programData.value?.idMitra.toString()
                .toRequestBody("text/plain".toMediaType())
            val statusMbkm =
                if (msibViewModel.jenisMsib.value == "mandiri") "MANDIRI".toRequestBody("text/plain".toMediaType())
                else null

            Log.d(
                "cek data daftar", "${
                    getUser(this)?.user?.idSiamikMahasiswa
                } ; ${msibViewModel.programData.value?.idMitra} ; ${msibViewModel.jenisMsib.value}"
            )

            viewmodel.postProgram(fileMultipart, idSiamik, idMitra, statusMbkm, this)
            viewmodel.addProgram.observe(this) { vl ->
//            viewmodel.postPaketBulk(
//                "bearer ${getUser(this)?.token?.AccessToken}",
//                PostPaketKonversi(
//                    deskripsi = msibViewModel.paketData.value?.deskripsi.toString(),
//                    idPaketKonversi = msibViewModel.paketData.value?.id.toString(),
//                    idPendaftarMbkm = vl.data.id,
//                    idProgram = msibViewModel.programData.value?.idProgram.toString(),
//                    idProgramProdi = msibViewModel.paketData.value?.idProgramProdi.toString(),
//                    idRole = getUser(this)?.user?.role?.id.toString(),
//                    listMatkul = msibViewModel.paketData.value?.detail!!.map {
//                        ListMatkul(
//                            id = it?.id.toString(),
//                            idMatkul = it?.idMatkul.toString(),
//                            idPendaftarMbkm = vl.data.id,
//                            isMahasiswa = true,
//                            nilaiAngka = null,
//                            nilaiHuruf = null,
//                            sks = it?.sks.toString()
//                        )
//                    },
//                    namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi.toString()
//                ), this
//            )
//            viewmodel.addPaketBulk.observe(this) {
                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getUser(this)?.user?.username)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            Log.d("cek noti", "${it.size()}")
                            createNewNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran Mandiri",
                                "Anda berhasil mendaftar program Mandiri. " +
                                        "Mohon bersabar menunggu hasil verifikasi sebelum " +
                                        "melanjutkan ke tahap perancangan paket konversi",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        } else {
                            Log.d("cek notif else", "${it.size()}")
                            saveNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran Mandiri",
                                "Anda berhasil mendaftar program Mandiri. " +
                                        "Mohon bersabar menunggu hasil verifikasi " +
                                        "sebelum melanjutkan ke tahap perancangan paket konversi",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif mandiri", "${it.message} ${it.cause} ${it.stackTrace}")
                    }

                Toast.makeText(
                    this,
                    "Pendaftaran program jeniss ${msibViewModel.jenisMsib.value} berhasil",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
//        }
//        viewmodel.isLoading.observe(this) {
//            if (it) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.INVISIBLE
//        }
//        viewmodel.isError.observe(this) {
//            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
//        }
        } else {
            Log.e("File Error", "File does not exist")
        }
    }

    var data: MutableList<DataNotif> = mutableListOf()

    private fun savePaketMandiri() {
        Toast.makeText(this, "simpan pendaftaran mandiri", Toast.LENGTH_SHORT).show()

        /// TODO: [TESTING] ajukan penadftaran mandiri
        viewmodel.postPaketBulk(
            "bearer ${getUser(this)?.token?.AccessToken}",
            PostPaketKonversi(
                deskripsi = msibViewModel.paketData.value?.deskripsi.toString(),
                idPaketKonversi = msibViewModel.paketData.value?.id,
                idPendaftarMbkm = getIdPendaftar(this)?.id.toString(),
                idProgram = msibViewModel.programData.value?.idProgram.toString(),
                idProgramProdi = msibViewModel.paketData.value?.idProgramProdi.toString(),
                idRole = getUser(this)?.user?.role?.id.toString(),
                listMatkul = msibViewModel.paketData.value?.detail
                    ?.filter { it?.idMatkul != null && it.idMatkul != "null" && it.idMatkul != "" }
                !!.map {
                        ListMatkul(
                            id = it?.id,
                            idMatkul = it?.idMatkul.toString(),
                            idPendaftarMbkm = getIdPendaftar(this)?.id.toString(),
                            isMahasiswa = true,
                            nilaiAngka = null,
                            nilaiHuruf = null,
                            sks = it?.sks.toString()
                        )
                    },
                namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi.toString()
            ), this
        )
        viewmodel.addPaketBulk.observe(this) {

            FirebaseFirestore.getInstance()
                .collection("notification")
                .whereEqualTo("username", getUser(this)?.user?.username)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        createNewNotif(
                            getUser(this)?.user?.username.toString(),
                            "Pendaftaran Mandiri",
                            "Paket konversi berhasil dibuat. " +
                                    "Mohon bersabar menunggu hasil plotting dosen pembimbing",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this
                        )
                    } else {
                        saveNotif(
                            getUser(this)?.user?.username.toString(),
                            "Pendaftaran Mandiri",
                            "Paket konversi berhasil dibuat. " +
                                    "Mohon bersabar menunggu hasil plotting dosen pembimbing",
                            getIdPendaftar(this)?.status.toString(),
                            getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                            this
                        )
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("error notif", "${e.message} ${e.cause} ${e.stackTrace}")
                }

        }

        viewmodel.isLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }


    }

    private fun savePendaftaranMsib() {
        Toast.makeText(this, "simpan pendaftaran msib", Toast.LENGTH_SHORT).show()

        /// TODO: [TESTING] ajukan penadftaran msib
        val requestImageFile = msibViewModel.file.value?.asRequestBody("pdf".toMediaTypeOrNull())
        val fileMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "pdf", msibViewModel.fileName.value, requestImageFile!!
        )
        val idSiamik = getUser(this)?.user?.idSiamikMahasiswa.toString()
            .toRequestBody("text/plain".toMediaType())
        val idMitra =
            msibViewModel.programData.value?.id.toString().toRequestBody("text/plain".toMediaType())

        viewmodel.postProgram(fileMultipart, idSiamik, idMitra, null, this)
        viewmodel.addProgram.observe(this) { vl ->
            viewmodel.postPaketBulk(
                "bearer ${getUser(this)?.token?.AccessToken}",
                PostPaketKonversi(
                    deskripsi = msibViewModel.paketData.value?.deskripsi.toString(),
                    idPaketKonversi = msibViewModel.paketData.value?.id.toString(),
                    idPendaftarMbkm = vl.data.id,
                    idProgram = msibViewModel.programData.value?.idProgram.toString(),
                    idProgramProdi = msibViewModel.paketData.value?.idProgramProdi.toString(),
                    idRole = getUser(this)?.user?.role?.id.toString(),
                    listMatkul = msibViewModel.paketData.value?.detail!!.map {
                        ListMatkul(
                            id = it?.id.toString(),
                            idMatkul = it?.idMatkul.toString(),
                            idPendaftarMbkm = vl.data.id,
                            isMahasiswa = true,
                            nilaiAngka = null,
                            nilaiHuruf = null,
                            sks = it?.sks.toString()
                        )
                    },
                    namaPaketKonversi = msibViewModel.paketData.value?.namaPaketKonversi.toString()
                ), this
            )
            viewmodel.addPaketBulk.observe(this) {

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .whereEqualTo("username", getUser(this)?.user?.username)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            createNewNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran Mandiri",
                                "Anda berhasil mendaftar program Mandiri. " +
                                        "Mohon bersabar menunggu hasil verifikasi " +
                                        "sebelum melanjutkan ke tahap perancangan paket konversi",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        } else {
                            saveNotif(
                                getUser(this)?.user?.username.toString(),
                                "Pendaftaran MSIB",
                                "Anda berhasil mendaftar program MSIB. " +
                                        "Mohon bersabar menunggu hasil plotting Pembimbing MBKM",
                                getIdPendaftar(this)?.status.toString(),
                                getIdPendaftar(this)?.isFinish.toString().toBoolean(),
                                this
                            )
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif", "${e.message} ${e.cause} ${e.stackTrace}")
                    }

                Toast.makeText(
                    this,
                    "Pendaftaran program ${msibViewModel.jenisMsib.value} berhasil",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        viewmodel.isLoading.observe(this) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }


    }

    private fun navigateToState(navController: NavController, stateNumber: Int) {
        when (stateNumber) {
            1 -> {
                navController.navigate(R.id.stOneBuktiDaftarFragment)
            }

            2 -> {
                navController.navigate(R.id.stTwoProgramFragment)
            }

            3 -> {
                navController.navigate(R.id.stThreePaketFragment)
            }

            4 -> {
                navController.navigate(R.id.stFourKonfirmasiFragment)
            }
        }
    }


}
