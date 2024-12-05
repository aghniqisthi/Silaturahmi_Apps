package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponseLookupProgram
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookupProgramViewModel : ViewModel() {
    var liveDataLookupProgram : MutableLiveData<ResponseLookupProgram> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataLookupProgram(
        authHeader: String, pageSize: Int = 100, pageNumber: Int = 1, q: String?
    ) : MutableLiveData<ResponseLookupProgram> {
        callApiLookupProgram(authHeader, pageSize, pageNumber, q)
        return liveDataLookupProgram
    }


    private fun callApiLookupProgram(
        authHeader: String, pageSize: Int?, pageNumber: Int?, q: String?
    ){
        _isLoading.value = true

        ApiConfig.getApiService().lookupProgram(
            authHeader, pageSize, pageNumber, q
        ).enqueue(object : Callback<ResponseLookupProgram> {
            override fun onResponse(call: Call<ResponseLookupProgram>, response: Response<ResponseLookupProgram>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataLookupProgram.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseLookupProgram: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseLookupProgram>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure LookupProgram: ${t.message.toString()}")
            }
        })
    }

//    fun setDospem(authHeader: String, setDospem: SetDospem){
//        _isLoading.value = true
//
//        ApiConfig.getApiService().setDospem(authHeader, setDospem).enqueue(object : Callback<ResponseSetDospem> {
//            override fun onResponse(call: Call<ResponseSetDospem>, response: Response<ResponseSetDospem>) {
//                _isLoading.value = false
//
//                if (response.isSuccessful){
//                    liveDataSetDospem.postValue(response.body())
//                }
//                else {
//                    Log.e(ContentValues.TAG, "onFailureResponseSetDospem: ${response.message()}")
//                    _isError.postValue(response.message())
//                }
//            }
//            override fun onFailure(call: Call<ResponseSetDospem>, t: Throwable) {
//                _isLoading.value = false
//                _isError.postValue(t.message)
//                Log.e(ContentValues.TAG, "onFailure setDospem: ${t.message.toString()}")
//            }
//        })
//    }
}