package com.upnvjatim.silaturahmi.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailsPaketKonversi(
    val createdAt: String?,
    val createdBy: String?,
    val id: String?,
    val idMatkul: String?,
    val idPaketKonversi: String?,
    val kodeMatkul: String?,
    val namaMatkul: String?,
    val namaPaketKonversi: String?,
    val sks: String?,
    val updatedAt: String?,
    val updatedBy: String?
) : Parcelable