package com.maurya.clouddrop.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linkRecords")
data class UploadRequest(
    val title: String,
    val link: String,
    val createdAt: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

