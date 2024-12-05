package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponseActiveBySiamik
import com.upnvjatim.silaturahmi.model.response.ResponseRiwayat
import com.upnvjatim.silaturahmi.model.response.ResponseRiwayatNilai
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatViewModel : ViewModel() {
    var liveDataRiwayat : MutableLiveData<ResponseRiwayat> = MutableLiveData()
    var liveDataRiwayatNilai : MutableLiveData<ResponseRiwayatNilai> = MutableLiveData()
    var liveDataActiveBySiamik : MutableLiveData<ResponseActiveBySiamik> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataActiveBySiamik(authHeader: String, idSiamik: String) : MutableLiveData<ResponseActiveBySiamik> {
        callApiActiveBySiamik(authHeader, idSiamik)
        return liveDataActiveBySiamik
    }

    fun getliveDataRiwayat(authHeader: String, pageSize: Int, pageNumber: Int) : MutableLiveData<ResponseRiwayat> {
        callApiRiwayat(authHeader, pageSize, pageNumber)
        return liveDataRiwayat
    }

    fun getliveDataRiwayatNilai(authHeader: String, id: String, status: String?) : MutableLiveData<ResponseRiwayatNilai> {
        callApiRiwayatNilai(authHeader, id, status)
        return liveDataRiwayatNilai
    }

    private fun callApiActiveBySiamik(authHeader: String, idSiamik: String){
        _isLoading.value = true

        ApiConfig.getApiService().getActiveProgram(authHeader, idSiamik)
            .enqueue(object : Callback<ResponseActiveBySiamik> {
            override fun onResponse(call: Call<ResponseActiveBySiamik>, response: Response<ResponseActiveBySiamik>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    Log.d("cek avtive by siamik","${response.body().toString()} ${response.message()} ; ${response.raw()}")
                    liveDataActiveBySiamik.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseActiveBySiamik: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseActiveBySiamik>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure ResponseActiveBySiamik: ${t.message.toString()}")
            }
        })
    }

    private fun callApiRiwayatNilai(authHeader: String, id: String, status: String?){
        _isLoading.value = true

        ApiConfig.getApiService().getRiwayatNilai(authHeader, id, status)
            .enqueue(object : Callback<ResponseRiwayatNilai> {
            override fun onResponse(call: Call<ResponseRiwayatNilai>, response: Response<ResponseRiwayatNilai>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    Log.d("cek riwayat nilai","${response.body().toString()} ${response.message()} ; ${response.raw()}")
                    liveDataRiwayatNilai.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseRiwayatNilai: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseRiwayatNilai>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure RiwayatNilai: ${t.message.toString()}")
            }
        })
    }

    fun callApiRiwayat(authHeader: String, pageSize: Int, pageNumber: Int){
        _isLoading.value = true

        ApiConfig.getApiService().getRiwayat(authHeader, pageSize, pageNumber)
            .enqueue(object : Callback<ResponseRiwayat> {
            override fun onResponse(call: Call<ResponseRiwayat>, response: Response<ResponseRiwayat>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataRiwayat.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseRiwayat: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseRiwayat>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure Riwayat: ${t.message.toString()}")
            }
        })
    }




}