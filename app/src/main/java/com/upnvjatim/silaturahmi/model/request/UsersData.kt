package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsersData(
    val avatar:String?,
//    val idPegawai:String?,
//    val idProdi:String,
//    val idRole:String,
//    val idSiamikMahasiswa:String,
//    val idUser:String,
//    val kdFakjur:String,
//    val namaProdi:String,
//    val name:String,
//    val userAccessToken:String?,
//    val username:String
) : Parcelable

@Parcelize
data class UserGroupChats(
    val idProgramMbkm:String,
    val name:String,
    val description:String?,
    val npm:String?,
    val avatar:String?
) : Parcelable

