package com.maurya.clouddrop.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linkRecords")
data class UploadResponse(
    val file: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}

