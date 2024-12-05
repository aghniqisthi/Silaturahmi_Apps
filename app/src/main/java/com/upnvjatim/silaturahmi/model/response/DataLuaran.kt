package com.upnvjatim.silaturahmi.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataLuaran(
    val createdAt: String?,
    val createdBy: String?,
    val id: String?,
    val idJenisLuaran: String?,
    val idPendaftarProgramMbkm: String?,
    val isDeleted: Boolean?,
    val keterangan: String?,
    val link: String?,
    val namaLuaran: String?,
    val tanggal: String?,
    val updatedAt: String?,
    val updatedBy: String?
) : Parcelable