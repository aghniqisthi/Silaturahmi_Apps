package com.upnvjatim.silaturahmi.model.response

data class DataPutDaftar(
    val createdAt: String?,
    val createdBy: String?,
    val id: String?,
    val idMitraTerlibatProgram: String?,
    val idPegawai: String?,
    val idSiamikMahasiswa: String?,
    val isDeleted: Boolean?,
    val isFinish: Boolean?,
    val isMagangMandiri: Boolean?,
    val link: String?,
    val status: Int?,
    val statusApproval: String?,
    val statusMbkm: String?,
    val updatedAt: String?,
    val updatedBy: String?
)