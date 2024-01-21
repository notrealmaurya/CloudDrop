package com.maurya.clouddrop.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "linkRecords")
data class DataLink(
    val title: String,
    val link: String,
    val createdAt: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

