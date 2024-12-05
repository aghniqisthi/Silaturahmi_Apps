package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message(
    val chatId:String,
    val message:String,
    val sender:Sender,
    val replay:Replay?,
    val timestamp: Date,
//    val stability: String? = null
) : Parcelable

@Parcelize
data class Replay(
    val message: String?,
    val name: String?
) : Parcelable

@Parcelize
data class Sender(
    val username: String,
    val name: String
) : Parcelable

@Parcelize
data class MessagesList(
    val messages: List<Message>
) : Parcelable

//
////    val idReplied:String,
////    val repliedMessage:String,
////    val messageId:String,