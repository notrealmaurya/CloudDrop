package com.maurya.clouddrop.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maurya.clouddrop.module.UploadResponse

@Database(entities = [UploadResponse::class], version = 1)
abstract class LinkDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao

}