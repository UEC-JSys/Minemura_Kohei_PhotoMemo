package com.example.photomemo.ViewModel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.example.photomemo.Model.Photo
import com.example.photomemo.Model.PhotoRepository
import com.example.photomemo.Model.PhotoRoomDatabase

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoViewModel(application: Application) : AndroidViewModel(application){
    private val repository: PhotoRepository
    val allPhotos: LiveData<List<Photo>>

    init {
        val photoDao = PhotoRoomDatabase.getPhotoDatabase(application).photoDao()
        repository = PhotoRepository(photoDao)
        allPhotos = repository.allPhotos
    }

    fun getAllThumbs(photos: List<Photo>) : List<Pair<Photo, Bitmap?>>{
        return repository.getAllThumnails(
            photos, getApplication<Application>().contentResolver
        ).toList()
    }

}