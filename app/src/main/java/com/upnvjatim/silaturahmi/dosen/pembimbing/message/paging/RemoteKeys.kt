package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val id: String,

    @ColumnInfo
    val timestamp: Long?
)

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLast(remoteKey: RemoteKeys)

    @Query("SELECT * FROM remote_keys WHERE timestamp = :timestamp")
    suspend fun getRemoteKeysTimestamp(timestamp: Long?): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}
