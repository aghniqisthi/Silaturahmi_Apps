package com.upnvjatim.silaturahmi.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.ChangeStatus
import com.upnvjatim.silaturahmi.model.response.ResponseStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeStatusViewModel(app: Application) : AndroidViewModel(app)  {

    private val _status = MutableLiveData<ResponseStatus>()
    val status: LiveData<ResponseStatus> = _status

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun changeStatus(data: ChangeStatus, context: Context) {
        _isLoading.value = true

        ApiConfig.getApiService().changeStatus(
            authHeader = "bearer ${getUser(context)?.token?.AccessToken}", data = data
        ).enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _status.postValue(response.body())
                } else {
                    _isLoading.value = false
                    _isError.postValue("${response.body()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Failure : "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}