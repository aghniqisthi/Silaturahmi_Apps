package com.upnvjatim.silaturahmi.mahasiswa.luaran

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.upnvjatim.silaturahmi.databinding.ActivityLuaranNilaiBinding
import com.upnvjatim.silaturahmi.viewmodel.adapter.ViewPagerLuaranNilaiAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LuaranNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLuaranNilaiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLuaranNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.isTitleCentered = false
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.viewpagerLuarannilai.setAdapter(ViewPagerLuaranNilaiAdapter(this))

        TabLayoutMediator(binding.tabLayout, binding.viewpagerLuarannilai) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "Luaran"
                }
                1 -> {
                    tab.text = "Nilai Akhir"
                }
            }
        }.attach()
    }
}