package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "message")
data class MessageItem(
    @PrimaryKey
    @field:SerializedName("chatId")
    val chatId: String,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("reply")
    val reply: String? = null,

    @field:SerializedName("senderReplay")
    val senderReplay: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: Long? = null
)
