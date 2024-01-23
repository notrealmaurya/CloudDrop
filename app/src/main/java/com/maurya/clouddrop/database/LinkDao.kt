package com.maurya.clouddrop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.maurya.clouddrop.model.UploadRequest
import com.maurya.clouddrop.model.UploadResponse

@Dao
interface LinkDao {
    @Query("SELECT * FROM linkRecords")
    fun getAll(): List<UploadRequest>

    @Insert
    fun insert(vararg audioRecord: UploadRequest)

    @Delete
    fun delete(audioRecord:UploadRequest)

    @Delete
    fun delete(audioRecord: Array<UploadRequest>)


}
