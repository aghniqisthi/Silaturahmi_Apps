package com.upnvjatim.silaturahmi.model.request

import java.io.File

data class PostDocsLogbook(
    var idPendaftar:String,
    var nama_dokumen:String,
    var file: File,
)

data class PostLogbook(
    var deskripsi:String,
    var idPendaftar:String,
    var jam: String,
    var link: String,
    var listDokumen: List<PostDataLogbook>,
    var tanggal: String
)

data class PostDataLogbook(
    var fileDokumen:String,
    var idLogbookMagang:String?,
    var namaDokumen: String,
)
