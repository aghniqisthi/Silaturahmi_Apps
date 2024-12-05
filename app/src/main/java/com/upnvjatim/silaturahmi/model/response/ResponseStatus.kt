package com.upnvjatim.silaturahmi.model.response

data class ResponseStatus(
    val data: String
)

data class ResponseDeleteDocs(
    val data: DataDeleteDocs
)

data class DataDeleteDocs(
    val message: String,
    val path: Path?,
    val success: Boolean
)

data class Path(
    val Op : String,
    val Path: String,
    val Err: Int
)