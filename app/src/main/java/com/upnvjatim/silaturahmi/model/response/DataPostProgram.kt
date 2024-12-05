package com.upnvjatim.silaturahmi.model.response

data class DataPostProgram(
    val id: String,
    val idSiamikMahasiswa: String,
    val idPegawai: Any?,
    val status: Int,
    val idMitraTerlibatProgram: Any?,
    val statusApproval: String,
    val statusMbkm: String,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: Any?,
    val updatedBy: Any?,
    val isDeleted: Boolean,
    val link: String,
    val isMagangMandiri: Boolean,
    val isFinish: Boolean
)