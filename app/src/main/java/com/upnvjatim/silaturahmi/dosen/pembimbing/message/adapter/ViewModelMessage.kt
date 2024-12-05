package com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageDatabase
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageItem
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageRepository
import com.upnvjatim.silaturahmi.model.request.Message
import com.upnvjatim.silaturahmi.model.request.Sender
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjatim.silaturahmi.model.request.Replay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ViewModelMessage(private val messageRepository: MessageRepository, private val id:String) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    val pagingMessages : LiveData<PagingData<MessageItem>>
    = messageRepository.getPager(id).cachedIn(viewModelScope)

//    private val _dataMessages = MutableLiveData<PagingData<MessageItem>>()
//    val dataMessages: LiveData<PagingData<MessageItem>> get() = _dataMessages
//
//    fun loadMessages(last: String?, groupId: String) {
//        viewModelScope.launch {
//
//            val liveData = MutableLiveData<PagingData<MessageItem>>()
//
//            // Firestore query initialization
//            var eventsCollection: Query? = FirebaseFirestore.getInstance()
//                .collection("chats").document(groupId)
//                .collection("messages")
//                .orderBy("timestamp", Query.Direction.ASCENDING)
//
//            last?.let {
//                if (it.isNotEmpty()) {
//                    FirebaseFirestore.getInstance()
//                        .collection("chats").document(groupId)
//                        .collection("messages").document(it)
//                        .get().addOnSuccessListener { documentSnapshot ->
//                            eventsCollection = eventsCollection?.startAfter(documentSnapshot)?.limit(10)
//                        }
//                } else {
//                    eventsCollection = eventsCollection?.limit(10)
//                }
//                fetchMessages(liveData, eventsCollection)
//            }
//        }
//    }
//
//        private fun fetchMessages
//                    (liveData: MutableLiveData<PagingData<MessageItem>>, eventsCollection: Query?) {
//            eventsCollection?.addSnapshotListener { snapshot, _ ->
//                if (snapshot == null) {
//                    Log.d("cek snapshot eventcoll", "${snapshot} is null")
//                    return@addSnapshotListener
//                }
//                try {
//                    val messages = snapshot.documents.map {
//                        MessageItem(
//                            chatId = it.id,
//                            message = it.data?.get("message")?.toString() ?: "",
//                            username = it.data?.get("username")?.toString() ?: "",
//                            name = it.data?.get("name")?.toString() ?: "",
//                            timestamp = it.getTimestamp("timestamp")?.toDate()?.toString() ?: Timestamp.now().toDate().toString(),
//                        )
//                    }
//                    _dataMessages.postValue(PagingData.from(messages)) // Update LiveData with PagingData
//                    Log.d("cek subscription messages", "${messages.size}")
//                } catch (e: Throwable) {
//                    Log.e("SUBSCRIPTION ERROR", e.message.toString())
//                }
//            }
//        }


    private var mDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    val messageLiveData = MutableLiveData<String>()

    fun sendMessages(
        documentId:String, username: String, name: String, idD: String, timestamp: Date,
        replay: String?,  replaySender: String?, context: Context
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("cek mess", messageLiveData.value.toString())
            if (messageLiveData.value?.toString() != "") {
                mDb
                    .collection("chats")
                    .document(idD)
                    .update(
                        "lastMessage", messageLiveData.value.toString(),
                        "timestamp", Timestamp.now().toDate()
                    ).addOnSuccessListener {
                        mDb
                            .collection("chats").document(idD)
                            .collection("messages").document(documentId)
                            .set(
                                Message(
                                    chatId = documentId,
                                    sender = Sender(username, name),
                                    message = messageLiveData.value.toString(),
                                    timestamp = timestamp,
                                    replay = Replay(replay, replaySender)
                                )
                            ).addOnSuccessListener {
//                                if (message.value!!.isNotEmpty()) {
//                                    PushNotification(
//                                        NotificationData(loggedInUsername, message.value!!), token!!
//                                    ).also {
//                                        sendNotification(it)
//                                    }
//                                } else {
//                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
//                                }

                                messageLiveData.value = ""

//                                getAllMessages(idD)
                            }.addOnFailureListener {
                                Log.e("error firestore", "${it.message} ${it.stackTrace}")
                            }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("error remove members", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
            }
        }
}

class MessageViewModelFactory(private val context: Context, private val idGroup:String)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelMessage::class.java)) {
            val database = MessageDatabase.getDatabase(context)
            return ViewModelMessage(MessageRepository(database), idGroup) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

