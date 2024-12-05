package com.upnvjatim.silaturahmi.dosen.pembimbing.message

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentMessageRiwayatBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.GroupChats
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelGroupChat
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.ViewModelMessage
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.LoadMessageAdapter
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MessageViewModelFactory
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageDatabase
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MessageAdapter
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.QuoteClickListener
import com.upnvjatim.silaturahmi.model.request.Replay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageRiwayatFragment : Fragment(), QuoteClickListener {
    private var _binding: FragmentMessageRiwayatBinding? = null
    private val binding get() = _binding!!
    private var replyMessage: MutableLiveData<String?> = MutableLiveData()
    private var replySender: MutableLiveData<String?> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        val getData = arguments?.getParcelable("gc") as GroupChats?

        binding.toolbarTitle.text = getData?.groupName
        binding.toolbarTitle.isSelected = true
        binding.toolbar.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        binding.toolbar.setNavigationIconTint(Color.parseColor("#458654"))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("groupchat", getData)
            findNavController().navigate(R.id.action_messageRiwayatFragment_to_detailGroupChatsFragment, bundle)
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val viewModelMess : ViewModelMessage by viewModels {
            MessageViewModelFactory(requireContext(), getData?.groupId.toString())
        }

        val viewmodelGc = ViewModelProvider(this)[ViewModelGroupChat::class.java]
        viewmodelGc.getMembersGc(getData?.groupId.toString())
        viewmodelGc.membersGc.observe(viewLifecycleOwner){
            val a = viewmodelGc.membersGc.value?.map { it.name }?.joinToString(", ")
            binding.toolbarSubtitle.visibility = View.VISIBLE
            binding.toolbarSubtitle.text = a
        }
        val username = getUser(requireContext())?.user?.username.toString()

        val adapter = MessageAdapter(requireContext(), username)
//        val adapter = ChatAdapter(username)

        getAllMessages(adapter, viewModelMess)

        binding.btnSend.setOnClickListener {
            val message = binding.editTextbox.text.toString().trim()
            if (message != "" && message.isNotEmpty() && message.isNotBlank()) {
                binding.editTextbox.isEnabled = false
                viewModelMess.messageLiveData.value = binding.editTextbox.text.toString()

                val role = getUser(requireContext())?.user?.role?.id.toString()
                val username = getUser(requireContext())?.user?.username.toString()
                val nip = getUser(requireContext())?.user?.nip ?: getUser(requireContext())?.user?.username

                val documentReference = FirebaseFirestore.getInstance()
                    .collection("chats").document(getData?.groupId.toString())
                    .collection("messages").document() // Generate document reference
                val documentId = documentReference.id // Get the document ID

                val nameUser =  if(role == "HA02") username
                else if(nip != "" && nip != null && nip != "null") nip.toString()
                else username
                val name = getUser(requireContext())?.user?.name.toString()

                viewModelMess.sendMessages(
                    username = nameUser, name = name,
                    idD = getData?.groupId.toString(),
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
                        cancelReply()
                    } else binding.editTextbox.isEnabled = false

                    listenForNewMessages(
                        getData?.groupId.toString(), MessageDatabase.getDatabase(requireContext())
                    )
                }

                if (binding.editTextbox.text.trim().isNotEmpty()) {
                    if (replyMessage.value != null && replySender.value != null) {
                        Log.d("add quote mess", "if (binding.includeReply.root.visibility == View.VISIBLE) {\n" +
                                "                        hideReplyLayout()")
//                        mainActivityViewModel.addQuotedMessage(
//                            edit_message.text.trim().toString(),
//                            textQuotedMessage.text.toString(),
//                            quotedMessagePos
//                        )
                    } else {
//                        mainActivityViewModel.addMessage(edit_message.text.trim().toString())
                        Log.d("add message", "else that means it is not (binding.includeReply.root.visibility == View.VISIBLE) {\n" +
                                "                        hideReplyLayout()")
                    }
                }

            } else Toast.makeText(requireContext(), "Write Something", Toast.LENGTH_SHORT).show()
        }
        replyMess(adapter)
    }

    private fun getAllMessages(adapter: MessageAdapter, viewModelMess: ViewModelMessage) {
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
                    } else if(loadStates.refresh is LoadState.Loading){
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

    private fun replyMess(adapter: MessageAdapter){

        val messageSwipeController = MessageSwipeController(object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                val message = adapter.getMessageItem(position)
                Log.d("cek showreplyui", "$position ${message?.message} ; ${message?.reply}")
                if (message != null) {
                    showQuotedMessage(message)
                }
            }
        })

        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(binding.rvMessage)

//        mainActivityViewModel.getDisplayMessage()
//            .observe(this, Observer<List<Message>> { messages ->
//                messageList.clear()
//                messageList.addAll(messages)
//                adapter.setMessages(messages)
//                Handler().postDelayed({
//                    recyclerView.scrollToPosition(adapter.itemCount - 1);
//                }, 100)
//            })

        binding.includeReply.btnCancel.setOnClickListener {
            cancelReply()
        }

    }

    private fun cancelReply() {
        binding.includeReply.linearReply.visibility = View.GONE
        replyMessage.value = null
        replySender.value = null
    }


    private fun showQuotedMessage(message: MessageItem) {
        // Focus on the edit text and show the keyboard
        binding.editTextbox.requestFocus()
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.editTextbox, InputMethodManager.SHOW_IMPLICIT)

        replyMessage.value = message.message
        replySender.value = message.name

        // Show the reply layout if it's not visible
        if (replyMessage.value != null && replySender.value != null) {
            binding.includeReply.linearReply.visibility = View.VISIBLE
            binding.includeReply.txtSenderReply.text = replySender.value
            binding.includeReply.txtMessageReply.text = replyMessage.value
        }

    }

    override fun onQuoteClick(messageItem: MessageItem) {
        val position = messageItem.chatId.toIntOrNull()?.minus(1)?:0
        binding.rvMessage.smoothScrollToPosition(position)
        (binding.rvMessage.adapter as MessageAdapter).blinkItem(position)
    }
}



