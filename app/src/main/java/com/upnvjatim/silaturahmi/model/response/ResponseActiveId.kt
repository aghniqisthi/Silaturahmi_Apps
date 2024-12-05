package com.upnvjatim.silaturahmi.model.response

data class ResponseActiveId(
    val data: List<DataActiveId?>?
)

data class DataActiveId(
    val IdPendaftar: String?,
    val idSiamikMahasiswa: String?,
    val keterangan: String?,
    val status: Int?,
    val statusMbkm: String?
)