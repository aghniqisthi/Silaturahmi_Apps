package com.upnvjatim.silaturahmi.model.response

data class DataPaketKonversi(
    val deskripsi: String?,
    val idPaketKonversi: String?,
    val listMatkul: List<MatkulKonversi?>?,
    val namaPaketKonversi: String?
)