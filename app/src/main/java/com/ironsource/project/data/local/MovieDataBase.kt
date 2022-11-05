package com.ironsource.project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironsource.project.domain.models.Movie

@Database(
    entities = [Movie::class, RemoteKeys::class],
    version = 7
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}