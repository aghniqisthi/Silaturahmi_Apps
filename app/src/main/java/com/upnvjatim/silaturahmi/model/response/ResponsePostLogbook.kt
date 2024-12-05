package com.upnvjatim.silaturahmi.model.response

data class ResponsePostLogbook(
    val data: ItemPostLogbook
)

data class ItemPostLogbook(
    val id: String,
    val tanggal: String,
    val link: String,
    val deskripsi: String,
    val idPendaftar: String,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: String,
    val updatedBy: String,
    val deletedAt: String?,
    val deletedBy: String?,
)

