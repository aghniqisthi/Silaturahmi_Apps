package com.upnvjatim.silaturahmi.model.response

data class ResponseSimpanNilaiFinal(
    val data : List<DataEditNilaiMk>
)

data class DataEditNilaiMk(
    val id: String,
    val idPaketKonversi: String,
    val namaPaketKonversi: Any,
    val idMatkul: String,
    val namaMatkul: String,
    val sks: String,
    val nilaiAngka: Any,
    val nilaiHuruf: Any,
    val idPendaftarMbkm: String,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: Any,
    val updatedBy: Any,
    val isDeleted: Boolean,
)

