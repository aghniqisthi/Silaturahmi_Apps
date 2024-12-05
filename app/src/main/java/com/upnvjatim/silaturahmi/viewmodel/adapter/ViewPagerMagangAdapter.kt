package com.upnvjatim.silaturahmi.viewmodel.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.upnvjatim.silaturahmi.dosen.pembimbing.apprmagang.ApprovalPaketFragment
import com.upnvjatim.silaturahmi.dosen.pembimbing.verifnilai.VerifNilaiFragment
import com.upnvjatim.silaturahmi.dosen.pic.RevisiNilaiFragment
import com.upnvjatim.silaturahmi.mahasiswa.luaran.ListLuaranFragment
import com.upnvjatim.silaturahmi.mahasiswa.luaran.NilaiAkhirFragment
import com.upnvjatim.silaturahmi.mahasiswa.notif.NotifAktivitasFragment
import com.upnvjatim.silaturahmi.mahasiswa.notif.NotifInformasiFragment

class ViewPagerMagangAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ApprovalPaketFragment()
            1 -> VerifNilaiFragment()
//                .apply { arguments = Bundle().apply { putParcelable("warga", getData) } }
            else -> ApprovalPaketFragment()
        }
    }
}

class ViewPagerLuaranNilaiAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> NilaiAkhirFragment()
            else -> ListLuaranFragment()
        }
    }
}

class ViewPagerMbkmPicAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 1 // 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
//            0 -> ListMbkmFragment()
//            1 -> ListMitraFragment()
//            2 -> RevisiNilaiFragment()
            else -> RevisiNilaiFragment() // ListMbkmFragment()
        }
    }
}

class ViewPagerNotifAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> NotifInformasiFragment()
            else -> NotifAktivitasFragment()
        }
    }
}