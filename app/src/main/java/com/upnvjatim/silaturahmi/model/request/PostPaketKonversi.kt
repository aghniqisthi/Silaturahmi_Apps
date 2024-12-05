package com.upnvjatim.silaturahmi.model.request

data class PostPaketKonversi(
    var deskripsi:String,
    var idPaketKonversi:String?,
    var idPendaftarMbkm:String,
    var idProgram:String,
    var idProgramProdi:String,
    var idRole:String,
    var listMatkul:List<ListMatkul>,
    var namaPaketKonversi:String
)

data class ListMatkul(
    var id:String?,
    var idMatkul:String,
    var idPendaftarMbkm:String,
    var isMahasiswa:Boolean,
    var nilaiAngka:String?,
    var nilaiHuruf:String?,
    var sks:String
)

data class PostPaketKonversiOnly(
    var id:String,
    var deskripsi:String,
    var idProgram:String,
    var idProgramProdi:String,
    var idRole:String,
    var namaPaketKonversi:String
)
