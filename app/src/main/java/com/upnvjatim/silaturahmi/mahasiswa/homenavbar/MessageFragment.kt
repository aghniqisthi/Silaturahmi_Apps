package com.upnvjatim.silaturahmi.mahasiswa.homenavbar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentMessageBinding
import com.upnvjatim.silaturahmi.getIdPendaftar
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelGroupChat
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.LoadMessageAdapter
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MessageViewModelFactory
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelMessage
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageDatabase
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MessageAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private var replyMessage: MutableLiveData<String?> = MutableLiveData()
    private var replySender: MutableLiveData<String?> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewmodelGc = ViewModelProvider(this)[ViewModelGroupChat::class.java]

        binding.toolbarTitle.isSelected = true
        binding.toolbar.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val role = getUser(requireContext())?.user?.role?.id.toString()
        val username = getUser(requireContext())?.user?.username.toString()
        val semester = getIdPendaftar(requireContext())?.semester.toString()
        val tahun = getIdPendaftar(requireContext())?.tahun.toString()
        val nip = getUser(requireContext())?.user?.nip

        viewmodelGc.getActiveGroupChats(username, semester, tahun)
        viewmodelGc.detailGroupChats.observe(viewLifecycleOwner) { dt ->
            if (dt != null) {
                binding.linearEmptydata.visibility = View.GONE
                binding.btnSend.visibility = View.VISIBLE
                binding.editTextbox.visibility = View.VISIBLE
                binding.cardView4.visibility = View.VISIBLE

                val viewModelMess : ViewModelMessage by viewModels {
                    MessageViewModelFactory(requireContext(), dt.groupId.toString())
                }

                viewmodelGc.getMembersGc(dt.groupId.toString())
                viewmodelGc.membersGc.observe(viewLifecycleOwner) {
                    val a = viewmodelGc.membersGc.value?.map { it.name }?.joinToString(", ")
                    binding.toolbarSubtitle.visibility = View.VISIBLE
                    binding.toolbarSubtitle.text = a
                }

                binding.toolbarTitle.text = dt.groupName
                binding.toolbar.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("groupchat", dt)
                    findNavController().navigate(
                        R.id.action_messageActiveFragment_to_detailGroupChatsFragment, bundle
                    )
                }

                val adapter = MessageAdapter(requireContext(),
//                val adapter = ChatAdapter(
                    if (role == "HA02") username
                    else if (nip != "" && nip != null && nip != "null") nip.toString()
                    else username //, it, viewmodel
                )

                getAllMessages(adapter, dt.groupId.toString())

                binding.btnSend.setOnClickListener {
                    val message = binding.editTextbox.text.toString().trim()

                    if (message != "" && message.isNotEmpty() && message.isNotBlank()) {
                        binding.editTextbox.isEnabled = false
                        viewModelMess.messageLiveData.value = binding.editTextbox.text.toString()

                        val role = getUser(requireContext())?.user?.role?.id.toString()
                        val username = getUser(requireContext())?.user?.username.toString()
                        val nip = getUser(requireContext())?.user?.nip ?: getUser(requireContext())?.user?.username

                        val documentReference = FirebaseFirestore.getInstance()
                            .collection("chats").document(dt.groupId.toString())
                            .collection("messages").document() // Generate document reference
                        val documentId = documentReference.id // Get the document ID

                        val nameUser =  if(role == "HA02") username
                        else if(nip != "" && nip != null && nip != "null") nip.toString()
                        else username

                        val name = getUser(requireContext())?.user?.name.toString()

                        viewModelMess.sendMessages(
                            username = nameUser, name = name,
                            idD = dt.groupId.toString(),
                            context = requireContext(),
                            documentId = documentId,
                            timestamp = Timestamp.now().toDate(),
                            replaySender = replyMessage.value,
                            replay = replySender.value
                        )
                        viewModelMess.messageLiveData.observe(viewLifecycleOwner) {
                            if(it.isNullOrEmpty() || it.isBlank()) {
                                binding.editTextbox.isEnabled = true
                                binding.editTextbox.setText(it)

                            } else binding.editTextbox.isEnabled = false

                            listenForNewMessages(
                                dt.groupId.toString(), MessageDatabase.getDatabase(requireContext())
                            )

                        }

                    } else Toast.makeText(requireContext(), "Write Something", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.txtEmpty.text = "Group chat tidak ditemukan."
                binding.btnSend.visibility = View.GONE
                binding.editTextbox.visibility = View.GONE
                binding.cardView4.visibility = View.GONE
            }
        }

        viewmodelGc.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodelGc.isError.observe(viewLifecycleOwner) {
            binding.txtEmpty.text = it
            binding.linearEmptydata.visibility = View.VISIBLE
        }
    }

    private fun getAllMessages(adapter: MessageAdapter, groupId: String) {
        val viewModelMess : ViewModelMessage by viewModels {
            MessageViewModelFactory(requireContext(), groupId)
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)

        binding.rvMessage.layoutManager = layoutManager
        binding.rvMessage.adapter = adapter.withLoadStateFooter(
            LoadMessageAdapter {
                Log.d("cek laodmess", "load adapyer ${adapter.itemCount}")
                adapter.retry()
            }
        )
        binding.rvMessage.smoothScrollToPosition(adapter.itemCount)

        viewModelMess.pagingMessages.observe(viewLifecycleOwner) { pagingData ->
            // Submit data to the adapter
            adapter.submitData(lifecycle, pagingData)

            // Check if there's any data in the PagingData
            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    // Show or hide empty data layout
                    if ( adapter.itemCount == 0 && loadStates.refresh is LoadState.NotLoading
                        && loadStates.append.endOfPaginationReached ) {
                        binding.linearEmptydata.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    } else
                        if(loadStates.refresh is LoadState.Loading){
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        else {
                            binding.linearEmptydata.visibility = View.INVISIBLE
                            binding.progressBar.visibility = View.GONE
                        }
                }
            }
        }

        viewModelMess.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }

        viewModelMess.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun listenForNewMessages(groupId: String, database: MessageDatabase) {
        FirebaseFirestore.getInstance()
            .collection("chats").document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("FirestoreListener", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val newMessages = snapshots.documentChanges.mapNotNull { change ->
                        if (change.type == DocumentChange.Type.ADDED) {
                            val data = change.document
                            val userDetailsMap = data.get("sender") as? Map<String, String>
                            val username = userDetailsMap?.get("username")
                            val name = userDetailsMap?.get("name")

                            MessageItem(
                                chatId = data.getString("chatId") ?: "",
                                message = data.getString("message") ?: "",
                                username = username, name = name,
                                timestamp = data.getTimestamp("timestamp")?.toDate()?.time
                            )
                        } else null
                    }

                    // Insert new messages into the local database
                    if (newMessages.isNotEmpty()) {
                        lifecycleScope.launch {
                            database.messageDao.insertMessage(newMessages)
                            binding.rvMessage.smoothScrollToPosition(0)
                        }
                    }

                }
            }
    }


}



