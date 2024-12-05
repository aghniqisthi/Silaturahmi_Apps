package com.upnvjatim.silaturahmi.dosen.timmbkm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import com.upnvjatim.silaturahmi.databinding.FragmentMbkmBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MbkmFragment : Fragment() {

    private var _binding: FragmentMbkmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMbkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

//        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, 0, systemBars.right, 0)
//            insets
//        }

        binding.viewpagerMbkm.setAdapter(ViewPagerMbkmAdapter(requireActivity()))

        TabLayoutMediator(binding.tabLayout, binding.viewpagerMbkm) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> {
                    tab.text = "Pendaftar Magang"
                }
                1 -> {
                    tab.text = "Pembimbing MBKM"
                }
            }
        }.attach()
    }
}

