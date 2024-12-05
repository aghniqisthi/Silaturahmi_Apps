package com.upnvjatim.silaturahmi.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLogbook(
    val deskripsi: String?,
    val fakjur: String?,
    val id: String?,
    val idPendaftar: String?,
    val link: String?,
    val namaDokumen: String?,
    val namaDosenPembimbing: String?,
    val namaMahasiswa: String?,
    val namaMitra: String?,
    val nip: String?,
    val npm: String?,
    val tanggal: String?,
    val tgl: String?
) : Parcelable