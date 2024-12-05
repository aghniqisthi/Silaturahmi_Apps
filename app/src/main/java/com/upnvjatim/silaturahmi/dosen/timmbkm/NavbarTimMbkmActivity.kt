package com.upnvjatim.silaturahmi.dosen.timmbkm

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.ActivityNavbarTimMbkmBinding
import com.upnvjatim.silaturahmi.getUser

class NavbarTimMbkmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavbarTimMbkmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavbarTimMbkmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setBackgroundColor(Color.WHITE)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) //, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        Log.d("cek id", getUser(this)?.user?.role?.id.toString())
        if(getUser(this)?.user?.role?.id == "HA05"){
            navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.d("check destination navbar", "${destination.id} ${destination.label}")
                when (destination.id) {
                    R.id.homeDosenFragment, R.id.mbkmFragment, R.id.profileFragment -> {
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