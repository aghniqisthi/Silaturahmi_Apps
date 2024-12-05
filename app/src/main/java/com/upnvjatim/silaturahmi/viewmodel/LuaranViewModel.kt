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
import com.upnvjatim.silaturahmi.model.response.ResponseListLuaran
import com.upnvjatim.silaturahmi.model.response.ResponsePostLuaran
import com.upnvjatim.silaturahmi.model.response.ResponseStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LuaranViewModel : ViewModel() {
    var liveDataAllLuaran : MutableLiveData<ResponseListLuaran> = MutableLiveData()
    var liveDataPostLuaran : MutableLiveData<ResponsePostLuaran> = MutableLiveData()
    var liveDataDeleteLuaran : MutableLiveData<ResponseStatus> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataAllLuaran(authHeader: String, id:String) : MutableLiveData<ResponseListLuaran> {
        callApiListLuaran(authHeader, id)
        return liveDataAllLuaran
    }

    fun callApiListLuaran(authHeader: String, id:String){
        _isLoading.value = true

        ApiConfig.getApiService().getAllLuaran(authHeader, id).enqueue(object : Callback<ResponseListLuaran> {
            override fun onResponse(call: Call<ResponseListLuaran>, response: Response<ResponseListLuaran>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataAllLuaran.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseListLuaran: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseListLuaran>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure ListLuaran: ${t.message.toString()}")
            }
        })
    }

    fun postLuaran(
        file: MultipartBody.Part, idJenisLuaran : RequestBody, idPendaftarProgramMbkm : RequestBody,
        tanggal : RequestBody, keterangan : RequestBody, context: Context
    ) {
        _isLoading.value = true

        ApiConfig.getApiService().postLuaran(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}",
            file = file,
            idJenisLuaran = idJenisLuaran,
            idPendaftarProgramMbkm = idPendaftarProgramMbkm,
            tanggal = tanggal,
            keterangan = keterangan,
        ).enqueue(object : Callback<ResponsePostLuaran> {
            override fun onResponse(call: Call<ResponsePostLuaran>, response: Response<ResponsePostLuaran>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPostLuaran.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponsePostLuaran>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PostLuaran: ${t.message.toString()}")
                Toast.makeText(context, "Failure PostLuaran: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun putLuaran(
        file: MultipartBody.Part?, id: RequestBody, idJenisLuaran : RequestBody,
        idPendaftarProgramMbkm : RequestBody, tanggal : RequestBody, keterangan : RequestBody, context: Context
    ) {
        _isLoading.value = true

        ApiConfig.getApiService().putLuaran(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", file,
            id = id,
            idJenisLuaran = idJenisLuaran,
            idPendaftarProgramMbkm = idPendaftarProgramMbkm,
            tanggal = tanggal,
            keterangan = keterangan
        ).enqueue(object : Callback<ResponsePostLuaran> {
            override fun onResponse(call: Call<ResponsePostLuaran>, response: Response<ResponsePostLuaran>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataPostLuaran.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponsePostLuaran>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure EditLuaran: ${t.message.toString()}")
                Toast.makeText(context, "Failure EditLuaran: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteLuaran(id: String, context: Context) {
        _isLoading.value = true

        ApiConfig.getApiService().deleteLuaran(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", id = id
        ).enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    liveDataDeleteLuaran.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure DeleteLuaran: ${t.message.toString()}")
                Toast.makeText(context, "Failure DeleteLuaran: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


}