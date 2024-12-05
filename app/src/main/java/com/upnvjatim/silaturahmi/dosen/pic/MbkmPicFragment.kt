package com.upnvjatim.silaturahmi.dosen.pic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import com.upnvjatim.silaturahmi.databinding.FragmentMbkmPicBinding
import com.upnvjatim.silaturahmi.viewmodel.adapter.ViewPagerMbkmPicAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MbkmPicFragment : Fragment() {
    private var _binding: FragmentMbkmPicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMbkmPicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        binding.viewpagerMbkm.setAdapter(ViewPagerMbkmPicAdapter(requireActivity()))

        TabLayoutMediator(binding.tabLayout, binding.viewpagerMbkm) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "MBKM"
                }
//                1 -> {
//                    tab.text = "Mitra"
//                }
//                2 -> {
//                    tab.text = "Revisi Nilai"
//                }
            }
        }.attach()
    }
}