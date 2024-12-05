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
import com.upnvjatim.silaturahmi.model.request.PostLogbook
import com.upnvjatim.silaturahmi.model.response.ResponseAllListLogbook
import com.upnvjatim.silaturahmi.model.response.ResponseDeleteDocs
import com.upnvjatim.silaturahmi.model.response.ResponseDocsLogbook
import com.upnvjatim.silaturahmi.model.response.ResponseListLogbook
import com.upnvjatim.silaturahmi.model.response.ResponsePostLogbook
import com.upnvjatim.silaturahmi.model.response.ResponseStatus
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LogbookViewModel : ViewModel() {
    var liveDataDocsLogbook: MutableLiveData<ResponseDocsLogbook> = MutableLiveData()
    var liveDataListLogbook: MutableLiveData<ResponseListLogbook> = MutableLiveData()
    var liveDataAllListLogbook: MutableLiveData<ResponseAllListLogbook> = MutableLiveData()

    var liveDataPostDataLogbook: MutableLiveData<ResponsePostLogbook> = MutableLiveData()

    var liveDataPutDataLogbook: MutableLiveData<ResponsePostLogbook> = MutableLiveData()
    var liveDataDeleteDataLogbook: MutableLiveData<ResponseStatus> = MutableLiveData()

    private val _liveDataPostDocsLogbook = MutableLiveData<ResponseStatus>()
    val liveDataPostDocsLogbook: LiveData<ResponseStatus> = _liveDataPostDocsLogbook

    private val _liveDataDeleteDocsLogbook = MutableLiveData<ResponseDeleteDocs>()
    val liveDataDeleteDocsLogbook: LiveData<ResponseDeleteDocs> = _liveDataDeleteDocsLogbook

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataDocsLogbook(authHeader: String, idLogbook: String): MutableLiveData<ResponseDocsLogbook> {
        callApiDocsLogbook(authHeader, idLogbook)
        return liveDataDocsLogbook
    }

    fun getliveDataAllListLogbook(authHeader: String, idPendaftar: String): MutableLiveData<ResponseAllListLogbook> {
        callApiAllListLogbook(authHeader, idPendaftar)
        return liveDataAllListLogbook
    }

    fun getliveDataListLogbook(
        authHeader: String, pageSize: Int, pageNumber: Int, idPendaftar: String, q: String?
    ): MutableLiveData<ResponseListLogbook> {
        callApiListLogbook(authHeader, pageSize, pageNumber, idPendaftar, q)
        return liveDataListLogbook
    }

    fun liveDataPostDataLogbook(authHeader: String, logbookMagang: PostLogbook)
    : MutableLiveData<ResponsePostLogbook> {
        postDataLogbook(authHeader, logbookMagang)
        return liveDataPostDataLogbook
    }

    fun liveDataPutDataLogbook(authHeader: String, id: String, logbookMagang: PostLogbook)
    : MutableLiveData<ResponsePostLogbook> {
        putDataLogbook(authHeader, id, logbookMagang)
        return liveDataPutDataLogbook
    }

    fun liveDataDeleteDataLogbook(authHeader: String, idLogbook: String): MutableLiveData<ResponseStatus> {
        deleteDataLogbook(authHeader, idLogbook)
        return liveDataDeleteDataLogbook
    }

    private fun deleteDataLogbook(authHeader: String, idLogbook: String) {
        _isLoading.value = true

        ApiConfig.getApiService().deleteLogbook(authHeader, idLogbook)
            .enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        liveDataDeleteDataLogbook.postValue(response.body())
                    } else {
                        Log.e(
                            ContentValues.TAG,
                            "onFailureDeleteLogbook: ${response.message()}"
                        )
                        _isError.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    _isLoading.value = false
                    _isError.postValue(t.message)
                    Log.e(ContentValues.TAG, "onFailure DeleteLogbook: ${t.message.toString()}")
                }
            })
    }

    private fun callApiDocsLogbook(authHeader: String, idLogbook: String) {
        _isLoading.value = true

        ApiConfig.getApiService().getDocsLogbook(authHeader, idLogbook)
            .enqueue(object : Callback<ResponseDocsLogbook> {
                override fun onResponse(
                    call: Call<ResponseDocsLogbook>,
                    response: Response<ResponseDocsLogbook>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        liveDataDocsLogbook.postValue(response.body())
                    } else {
                        Log.e(
                            ContentValues.TAG,
                            "onFailureResponseDocsLogbook: ${response.message()}"
                        )
                        _isError.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseDocsLogbook>, t: Throwable) {
                    _isLoading.value = false
                    _isError.postValue(t.message)
                    Log.e(ContentValues.TAG, "onFailure DocsLogbook: ${t.message.toString()}")
                }
            })
    }

    private fun callApiListLogbook(
        authHeader: String, pageSize: Int, pageNumber: Int, idPendaftar: String, q: String?
    ) {
        _isLoading.value = true

        ApiConfig.getApiService()
            .getListLogbook(authHeader, pageSize, pageNumber, idPendaftar, "desc", q)
            .enqueue(object : Callback<ResponseListLogbook> {
                override fun onResponse(
                    call: Call<ResponseListLogbook>,
                    response: Response<ResponseListLogbook>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        liveDataListLogbook.postValue(response.body())
                    } else {
                        Log.e(
                            ContentValues.TAG,
                            "onFailureResponseListLogbook: ${response.message()}"
                        )
                        _isError.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseListLogbook>, t: Throwable) {
                    _isLoading.value = false
                    _isError.postValue(t.message)
                    Log.e(ContentValues.TAG, "onFailure ListLogbook: ${t.message.toString()}")
                }
            })
    }

    private fun callApiAllListLogbook(authHeader: String, idPendaftar: String) {
        _isLoading.value = true

        ApiConfig.getApiService().getAllListLogbook(authHeader, idPendaftar)
            .enqueue(object : Callback<ResponseAllListLogbook> {
                override fun onResponse(
                    call: Call<ResponseAllListLogbook>,
                    response: Response<ResponseAllListLogbook>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        liveDataAllListLogbook.postValue(response.body())
                    } else {
                        Log.e(
                            ContentValues.TAG,
                            "onFailureResponseAllListLogbook: ${response.message()}"
                        )
                        _isError.postValue(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseAllListLogbook>, t: Throwable) {
                    _isLoading.value = false
                    _isError.postValue(t.message)
                    Log.e(ContentValues.TAG, "onFailure AllListLogbook: ${t.message.toString()}")
                }
            })
    }

    private fun postDataLogbook(authHeader: String, logbookMagang: PostLogbook) {
        _isLoading.value = true

        ApiConfig.getApiService().postLogbook(authHeader,logbookMagang)
            .enqueue(object : Callback<ResponsePostLogbook> {

            override fun onResponse(call: Call<ResponsePostLogbook>, response: Response<ResponsePostLogbook>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPostDataLogbook.postValue(response.body())
                } else {
                    Log.e(ContentValues.TAG, "onFailureResponsePostLogbook: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }

            override fun onFailure(call: Call<ResponsePostLogbook>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PostLogbook: ${t.message.toString()}")
            }
        })
    }

    private fun putDataLogbook(authHeader: String, id: String, logbookMagang: PostLogbook) {
        _isLoading.value = true

        ApiConfig.getApiService().putLogbook(authHeader,id, logbookMagang)
            .enqueue(object : Callback<ResponsePostLogbook> {

            override fun onResponse(call: Call<ResponsePostLogbook>, response: Response<ResponsePostLogbook>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPutDataLogbook.postValue(response.body())
                } else {
                    Log.e(ContentValues.TAG, "onFailureResponsePutLogbook: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }

            override fun onFailure(call: Call<ResponsePostLogbook>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PutLogbook: ${t.message.toString()}")
            }
        })
    }

    fun postDocsLogbook(file: MultipartBody.Part, id: RequestBody, nama_dokumen: RequestBody, context: Context) {
        _isLoading.value = true

        ApiConfig.getApiService().postDocsLogbook(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}",
            id = id, nama_dokumen = nama_dokumen, file = file
        ).enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                _isLoading.value = false

                val buffer = Buffer()
                nama_dokumen.writeTo(buffer)

                Log.d("cek vm - 3", "${buffer.readUtf8()} ; ${response.body()} ; ${response.raw()}")
                if (response.isSuccessful) {
                    _liveDataPostDocsLogbook.value = response.body()
                } else {
//                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure postDocsLogbook: ${t.message.toString()}")
                Toast.makeText(context, "Failure postDocsLogbook: " + t.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun deleteDocsLogbook(lokasi: String, context: Context) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                makeNetworkCall(lokasi, context)
            }
        }

//        ApiConfig.getApiService().deleteDocsLogbook(
//            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", lokasi = lokasi
//        ).enqueue(object : Callback<ResponseDeleteDocs> {
//            override fun onResponse(call: Call<ResponseDeleteDocs>, response: Response<ResponseDeleteDocs>) {
//                _isLoading.value = false
//
//                if (response.isSuccessful) {
//                    _liveDataDeleteDocsLogbook.value = response.body()
//                } else {
//                    _isLoading.value = false
//                    _isError.postValue("${response.body()} : ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseDeleteDocs>, t: Throwable) {
//                _isLoading.value = false
//                _isError.postValue(t.message)
//                Log.e(ContentValues.TAG, "onFailure DeleteDocs: ${t.message.toString()}")
//                Toast.makeText(context, "Failure DeleteDocs: " + t.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    fun makeNetworkCall(lokasi: String, context: Context) {
        val client = OkHttpClient()
        val boundary = "---011000010111000001101001"
        val body = MultipartBody.Builder(boundary)
            .setType(MultipartBody.FORM)
            .addFormDataPart("lokasi", lokasi)
            .build()

        val request = Request.Builder()
            .url("http://192.168.191.136:3222/v1/magang/logbook-magang/dokumen/delete")
            .post(body)
            .addHeader("Content-Type", "multipart/form-data; boundary=$boundary")
            .addHeader("Authorization", "bearer ${getUser(context)?.token?.AccessToken}")
            .build()

        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                Log.d("cek response", responseBody.toString())
                responseBody?.let {
                    Log.d(
                        "cek response deletedocs lgbk",
                        "${response.body} ; ${response.message} ; ${response.isSuccessful} ; ${response.code
                        } ; ${response.cacheResponse} ; ${it}"
                    )
                    val gson = Gson().fromJson(it, ResponseDeleteDocs::class.java)
                    _liveDataDeleteDocsLogbook.postValue(gson)
                }
                _isLoading.postValue(false)
            } else {
                _isLoading.postValue(false)
                _isError.postValue("onResponse ResponseDeleteDocs: ${response.message}")
                Log.e(ContentValues.TAG, "onResponse ResponseDeleteDocs: ${response.body} ; ${response.message} ; ${response.isSuccessful} ; ${response.code} ; ${response.cacheResponse}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _isLoading.postValue(false)
            _isError.postValue("Failed ${e.cause}: ${e.message}")
        }
    }

}