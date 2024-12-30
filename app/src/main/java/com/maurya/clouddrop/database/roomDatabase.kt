package com.maurya.clouddrop.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maurya.clouddrop.ApplicationClass
import com.maurya.clouddrop.util.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Entity(tableName = "linkRecords")
data class LinkDataClass(
    val title: String,
    val link: String,
    val createdAt: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class EmailRequest(
    val emailFrom: String,
    val emailTo: String,
    val uuid: String
)

data class UploadResponse(
    val file: String
)


@Database(entities = [LinkDataClass::class], version = 1, exportSchema = false)
abstract class LinkDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao

    companion object {
        @Volatile
        private var INSTANCE: LinkDatabase? = null

        fun getInstance(context: Context): LinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LinkDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


@Dao
interface LinkDao {

    @Query("SELECT * FROM linkRecords")
    fun getAllItems(): List<LinkDataClass>

    @Query("SELECT * FROM linkRecords")
    fun getAllItemsLiveData(): LiveData<List<LinkDataClass>>

    @Query("DELETE FROM linkRecords")
    suspend fun deleteAllItems()

    @Query("DELETE FROM linkRecords WHERE id = :id")
    suspend fun deleteSingleItem(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(link: List<LinkDataClass>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleItem(link: LinkDataClass)

}


object LinkRepositoryForSavingInDB {

    private val dbTempleDao = ApplicationClass.appDatabase.linkDao()

    suspend fun saveAllDBLink(link: List<LinkDataClass>) {
        try {
            dbTempleDao.insertAll(link)
            Log.d(Tags.DBTempleRepo, "Links saved successfully: ${link.size}")
        } catch (e: Exception) {
            Log.e(Tags.DBTempleRepo, "Error saving temple details: ${e.message}")
            throw e
        }
    }

    suspend fun saveSingleDBLink(link: LinkDataClass) {
        try {
            dbTempleDao.insertSingleItem(link)
            Log.d(Tags.DBTempleRepo, "Links saved successfully: $link")
        } catch (e: Exception) {
            Log.e(Tags.DBTempleRepo, "Error saving temple details: ${e.message}")
            throw e
        }
    }

    suspend fun getLinkFromDatabaseAsync(): List<LinkDataClass> {
        return withContext(Dispatchers.IO) {
            val data = dbTempleDao.getAllItems()
            Log.d(Tags.DBTempleRepo, "Getting Size to fetch from API or db${data.size}")
            data
        }
    }

    fun getAllDBLink(): LiveData<List<LinkDataClass>> {
        return dbTempleDao.getAllItemsLiveData().also {
            Log.d(Tags.DBTempleRepo, "Fetching links as LiveData")
        }
    }

    // Delete all links from the database
    suspend fun deleteAllDBLinkDetails() {
        try {
            dbTempleDao.deleteAllItems()
            Log.d(Tags.DBTempleRepo, "All link records deleted successfully.")
        } catch (e: Exception) {
            Log.e(Tags.DBTempleRepo, "Error deleting link records: ${e.message}")
        }
    }

    // New method to delete a single link by its ID
    suspend fun deleteSingleLink(id: Long) {
        try {
            dbTempleDao.deleteSingleItem(id)
            Log.d(Tags.DBTempleRepo, "Link with ID $id deleted successfully.")
        } catch (e: Exception) {
            Log.e(Tags.DBTempleRepo, "Error deleting link with ID $id: ${e.message}")
        }
    }

}