package com.upnvjatim.silaturahmi.model.response

data class DataLogin(
    val token: Token?,
    val user: UserLogin?
)

data class Token(
    val AccessToken: String?
)

data class UserLogin(
    val email: String?,
    val firebaseToken: Any?,
    val id: String?,
    val idJenisMbkm: Any?,
    val idMhsLuarUpn: Any?,
    val idMitra: Any?,
    val idPegawai: Any?,
    val idProgram: Any?,
    val idProgramProdi: String?,
    val idSiamikMahasiswa: String?,
    val namaProdi: String?,
    val name: String?,
    val nik: String?,
    val nip: String?,
    val prodi: ProdiLogin?,
    val role: RoleLogin?,
    val username: String?
)

data class RoleLogin(
    val id: String?,
    val isPegawai: Boolean?,
    val name: String?,
    val urut: Int?
)


data class ProdiLogin(
    val id: String?,
    val kdFakjur: String?,
    val kdProdi: String?,
    val maxKrs: Int?,
    val maxSks: Int?,
    val namaProdi: String?
)