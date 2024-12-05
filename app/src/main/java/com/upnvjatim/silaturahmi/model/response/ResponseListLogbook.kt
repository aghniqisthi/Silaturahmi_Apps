package com.upnvjatim.silaturahmi.model.response

data class ResponseListLogbook(
    val data : DataLogbook?
)

data class ResponseAllListLogbook(
    val data : List<ItemLogbook>?
)