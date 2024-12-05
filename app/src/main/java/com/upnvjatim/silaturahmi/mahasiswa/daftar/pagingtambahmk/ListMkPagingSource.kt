package com.upnvjatim.silaturahmi.mahasiswa.daftar.pagingtambahmk

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.upnvjatim.silaturahmi.Endpoint
import com.upnvjatim.silaturahmi.model.response.ItemAllMatkul

//class ListMkPagingSource(
//    private val endpoint: Endpoint,
//    private val token:String,
//    private val idProgramProdi:String,
//    private val q:String?
//)
//    : PagingSource<Int, ItemAllMatkul>() {
//
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllMatkul> {
//        return try {
//            val page = params.key ?: INITIAL_PAGE_INDEX
//            val responseData = endpoint.getAllMatkul(token, params.loadSize, page, idProgramProdi, q)
//            val a = responseData.data?.items ?: emptyList()
//
//            Log.d("cek mk pagingsource", "$q ; pg $page ; sz ${a.size} ; ${responseData.data?.meta?.currentPage} ; ${
//                responseData.data?.meta?.nextPage} ; ${responseData.data?.meta?.previousPage} ; ${
//                responseData.data?.items?.firstOrNull()?.namaMatkul}")
//
//            LoadResult.Page(
//                data = a,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (responseData.data?.items.isNullOrEmpty()) null else page + 1
//            )
//        } catch (exception: Exception) {
//            return LoadResult.Error(exception)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, ItemAllMatkul>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//}




class ListMkPagingSource (
    private val apiService: Endpoint,
    private val token: String,
    private val idProgramProdi: String,
    private val q: String?
) : PagingSource<Int, ItemAllMatkul>() {

    override fun getRefreshKey(state: PagingState<Int, ItemAllMatkul>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)

            state.closestPageToPosition(anchorPosition)?.let { anchorPage ->
                anchorPage.nextKey?.minus(1) ?: anchorPage.prevKey?.plus(1)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemAllMatkul> {
        return try {
            val position = params.key ?: 1
            val responseData = apiService.getAllMatkul(token, params.loadSize, position, idProgramProdi, q).data?.items
            val prevKey = if (position == 1) null else position - 1
            val nextKey = if (responseData.isNullOrEmpty()) null else position + 1

            Log.d("cek PagingSource $position size ${params.loadSize}", "PrevKey: $prevKey, NextKey: $nextKey, Data loaded: ${responseData?.map { "${it.id} = ${it.namaMatkul}" }}")

            LoadResult.Page(data = responseData!!, prevKey = prevKey, nextKey = nextKey)

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}