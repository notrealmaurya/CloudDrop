package com.maurya.clouddrop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.maurya.clouddrop.model.UploadResponse

@Dao
interface LinkDao {
    @Query("SELECT * FROM linkRecords")
    fun getAll(): List<UploadResponse>

    @Insert
    fun insert(vararg audioRecord: UploadResponse)

    @Delete
    fun delete(audioRecord:UploadResponse)

    @Delete
    fun delete(audioRecord: Array<UploadResponse>)


}
