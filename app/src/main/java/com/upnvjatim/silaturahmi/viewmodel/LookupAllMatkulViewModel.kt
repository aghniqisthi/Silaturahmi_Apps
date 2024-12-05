package com.upnvjatim.silaturahmi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk.Injection
import com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk.ListMkRepository
import com.upnvjatim.silaturahmi.model.response.ItemAllMatkul
import kotlinx.coroutines.flow.Flow

class LookupAllMatkulViewModel(private val listMkRepository: ListMkRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun dataMk(token: String, idProgramProdi: String, q: String?): LiveData<PagingData<ItemAllMatkul>> =
        listMkRepository.getMatkul(token, idProgramProdi, q).cachedIn(viewModelScope)
}

//    var liveDataAllMatkul : MutableLiveData<ResponseAllMatkul> = MutableLiveData()

//    fun getliveDataAllMatkul(
//        authHeader: String, pageSize: Int, pageNumber: Int, idProgramProdi: String, q: String?
//    ) : MutableLiveData<ResponseAllMatkul> {
//        callApiAllMatkul(authHeader, pageSize, pageNumber, idProgramProdi, q)
//        return liveDataAllMatkul
//    }


//    private fun callApiAllMatkul(
//        authHeader: String, pageSize: Int, pageNumber: Int, idProgramProdi: String, q: String?
//    ){
//        _isLoading.value = true
//
//        ApiConfig.getApiService().getAllMatkul(authHeader, pageSize, pageNumber, idProgramProdi, q)
//            .enqueue(object : Callback<ResponseAllMatkul> {
//            override fun onResponse(call: Call<ResponseAllMatkul>, response: Response<ResponseAllMatkul>) {
//                _isLoading.value = false
//
//                if (response.isSuccessful){
//                    liveDataAllMatkul.postValue(response.body())
//                }
//                else {
//                    Log.e(ContentValues.TAG, "onFailureResponseAllMatkul: ${response.message()}")
//                    _isError.postValue(response.message())
//                }
//            }
//            override fun onFailure(call: Call<ResponseAllMatkul>, t: Throwable) {
//                _isLoading.value = false
//                _isError.postValue(t.message)
//                Log.e(ContentValues.TAG, "onFailure AllMatkul: ${t.message.toString()}")
//            }
//        })
//    }

class LookupMkViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LookupAllMatkulViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LookupAllMatkulViewModel(Injection.provideRepository()) //, token, idProgramProdi, q)
                    as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}