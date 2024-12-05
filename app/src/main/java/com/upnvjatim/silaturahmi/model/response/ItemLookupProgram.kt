package com.upnvjatim.silaturahmi.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLookupProgram(
    val akhirKegiatan: String?,
    val akhirPendaftaran: String?,
    val alamat: String?,
    val awalKegiatan: String?,
    val awalPendaftaran: String?,
    val createdAt: String?,
    val createdBy: String?,
    val email: String?,
    val id: String?,
    val idJenisMbkm: String?,
    val idMitra: String?,
    val idPeriode: String?,
    val idPosisiKegiatanTopik: String?,
    val idProgram: String?,
    val idProgramDitawarkan: String?,
    val isDeleted: Boolean?,
    val jenisKelamin: String?,
    val kodePeriode: String?,
    val kuota: Int?,
    val kuotaPerMitra: String?,
    val kuotaProgramDitawarkan: String?,
    val namaJenisMbkm: String?,
    val namaMitra: String?,
    val namaPic: String?,
    val namaPosisiKegiatanTopik: String?,
    val namaProgram: String?,
    val nik: String?,
    val nomorTelegram: String?,
    val nomorWA: String?,
    val semester: String?,
    val statusApproval: String?,
    val tahun: String?,
    val updatedAt: String?,
    val updatedBy: String?
) : Parcelable