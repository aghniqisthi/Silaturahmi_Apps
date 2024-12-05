package com.upnvjatim.silaturahmi.dosen.pembimbing

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.ActivityNavbarPembimbingBinding
import com.upnvjatim.silaturahmi.getUser

class NavbarPembimbingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavbarPembimbingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavbarPembimbingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) //, systemBars.bottom)
//            insets
//        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        if(getUser(this)?.user?.role?.id == "HA08"){
            navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.d("check destination navbar", "${destination.id} ${destination.label}")
                when (destination.id) {
                    R.id.homeDosenFragment, R.id.riwayatBimbinganFragment,
                    R.id.magangDospemFragment, R.id.profileFragment -> {
                        binding.bottomNavigationView.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.bottomNavigationView.visibility = View.GONE
                    }
                }
            }
            binding.bottomNavigationView.setupWithNavController(navController)
        } else {
            binding.bottomNavigationView.visibility = View.GONE
        }
    }
}