package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.firebase.Timestamp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator (
    private val database: MessageDatabase, private val groupId: String
) : RemoteMediator<Int, MessageItem>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

        override suspend fun load(loadType: LoadType, state: PagingState<Int, MessageItem>): MediatorResult {

            var query = FirebaseFirestore.getInstance()
                .collection("chats").document(groupId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            // Determine starting point based on LoadType
            val lastSnapshot = when (loadType) {
                LoadType.REFRESH -> {
                    database.remoteKeysDao.deleteRemoteKeys()
                    database.messageDao.deleteAll()
                    Log.d("cek refresh", "refresh")
                    null
                }

                LoadType.APPEND -> {
                    // Get the snapshot of the last item for `startAfter`
                    Log.d("cek append", getRemoteKeyForLastItem(state)?.timestamp.toString())
                    getRemoteKeyForLastItem(state)?.timestamp?.let {
                        Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt())
                    }
                }

                LoadType.PREPEND -> {
                    Log.d("cek prepend", getRemoteKeyForFirstItem(state)?.id.toString())
                    return MediatorResult.Success(endOfPaginationReached = true)
                } // return MediatorResult.Success(endOfPaginationReached = true)
            }

            // Apply `startAfter` if we have a last snapshot
            lastSnapshot?.let {
                query = query.startAfter(it)
                Log.d("cek startagter", "$it ; ${query}")
            }

            try {
                // Fetch data from Firestore
                val result = query
                    .limit(state.config.pageSize.toLong())
                    .get().await()
                val endOfPaginationReached = result.isEmpty

                Log.d("cek result", "${result.documents.size
                } ; ${result.documents.firstOrNull()?.id} ; ${endOfPaginationReached
                } ; ${result.documents.isNotEmpty()} ; ${result.documents.size > 0}")

                database.withTransaction {
                    // Insert Remote Keys with the last snapshot
                    if (loadType == LoadType.REFRESH) {
                        database.messageDao.deleteAll()
                        database.remoteKeysDao.deleteRemoteKeys()
                    }

                    if (result.documents.isNotEmpty() && result.documents.size > 0) {
                        val lastDocSnapshot = result.documents.last()
                        val remoteKeys = RemoteKeys(
                            id = lastDocSnapshot.id,
                            timestamp = lastDocSnapshot.getTimestamp("timestamp")?.toDate()?.time
                        )

                        database.remoteKeysDao.insertAll(result.documents.map {
                            RemoteKeys(
                                id = lastDocSnapshot.id,
                                timestamp = lastDocSnapshot.getTimestamp("timestamp")?.toDate()?.time
                            )
                        })

                        database.remoteKeysDao.insertLast(remoteKeys)
                        Log.d("cek remotekeysdao", lastDocSnapshot.id)
                    }

                    // Insert messages into the database
                    val a = result.documents.map { doc ->

                        val userDetailsMap = doc.get("sender") as? Map<String, String>
                        val username = userDetailsMap?.get("username")
                        val name = userDetailsMap?.get("name")

                        val replayDetailsMap = doc.get("replay") as? Map<String, String>
                        val messageReplay = replayDetailsMap?.get("message")
                        val senderReplay = replayDetailsMap?.get("name")

                        MessageItem(
                            chatId = doc.data?.get("chatId").toString(),
                            message = doc.data?.get("message").toString(),
                            username = username, name = name,
                            timestamp = doc.getTimestamp("timestamp")?.toDate()?.time,
                            reply = messageReplay,
                            senderReplay = senderReplay,
                        )
                    }
                    Log.d("cek insert mess", "${a.size}")
                    database.messageDao.insertMessage(a)
                }
                Log.d("cek mediator", endOfPaginationReached.toString())
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } catch (e: Exception) {
               return MediatorResult.Error(e)
            }
        }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MessageItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            Log.d("cek key last", data.timestamp.toString())
            database.remoteKeysDao.getRemoteKeysTimestamp(data.timestamp)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MessageItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            Log.d("cek key first", data.timestamp.toString())
            database.remoteKeysDao.getRemoteKeysTimestamp(data.timestamp)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MessageItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.timestamp?.let { data ->
                Log.d("cek key closes", data.toString())
                database.remoteKeysDao.getRemoteKeysTimestamp(data)
            }
        }
    }


}
