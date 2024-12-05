package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.request.SetDospem
import com.upnvjatim.silaturahmi.model.response.ResponseLookupDospem
import com.upnvjatim.silaturahmi.model.response.ResponseSetDospem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookupDospemViewModel : ViewModel() {
    var liveDataLookupDospem : MutableLiveData<ResponseLookupDospem> = MutableLiveData()
    var liveDataSetDospem : MutableLiveData<ResponseSetDospem> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataLookupDospem(
        authHeader: String, pageSize: Int = 100, pageNumber: Int = 1, idProgramProdi: String
    ) : MutableLiveData<ResponseLookupDospem> {
        callApiLookupDospem(authHeader, pageSize, pageNumber, idProgramProdi)
        return liveDataLookupDospem
    }


    fun callApiLookupDospem(
        authHeader: String, pageSize: Int?, pageNumber: Int?, idProgramProdi: String
    ){
        _isLoading.value = true

        ApiConfig.getApiService().lookupDospem(
            authHeader, pageSize, pageNumber, idProgramProdi
        ).enqueue(object : Callback<ResponseLookupDospem> {
            override fun onResponse(call: Call<ResponseLookupDospem>, response: Response<ResponseLookupDospem>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataLookupDospem.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseLookupdospem: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseLookupDospem>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure LookupDospem: ${t.message.toString()}")
            }
        })
    }

    fun setDospem(authHeader: String, setDospem: SetDospem){
        _isLoading.value = true

        ApiConfig.getApiService().setDospem(authHeader, setDospem).enqueue(object : Callback<ResponseSetDospem> {
            override fun onResponse(call: Call<ResponseSetDospem>, response: Response<ResponseSetDospem>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataSetDospem.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseSetDospem: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseSetDospem>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure setDospem: ${t.message.toString()}")
            }
        })
    }
}