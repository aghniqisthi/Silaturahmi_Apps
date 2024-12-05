package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponseLookupPaketKonversi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LookupPaketKonversiViewModel : ViewModel() {
    var liveDataLookupPaketKonversi : MutableLiveData<ResponseLookupPaketKonversi> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataLookupPaketKonversi(
        authHeader: String, pageSize: Int = 100, pageNumber: Int = 1, idProgramProdi: String, q: String?
    ) : MutableLiveData<ResponseLookupPaketKonversi> {
        callApiLookupPaketKonversi(authHeader, pageSize, pageNumber, idProgramProdi, q)
        return liveDataLookupPaketKonversi
    }

    private fun callApiLookupPaketKonversi(
        authHeader: String, pageSize: Int?, pageNumber: Int?, idProgramProdi: String, q: String?
    ){
        _isLoading.value = true

        ApiConfig.getApiService().lookupPaketKonversi(
            authHeader, pageSize, pageNumber, idProgramProdi, q
        ).enqueue(object : Callback<ResponseLookupPaketKonversi> {
            override fun onResponse(call: Call<ResponseLookupPaketKonversi>, response: Response<ResponseLookupPaketKonversi>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataLookupPaketKonversi.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponseLookupPaketKonversi: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseLookupPaketKonversi>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure LookupPaketKonversi: ${t.message.toString()}")
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