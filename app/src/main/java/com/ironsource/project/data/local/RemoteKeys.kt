package com.ironsource.project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "REMOTE_KEYS_TABLE")
data class RemoteKeys(@PrimaryKey val movieId: String, val prevKey: Int?, val nextKey: Int?)
