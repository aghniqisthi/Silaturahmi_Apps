package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponsePaketKonversi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaketKonversiViewModel : ViewModel() {
    var liveDataPaket : MutableLiveData<ResponsePaketKonversi> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getliveDataPaket(
        authHeader: String, idPendaftarMBKM: String, pageSize: Int? = 100, pageNumber: Int? = 1,
        idProgramProdi: String?, idJenisMbkm: String?, idProgram: String?, statusMbkm: String?
    ) : MutableLiveData<ResponsePaketKonversi> {
        callApiPendaftarProgram(
            authHeader, idPendaftarMBKM, pageSize, pageNumber, idProgramProdi,
            idJenisMbkm, idProgram, statusMbkm
        )
        return liveDataPaket
    }

    fun callApiPendaftarProgram(
        authHeader: String, idPendaftarMBKM: String, pageSize: Int?, pageNumber: Int?,
        idProgramProdi: String?, idJenisMbkm: String?, idProgram: String?, statusMbkm: String?
    ){
        _isLoading.value = true

        ApiConfig.getApiService().paketKonversi(
            authHeader, idPendaftarMBKM, pageSize, pageNumber, idProgramProdi, idJenisMbkm, idProgram, statusMbkm
        ).enqueue(object : Callback<ResponsePaketKonversi> {
            override fun onResponse(call: Call<ResponsePaketKonversi>, response: Response<ResponsePaketKonversi>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    liveDataPaket.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponsePaket: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponsePaketKonversi>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PaketKonversi: ${t.message.toString()}")
            }
        })
    }
}