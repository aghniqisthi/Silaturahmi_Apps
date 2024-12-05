package com.upnvjatim.silaturahmi.dosen.pembimbing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentMagangBinding
import com.upnvjatim.silaturahmi.dosen.pembimbing.apprmagang.ApprovalPaketFragment
import com.upnvjatim.silaturahmi.dosen.pembimbing.verifnilai.VerifNilaiFragment
import com.upnvjatim.silaturahmi.viewmodel.adapter.ViewPagerMagangAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MagangFragment : Fragment() {
    private var _binding: FragmentMagangBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMagangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        val adapter = ViewPagerMagangAdapter(requireActivity())
        binding.viewpagerMagang.setAdapter(adapter)

        TabLayoutMediator(binding.tabLayout, binding.viewpagerMagang) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "Approval Magang"
                }
                1 -> {
                    tab.text = "Verifikasi Penilaian"
                }
            }
        }.attach()


        findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.verifNilaiFragment -> {
                    (adapter.getItemId(0) as VerifNilaiFragment).loadData()
                }
                R.id.approvalPaketFragment -> {
                    (adapter.getItemId(1) as ApprovalPaketFragment).loadData()
                }
            }
        }


    }
}

