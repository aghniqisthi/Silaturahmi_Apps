package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM message ORDER BY timestamp DESC") // Ensure correct ordering by timestamp
    fun getMessages(): PagingSource<Int, MessageItem> // Use PagingSource for local cache

//    @Query("SELECT * FROM message ORDER BY timestamp DESC LIMIT 1")
//    suspend fun getLastMessage(): MessageItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messages: List<MessageItem>)

    @Query("DELETE FROM message")
    suspend fun deleteAll()
}
