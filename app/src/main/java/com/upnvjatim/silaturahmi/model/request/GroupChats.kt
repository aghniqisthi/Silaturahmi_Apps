package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class GroupChats(
    val groupId:String?,
    val groupName:String,
    val lastMessage:String?,
    val lecturerId:String,
    val timestamp: Date?,
    val semester: String?,
    val tahun: String?
) : Parcelable