package com.upnvjatim.silaturahmi

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.upnvjatim.silaturahmi.model.request.DataNotif
import com.upnvjatim.silaturahmi.model.request.Notif
import com.upnvjatim.silaturahmi.model.response.DataLogin
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.model.response.ResponseValidasiLogin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

const val DATAUSER = "dataUser"
const val IDPENDAFTARPROGRAM = "idPendaftar"
const val DETAILUSER = "detailUser"
const val DATAPASS = "dataPass"
const val LISTROLE = "listRole"

fun saveUser(user: DataLogin?, role: ResponseValidasiLogin?, pass: String, context: Context) {
    val sharedPref = context.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)

    val addUser = sharedPref.edit()
    addUser.putString(DETAILUSER, Gson().toJson(user))
    addUser.putString(DATAPASS, pass)
    if(role != null) addUser.putString(LISTROLE, Gson().toJson(role))
    addUser.apply()
    Log.d("cek string", "${sharedPref.getString(DATAPASS, "").toString()} ")
}

fun saveIdPendaftar(datas: ItemPendaftarProgram?, context: Context) {
    val sharedPref = context.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)

    val addUser = sharedPref.edit()
    addUser.putString(IDPENDAFTARPROGRAM, Gson().toJson(datas))
    addUser.apply()
    Log.d("cek string", "${sharedPref.getString(IDPENDAFTARPROGRAM, "").toString()} ")
}


fun getUser(context: Context): DataLogin? {
    val sharedPref = context.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
    val data = sharedPref.getString(DETAILUSER, null)
    if (data == null) {
        return null
    }
    return Gson().fromJson(data, DataLogin::class.java)
}

fun getListRole(context: Context): ResponseValidasiLogin? {
    val sharedPref = context.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
    val data = sharedPref.getString(LISTROLE, null)
    if (data == null) {
        return null
    }
    return Gson().fromJson(data, ResponseValidasiLogin::class.java)
}

fun getIdPendaftar(context: Context): ItemPendaftarProgram? {
    val sharedPref = context.getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
    val data = sharedPref.getString(IDPENDAFTARPROGRAM, null)

    return if (data != null) {
        Gson().fromJson(data, ItemPendaftarProgram::class.java) // Convert JSON string back to object
    } else {
        null // Return null if no data is found
    }
}


fun fakultas(kode: String): String {
    return when (kode) {
        "041", "042", "043", "044", "045", "046", "047" -> "Fakultas Ilmu Sosial dan Ilmu Politik"
        "011", "012", "013", "014" -> "Fakultas Ekonomi dan Bisnis"
        "091" -> "Fakultas Kedokteran"
        "071" -> "Fakultas Hukum"
        "024", "025" -> "Fakultas Pertanian"
        "081", "082", "083", "084" -> "Fakultas Ilmu Komputer"
        "051", "052", "053" -> "Fakultas Arsitektur dan Desain"
        "031", "032", "033", "034", "035", "036", "037" -> "Fakultas Teknik"
        else -> "-"
    }
}

fun semester(smst: String): String {
    if ((smst.toIntOrNull() ?: 0) % 2 == 1) return "Genap"
    else return "Gasal"
}

//fun getFileName(uri: Uri, context: Context): String? {
//    var displayName: String? = null
//
//    if (uri.toString().startsWith("content://")) {
//        Log.d("cek uri content", uri.toString())
//        var cursor: Cursor? = null
//        try {
//            cursor = context.contentResolver.query(uri, null, null, null, null)
//            if (cursor != null && cursor.moveToFirst()) {
//                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                if (nameIndex >= 0) {
//                    displayName = cursor.getString(nameIndex)
//                    Log.d("cek display name gfn", displayName)
//                }
//            }
//        } finally {
//            cursor?.close()
//        }
//    } else if (uri.toString().startsWith("file://")) {
//        displayName = File(uri.toString()).name
//        Log.d("cek display name file", displayName)
//    }
//
//    // Fallback if displayName is still null, extract from URI path
//    if (displayName == null) {
//        displayName = uri.path?.let {
//            val cut = it.lastIndexOf('/')
//            if (cut != -1) {
//                it.substring(cut + 1)
//            } else it
//        }
//        Log.d("cek display name null", displayName.toString())
//    }
//
//    Log.d("cek dispname utils", displayName ?: "Unknown file name")
//    return displayName
//}

fun openFile(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    val link = Uri.parse("https://silaturahmi.upnjatim.ac.id/api/files?path=$url")
    intent.setDataAndType(link, "application/pdf")
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
        Log.e("error start link daftar", "${e.message} ; ${e.cause} ${e.stackTrace}")
    }
}

fun statusKegiatan(status: Int, finish: Boolean): String {
    return when (status) {
        0 -> "Pendaftaran" //1
        1 -> "Revisi Penilaian" //6
        2 -> "Sudah Verifikasi Paket Konversi" //3
        3 -> {
            if (finish) "Sudah Verifikasi Penilai Konversi" //5
            else "Sudah Verifikasi Nilai oleh Pembimbing" //4
        }
        4 -> "Verifikasi Pendaftaran" //2
        else -> "Tidak Ada Status"
    }
}

fun saveNotif(username:String, title: String, message: String, status: String, isFinish: Boolean, context: Context) {
    val data = hashMapOf("title" to title, "message" to message, "seen" to false, "status" to status, "isFinish" to isFinish)

    val docRef = FirebaseFirestore.getInstance()
        .collection("notification")
        .document(username)

    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val notifArray = document.get("notif") as? MutableList<Map<String, Any>> ?: mutableListOf()
                notifArray.add(data)  // Add the new data, even if it's a duplicate

                // Update Firestore with the new array
                docRef.update("notif", notifArray)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error savebnotif", "${e.message} ${e.cause} ${e.stackTrace}")
                    }
            }
        }
}

fun createNewNotif(
    username:String, title: String, message: String, status: String, isFinish: Boolean, context: Context){
    FirebaseFirestore.getInstance()
        .collection("notification")
        .document(username)
        .set(
            Notif(
                username = getUser(context)?.user?.username.toString(),
                status = status,
                isFinish = isFinish,
                notif = listOf(DataNotif(title = title, message = message, seen = false))
            )
        ).addOnSuccessListener {
            Log.d(
                "cek Firestore",
                "Document ID successfully set with idChat: ${getUser(context)?.user?.username}"
            )
            Toast.makeText(context, "Add new notif", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            Log.e("error new notif", "${it.message} ${it.cause} ${it.stackTrace}")
        }
}


fun getFileName(uri: Uri, context: Context): String {
    var fileName = ""

    // Check if the URI scheme is "content"
    if (uri.scheme == "content") {
        // Query the content resolver for the display name
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
    }

    // If the file name wasn't found, fall back to extracting it from the URI path
    if (fileName.isEmpty()) {
        fileName = uri.path?.substringAfterLast('/').toString()
    }

    return fileName
}
