package com.maurya.clouddrop.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataLink::class], version = 1)
abstract class LinkDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao

}