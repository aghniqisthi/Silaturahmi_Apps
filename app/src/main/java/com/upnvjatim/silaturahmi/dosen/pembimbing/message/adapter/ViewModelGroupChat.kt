package com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.upnvjatim.silaturahmi.model.request.GroupChats
import com.upnvjatim.silaturahmi.model.request.UserGroupChats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewModelGroupChat(app: Application) : AndroidViewModel(app) {

    //    private var allGroupChat: MutableLiveData<List<DocumentSnapshot>> = MutableLiveData()
    private var mDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userGroupChats = MutableLiveData<List<GroupChats>>()
    val userGroupChats: LiveData<List<GroupChats>> get() = _userGroupChats

    private val _detailGroupChats = MutableLiveData<GroupChats?>()
    val detailGroupChats: LiveData<GroupChats?> get() = _detailGroupChats

    private val _membersGc = MutableLiveData<List<UserGroupChats>?>()
    val membersGc: LiveData<List<UserGroupChats>?> get() = _membersGc

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getUserGroupChats(username: String) {
        _isLoading.value = true
        Log.d("cek userename", username)
        val getDoc = FirebaseFirestore.getInstance()
            .collection("users").document(username)
            .collection("groupChats")

        getDoc.get().addOnSuccessListener { querySnapshot ->
                _isLoading.value = false
                Log.d("cek user groupchats", "${querySnapshot.documents.size}")
                val groupChats = mutableListOf<GroupChats>()
//                    runBlocking {
//                        groupChatIds.forEach { groupId ->
//                            launch {
//                                val groupChatDetails = fetchGroupChatDetails(groupId)
//                                groupChatDetails.let {
//                                    if(it != null) groupChats.add(it)
//                                    Log.d("cek data groupchats", "${it?.groupName} ; ${it?.members?.size}")
//                                }
//                            }
//                        }
//                    }

                viewModelScope.launch {
                    querySnapshot.documents.forEach {
                        val groupId = it.id
                        val groupChatDetails = fetchGroupChatDetails(groupId) // Fetch details for each group
                        groupChatDetails?.let {
                            groupChats.add(it)
                            Log.d("cek data user groupchats", "${groupId} ; ${it.groupName} ; ${it.lastMessage}")
                        }
                    }
                    _userGroupChats.postValue(groupChats) // After fetching all group chats, update LiveData
                }
//            } else {
//                    _isLoading.value = false
//                    Log.e(ContentValues.TAG, "itNull: ${querySnapshot}")
////                    _isError.postValue("Groupchat tidak ditemukan")
//                }
            }.addOnFailureListener {
                    _isLoading.value = false
                    _isError.postValue(it.message)
                    Log.e(
                        ContentValues.TAG,
                        "onFailure getgroupchat ${it.cause}: ${it.message.toString()} ${it.stackTrace}"
                    )
                }
//        }
    }

    fun getActiveGroupChats(username: String, semester: String, tahun: String) {
        _isLoading.value = true
        Log.d("cek userename", username)

        FirebaseFirestore.getInstance()
            .collection("users").document(username)
            .collection("groupChats")
            .whereEqualTo("semester", semester)
            .whereEqualTo("tahun", tahun)
            .get().addOnSuccessListener { querySnapshot ->
                if(querySnapshot != null && !querySnapshot.isEmpty && querySnapshot.size() > 0){
                    _isLoading.value = false
//                viewModelScope.launch {
                    Log.d("cek groupchats", "${querySnapshot.documents.size}")
                    getGroupChatDetail(querySnapshot.documents.first().id)
//                    groupChatDetails?.let {
//                        groupChats.add(it)
//                        Log.d("cek data groupchats", "${groupId} ; ${it.groupName} ; ${it.lastMessage}")
//                    }
//                    _userGroupChats.postValue(groupChats) // After fetching all group chats, update LiveData
//                    }
                } else {
                    Log.d(ContentValues.TAG, "itNull: ${querySnapshot.size()}")
                    _detailGroupChats.postValue(null)
                    _isLoading.postValue(false)
                    _isError.postValue("Group chat aktif tidak ditemukan")
                }
            }.addOnFailureListener {
                    _isLoading.value = false
                    _isError.postValue(it.message)
                    Log.e(
                        ContentValues.TAG,
                        "onFailure getgroupchat ${it.cause}: ${it.message.toString()} ${it.stackTrace}"
                    )
                }
//        }
    }

    fun getMembersGc(groupId: String) {
        _isLoading.value = true

        val getDoc = FirebaseFirestore.getInstance()
            .collection("chats").document(groupId)
            .collection("members")

        getDoc.get().addOnSuccessListener { querySnapshot ->
                _isLoading.value = false
                Log.d("cek members gc $groupId", "${querySnapshot.documents.size}")

//                val groupChats = mutableListOf<GroupChats>()
//                    runBlocking {
//                        groupChatIds.forEach { groupId ->
//                            launch {
//                                val groupChatDetails = fetchGroupChatDetails(groupId)
//                                groupChatDetails.let {
//                                    if(it != null) groupChats.add(it)
//                                    Log.d("cek data groupchats", "${it?.groupName} ; ${it?.members?.size}")
//                                }
//                            }
//                        }
//                    }

//            viewModelScope.launch {
                val a = querySnapshot.documents.map {
                    UserGroupChats(
                        idProgramMbkm = it.data?.get("idProgramMbkm").toString(),
                        name = it.data?.get("name").toString(),
                        description = it.data?.get("description").toString(),
                        avatar = it.data?.get("avatar").toString(),
                        npm = it.data?.get("npm").toString(),

//                        idPegawai = it.data?.get("idPegawai").toString(),
//                        idProdi = it.data?.get("idProdi").toString(),
//                        idRole = it.data?.get("idRole").toString(),
//                        idSiamikMahasiswa = it.data?.get("idSiamikMahasiswa").toString(),
//                        idUser = it.data?.get("idUser").toString(),
//                        kdFakjur = it.data?.get("kdFakjur").toString(),
//                        namaProdi = it.data?.get("namaProdi").toString(),
//                        name = it.data?.get("name").toString(),
//                        userAccessToken = it.data?.get("userAccessToken").toString(),
//                        username = it.data?.get("username").toString()
                    )
//                        val groupId = it.id
//                        val groupChatDetails = fetchGroupChatDetails(groupId) // Fetch details for each group
//                        groupChatDetails?.let {
//                            groupChats.add(it)
                }
            Log.d("cek data members groupchats", "${groupId} ; ${a.size}")
            _membersGc.postValue(a)
//            }
//            } else {
//                    _isLoading.value = false
//                    Log.e(ContentValues.TAG, "itNull: ${querySnapshot}")
////                    _isError.postValue("Groupchat tidak ditemukan")
//                }
        }.addOnFailureListener {
            _isLoading.value = false
            _isError.postValue(it.message)
            Log.e(
                ContentValues.TAG,
                "onFailure getgroupchat ${it.cause}: ${it.message.toString()} ${it.stackTrace}"
            )
        }
    }

    fun clearMembersCache() {
        _membersGc.value = null // Clear members data by setting an empty list
    }

    fun getGroupChatDetail(groupId: String) {
        _isLoading.value = true

        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(groupId)
            .get().addOnSuccessListener { chatDoc ->
                _isLoading.value = false
                viewModelScope.launch {
                    val groupId = chatDoc.getString("groupId") ?: ""
                    val groupName = chatDoc.getString("groupName") ?: ""
                    val lastMessage = chatDoc.getString("lastMessage") ?: ""
                    val lecturerId = chatDoc.getString("lecturerId") ?: ""
                    val timestamp = chatDoc.getTimestamp("timestamp")
                    val semester = chatDoc.getString("semester") ?: ""
                    val tahun = chatDoc.getString("tahun") ?: ""

                    _detailGroupChats.postValue(GroupChats(
                        groupId, groupName, lastMessage, lecturerId, timestamp?.toDate(), semester, tahun
                    ))
                }
            }.addOnFailureListener {
                _isLoading.value = false
                _isError.postValue(it.message)
                Log.e(
                    ContentValues.TAG, "onFailure getdetailgroupchat ${it.cause}: ${it.message.toString()} ${it.stackTrace}"
                )
            }
    }

    suspend fun fetchGroupChatDetails(groupId: String) : GroupChats? {
        return try {
            val chatDoc = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(groupId)
                .get()
                .await()

            if (chatDoc.exists()) {
                val groupId = chatDoc.getString("groupId") ?: ""
                val groupName = chatDoc.getString("groupName") ?: ""
                val lastMessage = chatDoc.getString("lastMessage") ?: ""
                val lecturerId = chatDoc.getString("lecturerId") ?: ""
                val timestamp = chatDoc.getTimestamp("timestamp")
                val semester = chatDoc.getString("semester") ?: ""
                val tahun = chatDoc.getString("tahun") ?: ""

                GroupChats(groupId, groupName, lastMessage, lecturerId, timestamp?.toDate(), semester, tahun)
            } else {
                _isError.postValue("Detail groupchat ${groupId} tidak ditemukan")
                Log.e(ContentValues.TAG, "Error fetching group chat details: ${chatDoc} empty")
                null
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Exception fetching group chat details: ${e.cause} ; ${e.message} ; ${e.stackTrace}")
            _isError.postValue("Failed ${e.cause} : ${e.message}")
            null
        }
//        FirebaseFirestore.getInstance()
//            .collection("chats")
//            .document(groupId)
//            .get()
//            .addOnSuccessListener { chatDoc ->
//                if (chatDoc.exists()) {
//                    val groupId = chatDoc.data?.get("groupId").toString()
//                    val groupName = chatDoc.data?.get("groupName").toString()
//                    val lastMessage = chatDoc.data?.get("lastMessage").toString()
//                    val members = chatDoc.data?.get("members") as List<String>
//                    val timestamp = chatDoc.data?.get("timestamp") as Timestamp
//                    val semester = chatDoc.data?.get("semester").toString()
//                    val tahun = chatDoc.data?.get("tahun").toString()
//
//                    return GroupChats(groupId, groupName, lastMessage, members, timestamp.toDate(), semester, tahun)
//                }
//            }
    }

//    fun getGroupChatDetails(groupId: String) {
////        try{
//            FirebaseFirestore.getInstance()
//                .collection("chats")
//                .document(groupId)
//                .get().addOnSuccessListener { chatDoc ->
//                    if (chatDoc.exists()) {
//                        viewModelScope.launch {
//
//                        }
//                    } else {
//                        _isError.postValue("Detail groupchat ${groupId} tidak ditemukan")
//                    }
//                }.addOnFailureListener {
//                    _isError.value = "Failed ${it.cause} : ${it.message}"
//                    Log.e("error fetch group details", "${it.message} ${it.cause} ${it.stackTrace}")
//                }
//        } catch (e: Exception) {
//            Log.e(ContentValues.TAG, "Error fetching group chat details: ${e.cause} ; ${e.message} ; ${e.stackTrace}")
//            _isError.postValue("Failed ${e.cause} : ${e.message}")
//            null
//        }


//        FirebaseFirestore.getInstance()
//            .collection("chats")
//            .document(groupId)
//            .get()
//            .addOnSuccessListener { chatDoc ->
//                if (chatDoc.exists()) {
//                    val groupId = chatDoc.data?.get("groupId").toString()
//                    val groupName = chatDoc.data?.get("groupName").toString()
//                    val lastMessage = chatDoc.data?.get("lastMessage").toString()
//                    val members = chatDoc.data?.get("members") as List<String>
//                    val timestamp = chatDoc.data?.get("timestamp") as Timestamp
//                    val semester = chatDoc.data?.get("semester").toString()
//                    val tahun = chatDoc.data?.get("tahun").toString()
//
//                    return GroupChats(groupId, groupName, lastMessage, members, timestamp.toDate(), semester, tahun)
//                }
//            }
//    }

//    fun addMembers(usernameMhs: String, nip: String, tahun: Int, semester: Int) {
//        GlobalScope.launch {
//            mDb
//                .collection("chats")
//                .whereEqualTo("lecturerId", nip)
//                .whereEqualTo("tahun", tahun)
//                .whereEqualTo("semester", semester)
//                .get().addOnSuccessListener {
//                    if (it != null) {
//                        if (it.documents.size > 0 && it.documents.isNotEmpty()) {
//
//                            mDb.collection("chats")
//                                .document(it.documents.first().id)
//                                .update("members", FieldValue.arrayUnion(usernameMhs))
//                                .addOnSuccessListener {
//
//                                }
//                                .addOnFailureListener {
//                                    _isError.value = "Error : ${it.message}"
//                                    Log.e("error getdata addmemb", "${it.message} ${it.cause} ${it.stackTrace}")
//                                }
//
//
//
//                        } else {
//                            Log.e("no periode exists", "cek docs ${it.documents.isEmpty()}")
//                            // create new documents "Semester $1 Genap $2 Gasal Tahun $2024 - $namadosen"
//                            // members dosen + mhs
//                        }
//                    }
//                }.addOnFailureListener {
//                    Log.e("error firestore", "${it.message} ${it.stackTrace}")
//                }
//        }
//    }

}