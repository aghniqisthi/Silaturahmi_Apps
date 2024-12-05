package com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.upnvjatim.silaturahmi.ApiConfig
import com.upnvjatim.silaturahmi.Endpoint
import com.upnvjatim.silaturahmi.model.response.ItemAllMatkul

class ListMkRepository (private val apiService: Endpoint) {
    fun getMatkul(
        token: String,
        idProgramProdi: String,
        q: String?
    ): LiveData<PagingData<ItemAllMatkul>> = Pager(
        config = PagingConfig(
            pageSize = 3,
            prefetchDistance = 2,
            initialLoadSize = 3,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            Log.d("cek reqpo pagingsource", "${idProgramProdi} ; $q")
            ListMkPagingSource(apiService, token, idProgramProdi, q)
        },
    ).liveData
}

object Injection {
    fun provideRepository(): ListMkRepository {
        val apiService = ApiConfig.getApiService()
        return ListMkRepository(apiService)
    }
}
