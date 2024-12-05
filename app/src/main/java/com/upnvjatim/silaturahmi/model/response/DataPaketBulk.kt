package com.upnvjatim.silaturahmi.model.response

data class DataPaketBulk(
    val id: String,
    val idPaketKonversi: String,
    val namaPaketKonversi: Any,
    val idMatkul: String,
    val kodeMatkul: Any,
    val namaMatkul: String,
    val sks: String,
    val nilaiAngka: Any,
    val nilaiAngkaFinal: Any,
    val nilaiHuruf: Any,
    val idPendaftarMbkm: String,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: Any,
    val updatedBy: Any,
    val isDeleted: Boolean,
    val isPenilaianKonversi: Any,
    val isMahasiswa: Boolean
)