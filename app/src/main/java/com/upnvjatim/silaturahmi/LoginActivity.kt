package com.upnvjatim.silaturahmi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.upnvjatim.silaturahmi.databinding.ActivityLoginBinding
import com.upnvjatim.silaturahmi.dosen.kaprodi.NavbarKaprodiActivity
import com.upnvjatim.silaturahmi.dosen.pembimbing.NavbarPembimbingActivity
import com.upnvjatim.silaturahmi.dosen.pic.NavbarPicActivity
import com.upnvjatim.silaturahmi.dosen.timmbkm.NavbarTimMbkmActivity
import com.upnvjatim.silaturahmi.mahasiswa.homenavbar.NavBarMhsActivity
import com.upnvjatim.silaturahmi.model.response.ResponseValidasiLogin
import com.upnvjatim.silaturahmi.viewmodel.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.upnvjatim.silaturahmi.model.response.DataLogin
import com.upnvjatim.silaturahmi.model.response.ProdiLogin
import com.upnvjatim.silaturahmi.model.response.RoleLogin
import com.upnvjatim.silaturahmi.model.response.Token
import com.upnvjatim.silaturahmi.model.response.UserLogin

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        sharedPref = this.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()
        val viewmodel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewmodel.isLoading.observe(this) {
            Log.d("state isLoading", it.toString())
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.text = ""
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.text = "Masuk"
            }
        }

        viewmodel.isError.observe(this) {
            MaterialAlertDialogBuilder(this)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.modal_login_badreq))
                .setTitle(it)
                .setPositiveButton("Ok", null)
                .show()
        }

        viewmodel.validasiLogin.observe(this) {
//                    val pass = binding.editPassword.text?.toString()?.length

            if(it.data?.firstOrNull()?.name != "MAHASISWA") {
                mAuth
                    .signInAnonymously()
//                        .signInWithEmailAndPassword(
//                        "${binding.editNpm.text.toString()}@student.upnjatim.ac.id",
//                        binding.editPassword.text.toString()
//                    )
                    .addOnSuccessListener {
//                        saveData(viewmodel, getListRole(this)!!, binding.editPassword.text.toString())
//                    }.addOnFailureListener {
//                        binding.progressBar.visibility = View.INVISIBLE
//
//                        if (it.message!!.contains(
//                                "The supplied auth credential is incorrect, malformed or has expired."
//                        )) {
//
//                            mAuth.createUserWithEmailAndPassword(
//                                "${binding.editNpm.text.toString()}@student.upnjatim.ac.id",
//                                binding.editPassword.text.toString()
//                            ).addOnSuccessListener {
//
//                                mAuth.signInWithEmailAndPassword(
//                                    "${binding.editNpm.text.toString()}@student.upnjatim.ac.id",
//                                    binding.editPassword.text.toString()
//                                ).addOnSuccessListener {
                        saveData(
                            viewmodel,
                            getListRole(this),
                            binding.editPassword.text.toString(),
                            binding.editNpm.text.toString()
                        )
//                                }.addOnFailureListener {
//                                    binding.progressBar.visibility = View.INVISIBLE
//                                    Log.e("error auth", "${it.message} ; ${it.stackTrace}")
//                                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                                }
                    }
                    .addOnFailureListener {
//                                binding.progressBar.visibility = View.INVISIBLE
                        Log.e("error auth", "${it.message} ; ${it.stackTrace}")
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                            }
//                        } else {
//                            Log.e("error auth", "${it.message} ; ${it.stackTrace}")
//                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
            }
        }

        viewmodel.login.observe(this) {
            mAuth.signInAnonymously()
                .addOnSuccessListener {
                    saveData(
                        viewmodel,
                        getListRole(this),
                        binding.editPassword.text.toString(),
                        binding.editNpm.text.toString()
                    )
                }.addOnFailureListener {
//                                binding.progressBar.visibility = View.INVISIBLE
                    Log.e("error auth", "${it.message} ; ${it.stackTrace}")
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.editNpm.text.toString() != "" && binding.editPassword.text.toString() != "") {
                viewmodel.getValidasiLoginResponse(
                    binding.editNpm.text.toString(),
                    binding.editPassword.text.toString(),
                    this
                )
            }
//            } else Toast.makeText(this, "Isi data dengan benar", Toast.LENGTH_SHORT).show()
        }

//        val dbUser = sharedPref.getString("username", "")
//            val dbUsername = sharedPref.getString("username", "You!")
//            val bundle = Bundle()
//            bundle.putString("username", dbUsername)

    }

    private fun saveData(viewmodel: LoginViewModel, listRole: ResponseValidasiLogin?, pass: String, username: String){
        val dataLogin = DataLogin(
            token = Token(null),
            UserLogin(
                email = null, firebaseToken = null, id = null, idJenisMbkm = null, idMhsLuarUpn = null,
                idMitra = null, idPegawai = null, idProgram = null, idProgramProdi = null,
                idSiamikMahasiswa = null, namaProdi = null, name = null, nik = null, nip = null,
                prodi = ProdiLogin(
                    id = null, kdFakjur = null, kdProdi = null, maxKrs = null, maxSks = null, namaProdi = null
                ), role = RoleLogin(id = null, isPegawai = null, name = null, urut = null),
                username = username
            )
        )
        saveUser(viewmodel.login.value?.data ?: dataLogin, listRole, pass, this)

//        val addUser = sharedPref.edit()
//        addUser.putStringSet(DATANAMA, viewmodel.login.value!!.data!!.user!!.name)
//        addUser.putString(DATANPM, binding.editNpm.text.toString())
//        addUser.apply(

        Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()

        val intent = when(viewmodel.login.value?.data?.user?.role?.id){
            "HA02" -> Intent(this, NavBarMhsActivity::class.java)
            "HA03" -> Intent(this, NavbarPicActivity::class.java)
            "HA05" -> Intent(this, NavbarTimMbkmActivity::class.java)
            "HA07" -> Intent(this, NavbarKaprodiActivity::class.java)
            "HA08" -> Intent(this, NavbarPembimbingActivity::class.java)
            else -> Intent(this, NavbarKaprodiActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


}

