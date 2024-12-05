package com.upnvjatim.silaturahmi.model.response

data class Meta(
    val currentPage: Int?,
    val limitPerPage: Int?,
    val nextPage: Int?,
    val previousPage: Int?,
    val totalItems: Int?,
    val totalPage: Int?
)