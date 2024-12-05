package com.upnvjatim.silaturahmi.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLookupPaketKonversi(
    val createdAt: String?,
    val createdBy: String?,
    val deskripsi: String?,
    var detail: List<DetailsPaketKonversi?>?,
    val id: String?,
    val idJenisMbkm: String?,
    val idProgram: String?,
    val idProgramProdi: String?,
    val isDeleted: Boolean?,
    val kdProdi: String?,
    val namaPaketKonversi: String?,
    val namaProdi: String?,
    val namaProgram: String?,
    val updatedAt: String?,
    val updatedBy: String?
) : Parcelable