package com.upnvjatim.silaturahmi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upnvjatim.silaturahmi.databinding.ActivitySplashBinding
import com.upnvjatim.silaturahmi.dosen.kaprodi.NavbarKaprodiActivity
import com.upnvjatim.silaturahmi.dosen.pembimbing.NavbarPembimbingActivity
import com.upnvjatim.silaturahmi.dosen.penilai.NavbarPenilaiActivity
import com.upnvjatim.silaturahmi.dosen.pic.NavbarPicActivity
import com.upnvjatim.silaturahmi.dosen.timmbkm.NavbarTimMbkmActivity
import com.upnvjatim.silaturahmi.mahasiswa.homenavbar.NavBarMhsActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (
//            getUser(this)?.user?.role?.id.isNullOrEmpty()
//            || getUser(this)?.user?.role?.id.isNullOrBlank()
//            || getUser(this)?.user?.role?.id == null
//            ||
                (getUser(this)?.user?.username.isNullOrEmpty()
                || getUser(this)?.user?.username == ""
                || getUser(this)?.user?.username == null
                || getUser(this)?.user?.username == "null")
                && getUser(this)?.user?.role?.id != "HA02"
            ) {
            Handler().postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        } else {
            val intent = when (getUser(this)?.user?.role?.id) {
                "HA02" -> Intent(this, NavBarMhsActivity::class.java)
                "HA03" -> Intent(this, NavbarPicActivity::class.java)
                "HA05" -> Intent(this, NavbarTimMbkmActivity::class.java)
                "HA06" -> Intent(this, NavbarPenilaiActivity::class.java)
                "HA07" -> Intent(this, NavbarKaprodiActivity::class.java)
                "HA08" -> Intent(this, NavbarPembimbingActivity::class.java)
                else -> Intent(this, NavbarKaprodiActivity::class.java)
            }
            Handler().postDelayed({
                startActivity(intent)
                finish()
            }, 3000)
        }
    }
}