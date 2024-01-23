package com.maurya.clouddrop.module

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linkRecords")
data class UploadResponse(
    val title: String,
    val link: String,
    val createdAt: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

