package com.example.photomemo

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = "photo_table")

data class Photo (
    @PrimaryKey @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "memo") val memo: String
    ) {
    }

@Dao
interface PhotoDao {
    @Query("SELECT * from photo_table")
    fun getPhotos(): LiveData<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert (photo: Photo)
}

@Database(entities = arrayOf(Photo::class), version = 1, exportSchema = false)
public abstract class PhotoRoomDatabase : RoomDatabase () {
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoRoomDatabase? = null

        fun getPhotoDatabase(context: Context) : PhotoRoomDatabase{
            val tmpInstance = INSTANCE
            if (tmpInstance != null){
                return tmpInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoRoomDatabase::class.java,
                    "photo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

class PhotoRepository(private val photoDao: PhotoDao){
    val allPhotos: LiveData<List<Photo>> = photoDao.getPhotos()

    suspend fun insert(photo: Photo) {
        photoDao.insert(photo)
    }
}

class AddPhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PhotoRepository
    val allPhotos: LiveData<List<Photo>>

    init {
        val photoDao = PhotoRoomDatabase.getPhotoDatabase(application).photoDao()
        repository = PhotoRepository(photoDao)
        allPhotos = repository.allPhotos
    }

    fun insert (photo:Photo) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(photo)
    }
}