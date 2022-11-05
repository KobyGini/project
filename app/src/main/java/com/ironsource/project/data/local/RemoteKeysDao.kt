package com.ironsource.project.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM REMOTE_KEYS_TABLE WHERE movieId = :id")
    suspend fun remoteKeysMovieId(id: String): RemoteKeys

    @Query("DELETE FROM REMOTE_KEYS_TABLE")
    suspend fun clearRemoteKeys()
}
