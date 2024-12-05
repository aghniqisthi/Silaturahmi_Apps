package com.upnvjatim.silaturahmi.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.model.response.ResponseActiveId
import com.upnvjatim.silaturahmi.model.response.ResponseItemsPeriode
import com.upnvjatim.silaturahmi.model.response.ResponsePendaftarProgram
import com.upnvjatim.silaturahmi.model.response.ResponsePendaftarProgramByIdPendaftar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendaftarProgramViewModel : ViewModel() {
//    var liveDataPendaftarProgram : MutableLiveData<ResponsePendaftarProgram> = MutableLiveData()
    var liveDataActiveProgram : MutableLiveData<ResponsePendaftarProgram> = MutableLiveData()
    var liveDataProgramById : MutableLiveData<ResponsePendaftarProgramByIdPendaftar> = MutableLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    private val _liveDataPendaftarProgram = MutableLiveData<ResponsePendaftarProgram>()
    val liveDataPendaftarProgram: LiveData<ResponsePendaftarProgram> = _liveDataPendaftarProgram

    private val _liveDataPeriodeActive = MutableLiveData<ResponseItemsPeriode>()
    val liveDataPeriodeActive: LiveData<ResponseItemsPeriode> = _liveDataPeriodeActive

//    private val _liveDataPendaftarProgram = MutableLiveData<ResponsePendaftarProgram>()
//    val liveDataPendaftarProgram: LiveData<ResponsePendaftarProgram> = _liveDataPendaftarProgram

//    fun getliveDataPendaftarProgram() : MutableLiveData<ResponsePendaftarProgram> {
//        return liveDataPendaftarProgram
//    }

//    fun getliveDataProgramById() : MutableLiveData<ResponsePendaftarProgramByIdPendaftar> {
//        return liveDataProgramById
//    }

//    fun getliveDataActiveProgram(
//        authHeader: String, idSiamikMahasiswa: String
//    ) : MutableLiveData<ResponsePendaftarProgramByIdPendaftar> {
//        callApiActiveProgram(authHeader, idSiamikMahasiswa)
//        return liveDataProgramById
//    }

    fun callApiPeriodeActive(authHeader: String){
        _isLoading.value = true
        ApiConfig.getApiService().getPeriode(authHeader, 1, 1).enqueue(
            object : Callback<ResponseItemsPeriode> {
            override fun onResponse(call: Call<ResponseItemsPeriode>, response: Response<ResponseItemsPeriode>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _liveDataPeriodeActive.postValue(response.body())
                } else {
                    Log.e(ContentValues.TAG, "onFailureItemPeriode: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseItemsPeriode>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure ItemPeriode: ${t.message.toString()}")
            }
        })
    }

    fun callApiPendaftarProgram(
        authHeader: String,
        pageSize: Int, pageNumber: Int,
        query: String?, idProgramProdi: String?, idProgram: String?, statusMbkm: String?, status: String?, sortByStatus: String?,
        sortBy: String?, sortType: String?, idJenisMbkm: String?, idPegawai: String?, idPeriode: String?
    ){
        _isLoading.value = true

        ApiConfig.getApiService().pendaftarProgram(
            authHeader, pageSize, pageNumber, query, idProgramProdi, idProgram, statusMbkm, status,
            sortByStatus, sortBy, sortType, idJenisMbkm, idPegawai, idPeriode
        ).enqueue(object : Callback<ResponsePendaftarProgram> {
            override fun onResponse(call: Call<ResponsePendaftarProgram>, response: Response<ResponsePendaftarProgram>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    _liveDataPendaftarProgram.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponsePendaftar: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponsePendaftarProgram>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PendaftarProgram: ${t.message.toString()}")
            }
        })
    }

    fun callApiActiveProgram(authHeader: String, idSiamikMahasiswa: String){
        _isLoading.value = true

        ApiConfig.getApiService().activeProgram(authHeader, idSiamikMahasiswa)
            .enqueue(object : Callback<ResponseActiveId> {
            override fun onResponse(call: Call<ResponseActiveId>, response: Response<ResponseActiveId>) {
                if (response.isSuccessful){
                    if(response.body()?.data?.first()?.IdPendaftar != null) {
                        Log.d("cek datactive", "${response.body()?.data?.first()?.IdPendaftar}")
                        callApiProgramByIdPendaftar(authHeader, response.body()?.data?.first()?.IdPendaftar.toString())
                    } else {
                        liveDataProgramById.postValue(ResponsePendaftarProgramByIdPendaftar(data = null))
                    }
//                    liveDataPendaftarProgram.postValue(response.body())
                }
                else {
                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "onFailureResponseActiveProgram: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponseActiveId>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure ActiveProgram: ${t.cause} ${t.message.toString()} ${t.stackTrace}")
            }
        })
    }

    fun callApiProgramByIdPendaftar(authHeader: String, idPendaftar: String){
        _isLoading.value = true

        ApiConfig.getApiService().pendaftarProgramByIdPendaftar(authHeader, idPendaftar)
            .enqueue(object : Callback<ResponsePendaftarProgramByIdPendaftar> {
            override fun onResponse(call: Call<ResponsePendaftarProgramByIdPendaftar>,
                                    response: Response<ResponsePendaftarProgramByIdPendaftar>) {
                _isLoading.value = false

                if (response.isSuccessful){
                    Log.d("cek ldprpgramid", response.body()?.data?.link.toString())
                    liveDataProgramById.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailureResponsePendaftarProgramByIdPendaftar: ${response.message()}")
                    _isError.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<ResponsePendaftarProgramByIdPendaftar>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure PendaftarProgramByIdPendaftar: ${t.message.toString()}")
            }
        })
    }
}