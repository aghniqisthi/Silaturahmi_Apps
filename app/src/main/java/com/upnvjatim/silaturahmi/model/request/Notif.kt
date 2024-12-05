package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notif(
    val username:String,
    val status:String,
    val isFinish:Boolean,
    val notif:List<DataNotif>
//    val message:List<String>
) : Parcelable

@Parcelize
data class DataNotif(
    val title: String,
    val message: String,
    val seen: Boolean
) : Parcelable