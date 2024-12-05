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
import com.upnvjatim.silaturahmi.model.request.EditNilaiMk
import com.upnvjatim.silaturahmi.model.request.PostNilaiFinal
import com.upnvjatim.silaturahmi.model.response.ResponseEditNilai
import com.upnvjatim.silaturahmi.model.response.ResponsePaketKonversi
import com.upnvjatim.silaturahmi.model.response.ResponsePostNilai
import com.upnvjatim.silaturahmi.model.response.ResponseSimpanNilaiFinal
import com.upnvjatim.silaturahmi.model.response.ResponseTranskrip
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NilaiViewModel : ViewModel() {
    var liveDataTranskrip : MutableLiveData<ResponseTranskrip> = MutableLiveData()
    var liveDataNilaiFinal : MutableLiveData<ResponsePaketKonversi> = MutableLiveData()
    var liveDataPutNilai : MutableLiveData<ResponsePostNilai> = MutableLiveData()
    var liveDataEditNilai : MutableLiveData<ResponseEditNilai> = MutableLiveData()
    var liveDataPostNilaiFinal : MutableLiveData<ResponseSimpanNilaiFinal> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataTranskrip(authHeader: String, npm: String) : MutableLiveData<ResponseTranskrip> {
        callApiTranskrip(authHeader, npm)
        return liveDataTranskrip
    }

    fun getliveNilaiFinal(authHeader: String, idPendaftar: String) : MutableLiveData<ResponsePaketKonversi> {
        callApiNilaiFinal(authHeader, idPendaftar)
        return liveDataNilaiFinal
    }

    private fun callApiTranskrip(authHeader: String, npm: String){
        _isLoading.value = true

        ApiConfig.getApiService().getTranskrip(authHeader, npm)
            .enqueue(object : Callback<ResponseTranskrip> {
            override fun onResponse(call: Call<ResponseTranskrip>, response: Response<ResponseTranskrip>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataTranskrip.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseTranskrip: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseTranskrip>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure RespTranskrip: ${t.message.toString()}")
            }
        })
    }

    private fun callApiNilaiFinal(authHeader: String, idPendaftar: String){
        _isLoading.value = true

        ApiConfig.getApiService().getNilaiFinal(authHeader, idPendaftar)
            .enqueue(object : Callback<ResponsePaketKonversi> {
            override fun onResponse(call: Call<ResponsePaketKonversi>, response: Response<ResponsePaketKonversi>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataNilaiFinal.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponsePaketKonversi: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponsePaketKonversi>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure RespPaketKonversi: ${t.message.toString()}")
            }
        })
    }

    fun putNilai(file: MultipartBody.Part, idPendaftar : String, ketPenilaian : RequestBody,
        tglPenilaian : RequestBody, nilai : RequestBody, context: Context
    ) {
        _isLoading.value = true

        ApiConfig.getApiService().putNilai(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", idPendaftar = idPendaftar,
            ketPenilaian = ketPenilaian, tglPenilaian = tglPenilaian, nilai = nilai, file
        ).enqueue(object : Callback<ResponsePostNilai> {
            override fun onResponse(call: Call<ResponsePostNilai>, response: Response<ResponsePostNilai>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPutNilai.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponsePostNilai>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Failure : "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun editNilai(data: EditNilaiMk, context: Context) {
        _isLoading.value = true

        ApiConfig.getApiService().editNilaiMk(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", data
        ).enqueue(object : Callback<ResponseEditNilai> {
            override fun onResponse(call: Call<ResponseEditNilai>, response: Response<ResponseEditNilai>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataEditNilai.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseEditNilai>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Failure : "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun postNilaiFinal(data: PostNilaiFinal, context: Context) {
        _isLoading.value = true

        ApiConfig.getApiService().postNilaiFinal(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", data
        ).enqueue(object : Callback<ResponseSimpanNilaiFinal> {
            override fun onResponse(call: Call<ResponseSimpanNilaiFinal>, response: Response<ResponseSimpanNilaiFinal>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPostNilaiFinal.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseSimpanNilaiFinal>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Failure : "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}