package com.example.photomemo.Model

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.LiveData

class PhotoRepository(private val photoDao: PhotoDao){
    val allPhotos: LiveData<List<Photo>> = photoDao.getPhotos()
    private val allThumbs: MutableMap<Photo, Bitmap?> = mutableMapOf()

    suspend fun insert(photo: Photo) {
        photoDao.insert(photo)
    }

    fun getAllThumnails(photos: List<Photo>, contentResolver: ContentResolver)
        :Map<Photo, Bitmap?>{
        photos.forEach{
            if(!allThumbs.containsKey(it)){
                allThumbs[it] = getThumnail(Uri.parse(it.uri),contentResolver)
            }
        }

        return allThumbs.toMap()
    }

    fun getThumnail (uri: Uri, contentResolver: ContentResolver): Bitmap? {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return contentResolver.loadThumbnail(uri, Size(100,100), null)
            // サムネイルがない場合はnullが返る
        }else{
            val uriPath = uri.pathSegments
            val id = uriPath.get(uriPath.size - 1).split(":")[1].toLong()

            return MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                id,
                MediaStore.Images.Thumbnails.MICRO_KIND,
                null
            )
        }
    }


}