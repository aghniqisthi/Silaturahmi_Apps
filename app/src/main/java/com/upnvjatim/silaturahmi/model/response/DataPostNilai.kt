package com.upnvjatim.silaturahmi.model.response

data class DataPostNilai(
    val id: String,
    val idSiamikMahasiswa: String,
    val idPegawai: String,
    val status: Int,
    val idMitraTerlibatProgram: String,
    val statusApproval: String,
    val linkPenilaian: Any,
    val tglPenilaian: Any,
    val ketPenilaian: Any,
    val nilaiAngka: Any,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: Any,
    val updatedBy: Any,
    val isDeleted: Boolean
)
