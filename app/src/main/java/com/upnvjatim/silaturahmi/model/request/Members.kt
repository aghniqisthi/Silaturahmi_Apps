package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Members(
    val description:String?,
    val idProgramMbkm:String,
    val name: String
) : Parcelable