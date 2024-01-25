package com.maurya.clouddrop.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maurya.clouddrop.model.DataDatabase

@Dao
interface LinkDao {
    @Query("SELECT * FROM linkRecords")
    fun getAll(): List<DataDatabase>

    @Insert
    fun insert(vararg linkRecords: DataDatabase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<DataDatabase>)

    @Delete
    fun delete(linkRecords:DataDatabase)

    @Delete
    fun delete(linkRecords: Array<DataDatabase>)


}
