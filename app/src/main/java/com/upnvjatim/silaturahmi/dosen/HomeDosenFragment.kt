package com.upnvjatim.silaturahmi.dosen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.upnvjatim.silaturahmi.DATAPASS
import com.upnvjatim.silaturahmi.DATAUSER
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.SplashActivity
import com.upnvjatim.silaturahmi.databinding.FragmentHomeDosenBinding
import com.upnvjatim.silaturahmi.getListRole
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.DataValidasiLogin
import com.upnvjatim.silaturahmi.saveUser
import com.upnvjatim.silaturahmi.viewmodel.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjatim.silaturahmi.DETAILUSER
import com.upnvjatim.silaturahmi.IDPENDAFTARPROGRAM
import com.upnvjatim.silaturahmi.LISTROLE
import com.upnvjatim.silaturahmi.LoginActivity

class HomeDosenFragment : Fragment() {
    private var _binding: FragmentHomeDosenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDosenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtFullname.text = getUser(requireContext())?.user?.name ?: getUser(requireContext())?.user?.username
        binding.txtNpm.text = getUser(requireContext())?.user?.role?.name ?: "DOSEN"

        val viewmodel = ViewModelProvider(this)[LoginViewModel::class.java]

        val roleNow = getUser(requireContext())?.user?.role?.name
//        var a = listOf<DataValidasiLogin>()

//        for(i in 0 until getListRole(requireContext())?.data!!.size){
//            if(getListRole(requireContext())?.data!![i]!!.name != roleNow)
//                a =  a + getListRole(requireContext())!!.data!![i]!!
//        }

        val a = getListRole(requireContext())?.data?.mapNotNull {
            if(it?.name != roleNow) it else null
        }

        val adapter = a?.let { RoleAdapter(it) }
        val sharedPref = requireContext().getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
        binding.rvAkses.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAkses.adapter = adapter

        adapter?.setOnItemClickListener{
            viewmodel.getLoginResponse(
                getUser(requireContext())?.user?.username.toString(),
                sharedPref.getString(DATAPASS, "").toString(),
                it.idRole ?: "",
                requireContext()
            )

            viewmodel.isLoading.observe(viewLifecycleOwner) {
                Log.d("state isLoading", it.toString())
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.GONE
            }

            viewmodel.isError.observe(viewLifecycleOwner) {
                MaterialAlertDialogBuilder(requireContext())
                    .setBackground(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.modal_login_badreq
                        )
                    )
                    .setTitle(it)
                    .setPositiveButton("Ok", null)
                    .show()
            }

            viewmodel.login.observe(viewLifecycleOwner) {
                Log.d("cek role", it.data?.user?.role?.name.toString())
                saveUser(
                    it.data,
                    getListRole(requireContext()),
                    sharedPref.getString(DATAPASS, "").toString(),
                    requireContext()
                )

                val intent = Intent(requireContext(), SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        if (isAdded && context != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUser(requireContext())?.user?.username.toString())
                .get()
                .addOnSuccessListener {
                    if (it.exists() && it.getString("avatar") != null && it.getString("avatar") != "") {
                        val avatar = it.getString("avatar").toString()
                        Glide.with(this).load(avatar).into(binding.imgAvatar)
                    } else {
                        Log.d("cek data users not found", "${it.data?.size}")
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),
                        "Error getAvatar : ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("error getavatar", "${it.message} ${it.cause} ${it.stackTrace}")
                }
        }

        val userId = getUser(requireContext())?.user?.role?.id
        if(userId == "HA03" || userId == "HA05" || userId == "HA07" || userId == "HA08"){
            binding.btnKeluar.visibility = View.GONE
        } else {
            binding.btnKeluar.visibility = View.VISIBLE
            binding.btnKeluar.setOnClickListener {
                val sharedPrefUser = requireActivity().getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
                sharedPrefUser.edit().clear().apply()

                val sharedPrefPendaftar = requireActivity().getSharedPreferences(IDPENDAFTARPROGRAM, Context.MODE_PRIVATE)
                sharedPrefPendaftar.edit().clear().apply()

                val sharedPrefDetailUser = requireActivity().getSharedPreferences(DETAILUSER, Context.MODE_PRIVATE)
                sharedPrefDetailUser.edit().clear().apply()

                val sharedPrefPass = requireActivity().getSharedPreferences(DATAPASS, Context.MODE_PRIVATE)
                sharedPrefPass.edit().clear().apply()

                val sharedPrefRole = requireActivity().getSharedPreferences(LISTROLE, Context.MODE_PRIVATE)
                sharedPrefRole.edit().clear().apply()

                FirebaseAuth.getInstance().currentUser?.delete()

                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
    }
}

