package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

class MessageRepository (private val messageDb: MessageDatabase) {
    fun getPager(id:String): LiveData<PagingData<MessageItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            initialKey = 1,
            config = PagingConfig(pageSize = 5),
            remoteMediator = MessageRemoteMediator(messageDb, id),
            pagingSourceFactory = {
                Log.d("cek pagingsourcefact", "getmessage from db")
                messageDb.messageDao.getMessages() },
        ).liveData
    }
}



