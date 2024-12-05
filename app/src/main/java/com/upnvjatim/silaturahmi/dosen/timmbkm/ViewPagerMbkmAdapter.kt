package com.upnvjatim.silaturahmi.dosen.timmbkm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.upnvjatim.silaturahmi.dosen.kaprodi.PlottingFragment
import com.upnvjatim.silaturahmi.dosen.timmbkm.pendaftarmagang.PendaftarMagangMbkmFragment

class ViewPagerMbkmAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendaftarMagangMbkmFragment()

            1 -> PlottingFragment()
//                .apply { arguments = Bundle().apply { putParcelable("warga", getData) } }
            else -> PendaftarMagangMbkmFragment()
        }
    }
}