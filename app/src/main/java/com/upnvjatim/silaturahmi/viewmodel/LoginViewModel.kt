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
import com.upnvjatim.silaturahmi.model.request.Login
import com.upnvjatim.silaturahmi.model.request.ValidasiLogin
import com.upnvjatim.silaturahmi.model.response.ResponseLogin
import com.upnvjatim.silaturahmi.model.response.ResponseValidasiLogin
import com.upnvjatim.silaturahmi.saveUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(app: Application) : AndroidViewModel(app)  {

    private val _validasiLogin = MutableLiveData<ResponseValidasiLogin>()
    val validasiLogin: LiveData<ResponseValidasiLogin> = _validasiLogin

    private val _login = MutableLiveData<ResponseLogin>()
    val login: LiveData<ResponseLogin> = _login

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun getValidasiLoginResponse(npm:String, pass:String, context: Context) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().validasiLogin(validasiLogin = ValidasiLogin(npm, pass))
        client.enqueue(object : Callback<ResponseValidasiLogin> {
            override fun onResponse(call: Call<ResponseValidasiLogin>, response: Response<ResponseValidasiLogin>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _validasiLogin.postValue(response.body())
                    if (response.body()?.data != null && response.body()?.data?.size != 0) {
                        saveUser(null, response.body()!!, pass, context)
                        val nameRole = response.body()!!.data?.firstOrNull()?.name
                        Log.d("cek namerole", "${response.body()!!.data?.firstOrNull()?.name} ; ${response.body()!!.data?.firstOrNull()?.name}")
                        if(nameRole == "MAHASISWA")
                            getLoginResponse(npm, pass, response.body()?.data?.firstOrNull()?.idRole?:"", context)
                    } else {
                        Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        Toast.makeText(
                            context,
                            "Validasi Login Status: ${response.body()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    _isLoading.value = false
                    _isError.postValue(response.message().toString())
                }
            }

            override fun onFailure(call: Call<ResponseValidasiLogin>, t: Throwable) {
                _isLoading.value = false
                _isError.postValue(t.message)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Validasi Login Status: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getLoginResponse(npm:String, pass:String, roleId: String, context: Context) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().login(login = Login(roleId, npm, pass))
        Log.d("hit vm", client.isExecuted.toString())
        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _login.postValue(response.body())
                }
                else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                    Toast.makeText(context, "Login Status: "+response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                _isLoading.value = false
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
                Toast.makeText(context, "Login Status: "+t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

//    fun callLogin(login: Login){
//        RetrofitClientUser.instance.getAllUser().enqueue(object : Callback<List<ResponseDataUserItem>> {
//            override fun onResponse(call: Call<List<ResponseDataUserItem>>, response: Response<List<ResponseDataUserItem>>) {
//                var data = false
//                if(response.isSuccessful){
//                    if(response.body() != null){
//                        val respon = response.body()
//                        for (i in 0 until respon!!.size){
//                            if(respon[i].username == username && respon[i].password == password){
//                                data = true
//
//                                //add ke sharedpref
//                                val addUser = sharedpref.edit()
//                                addUser.putString("id", respon[i].id)
//                                addUser.putString("username", username)
//                                addUser.putString("password", password)
//                                addUser.putString("name", respon[i].name)
//                                addUser.putInt("age", respon[i].age)
//                                addUser.putString("address", respon[i].address)
//                                addUser.apply()
//
//                                toast("Login Success!")
//                                val pinda = Intent(this@LoginActivity, HomeActivity::class.java)
//                                startActivity(pinda)
//                            }
//                        }
//                        if(!data) toast("Wrong Username or Password!")
//                    }
//                    else toast("Empty Response!")
//                }
//                else toast("Failed Load ItemPeriode!")
//            }
//
//            override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {
//
//            }
//
//        })
//    }
}