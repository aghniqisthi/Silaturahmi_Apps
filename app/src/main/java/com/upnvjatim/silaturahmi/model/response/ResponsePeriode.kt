package com.upnvjatim.silaturahmi.model.response

data class ResponsePeriode(
    val data: List<ItemPeriode?>?
)

data class ResponseItemsPeriode(
    val data: ItemsPeriode
)

data class ItemsPeriode(
    val items: List<ItemPeriode?>?,
    val meta: Meta
)