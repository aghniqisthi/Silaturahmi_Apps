package com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MessageItem::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {

    abstract val messageDao: MessageDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MessageDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java, "message_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
