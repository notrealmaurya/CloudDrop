package com.maurya.clouddrop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LinkDao {
    @Query("SELECT * FROM linkRecords")
    fun getAll(): List<DataLink>

    @Insert
    fun insert(vararg audioRecord: DataLink)

    @Delete
    fun delete(audioRecord: DataLink)

    @Delete
    fun delete(audioRecord: Array<DataLink>)


}
