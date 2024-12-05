package com.upnvjatim.silaturahmi.model.response

data class ResponseListLuaran(
    val data: List<DataLuaran?>?
)

data class ResponsePostLuaran(
    val data: DataLuaran?
)