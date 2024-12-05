package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponsePeriode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PeriodeViewModel : ViewModel() {
    var liveDataPeriode : MutableLiveData<ResponsePeriode> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataPeriode(authHeader: String) : MutableLiveData<ResponsePeriode> {
        callApiPeriode(authHeader)
        return liveDataPeriode
    }

    fun callApiPeriode(authHeader: String){
        _isLoading.value = true

        ApiConfig.getApiService().getAllPeriode(authHeader).enqueue(object : Callback<ResponsePeriode> {
            override fun onResponse(call: Call<ResponsePeriode>, response: Response<ResponsePeriode>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataPeriode.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponsePeriode: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponsePeriode>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure Periode: ${t.message.toString()}")
            }
        })
    }
}