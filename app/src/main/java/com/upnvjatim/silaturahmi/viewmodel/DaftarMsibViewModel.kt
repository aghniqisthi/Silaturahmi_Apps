package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.PostPaketKonversi
import com.upnvjatim.silaturahmi.model.response.ResponsePostPaketBulk
import com.upnvjatim.silaturahmi.model.response.ResponsePostProgram
import com.upnvjatim.silaturahmi.model.response.ResponsePutDocsPendaftaran
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class DaftarMsibViewModel : ViewModel() {

    private val _addProgram = MutableLiveData<ResponsePostProgram>()
    val addProgram: LiveData<ResponsePostProgram> = _addProgram

    private val _putFileProgram = MutableLiveData<ResponsePutDocsPendaftaran>()
    val putFileProgram: LiveData<ResponsePutDocsPendaftaran> = _putFileProgram

    private val _addPaketBulk = MutableLiveData<ResponsePostPaketBulk>()
    val addPaketBulk: LiveData<ResponsePostPaketBulk> = _addPaketBulk

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun postProgram(
        file: MultipartBody.Part,
        idSiamikMahasiswa: RequestBody,
        idMitraTerlibatProgram: RequestBody,
        statusMbkm: RequestBody?,
        context: Context
    ) {
        _isLoading.value = true

        ApiConfig.getApiService().postProgram(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", file,
            idSiamikMahasiswa = idSiamikMahasiswa,
            idMitraTerlibatProgram = idMitraTerlibatProgram,
            statusMbkm = statusMbkm
        ).enqueue(object : Callback<ResponsePostProgram> {
            override fun onResponse(call: Call<ResponsePostProgram>, response: Response<ResponsePostProgram>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _addProgram.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponsePostProgram>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Failure : "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun makeNetworkCall(
        files: File, id: String, idSiamikMahasiswa: String, idPegawai: String, status: String,
        idMitraTerlibatProgram: String, isFinish: Boolean, context: Context
    ) {
        val client = OkHttpClient()
        val boundary = "---011000010111000001101001"
        val file = File(files.path)
        val fileBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())

        val body = MultipartBody.Builder(boundary)
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, fileBody)
            .addFormDataPart("id", id)
            .addFormDataPart("idSiamikMahasiswa", idSiamikMahasiswa)
            .addFormDataPart("idPegawai", idPegawai)
            .addFormDataPart("status", status)
            .addFormDataPart("idMitraTerlibatProgram", idMitraTerlibatProgram)
            .addFormDataPart("isFinish", isFinish.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.191.136:3222/v1/magang/pendaftar-program-mbkm")
            .put(body)
            .addHeader("Content-Type", "multipart/form-data; boundary=$boundary")
            .addHeader("Authorization", "bearer ${getUser(context)?.token?.AccessToken}")
            .build()

        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                responseBody?.let {
                    val gson = Gson().fromJson(it, ResponsePutDocsPendaftaran::class.java)
                    Log.d(
                        "cek response link putprogram",
                        "${response.body} ; ${response.message} ; ${response.isSuccessful} ; ${response.code
                        } ; ${response.cacheResponse} ; ${gson.data?.link}"
                    )
                    _putFileProgram.postValue(gson)
                }
            } else {
                _isLoading.value = false
                _isError.postValue("onResponse: ${response.message}")
                Log.e(ContentValues.TAG, "onResponse: ${response.body} ; ${response.message} ; ${response.isSuccessful} ; ${response.code} ; ${response.cacheResponse}")
                Toast.makeText(context, "Response: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun putProgram(
        files: File, id: String, idSiamikMahasiswa: String, idPegawai: String, status: String,
        idMitraTerlibatProgram: String, isFinish: Boolean, context: Context
    ) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                makeNetworkCall(files, id, idSiamikMahasiswa, idPegawai, status, idMitraTerlibatProgram, isFinish, context)
            }
            Log.d("cek response putfile", "${_putFileProgram.value?.data?.link}")
        }
    }

    fun postPaketBulk(auth:String, paket: PostPaketKonversi, context: Context) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().postPaketBulk(
            authHeader = auth, matkulPaketKonversi = paket)
        client.enqueue(object : Callback<ResponsePostPaketBulk> {
            override fun onResponse(call: Call<ResponsePostPaketBulk>, response: Response<ResponsePostPaketBulk>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _addPaketBulk.postValue(response.body())

                    if (response.body()?.data != null) {
                        _addPaketBulk.postValue(response.body())
                    } else {
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        Toast.makeText(context,
                            "AddPaketKonversi Status: ${response.body()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    _isLoading.value = false
                    _isError.postValue(response.message().toString())
                }
            }

            override fun onFailure(call: Call<ResponsePostPaketBulk>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure ${t.cause}: ${t.message.toString()} ; ${t.stackTrace}")
                Toast.makeText(context, "Failure AddPaketKonversi: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


}