package com.upnvjatim.silaturahmi.dosen.pembimbing.message

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentDetailGroupChatsBinding
import com.upnvjatim.silaturahmi.model.request.GroupChats
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelGroupChat
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MemberGroupChatAdapter

class DetailGroupChatsFragment : Fragment() {
    private var _binding: FragmentDetailGroupChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailGroupChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        val getData = arguments?.getParcelable("groupchat") as GroupChats?
        val viewModelGc = ViewModelProvider(this)[ViewModelGroupChat::class.java]

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.isTitleCentered = false
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.title = getData?.groupName

        viewModelGc.getMembersGc(getData?.groupId.toString())
        viewModelGc.membersGc.observe(viewLifecycleOwner) { gc ->
            if(gc.isNullOrEmpty()) {
                binding.linearEmptydata.visibility = View.VISIBLE
                binding.toolbar.subtitle = "0 Anggota"
            }
            else {
                binding.linearEmptydata.visibility = View.GONE
                binding.toolbar.subtitle = "${gc.size} Anggota"
            }

            binding.rvRiwayatchats.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvRiwayatchats.adapter = gc?.let { MemberGroupChatAdapter(it) }
        }

        viewModelGc.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }

        viewModelGc.isError.observe(viewLifecycleOwner) {
            binding.txtEmpty.text = "Failed: ${it}"
        }

    }
}