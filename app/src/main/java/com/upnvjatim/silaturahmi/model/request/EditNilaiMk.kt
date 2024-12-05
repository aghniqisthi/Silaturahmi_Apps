package com.upnvjatim.silaturahmi.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PostNilaiFinal(
    var data:List<EditNilaiMk>
)

@Parcelize
data class EditNilaiMk(
    var id:String,
    var idMatkul:String,
    var idPaketKonversi:String,
    var idPendaftarMbkm:String,
    var nilaiAngka:String,
    var nilaiHuruf:String?,
    var sks:String
) : Parcelable

