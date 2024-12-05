package com.upnvjatim.silaturahmi.mahasiswa.homenavbar

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.NavHostFragment
import com.upnvjatim.silaturahmi.DATAUSER
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.ActivityNavBarMhsBinding

class NavBarMhsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavBarMhsBinding
    private lateinit var sharedpref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavBarMhsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) //, systemBars.bottom)
//            insets
//        }

        sharedpref = this.getSharedPreferences(DATAUSER, MODE_PRIVATE)
//        val role = sharedpref.getString(SHAREDPREF_ROLES, "")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("check destination navbar", "${destination.id} ${destination.label}")
            when (destination.id) {
                R.id.homeMhsFragment, R.id.messageActiveFragment, R.id.profileFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)

    }
}