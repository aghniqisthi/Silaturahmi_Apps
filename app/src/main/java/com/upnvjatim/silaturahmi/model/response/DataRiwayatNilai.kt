package com.upnvjatim.silaturahmi.model.response

data class DataRiwayatNilai(
    val idNilaiAwal: String?,
    val idNilaiFinal: String?,
    val isDeleted: Boolean?,
    val kodeMatkul: String?,
    val namaMatkul: String?,
    val nilaiAngka: String?,
    val nilaiAngkaFinal: String?,
    val sks: Int?
)