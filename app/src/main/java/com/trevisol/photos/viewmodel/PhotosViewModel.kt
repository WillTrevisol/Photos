package com.trevisol.photos.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.toolbox.ImageRequest
import com.trevisol.photos.R
import com.trevisol.photos.data.repositories.JsonPlaceholderAPI
import com.trevisol.photos.domain.entities.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotosViewModel(
    private val repository: JsonPlaceholderAPI,
): ViewModel() {

    private val _photos = MutableStateFlow<PhotoState>(PhotoState.Initial)
    val photos: StateFlow<PhotoState> = _photos.asStateFlow()

    fun retrievePhotos() {
        _photos.value = PhotoState.Loading
        repository.addRequestToQueue(
            JsonPlaceholderAPI.PhotosRequest(
                { photos ->
                    _photos.value = PhotoState.Success(photos)
                },
                {
                    _photos.value = PhotoState.Error(R.string.failed_to_retrieve_photos)
                }
            )
        )
    }

    fun retrievePhoto(url: String) {
        _photos.value = PhotoState.Loading
        repository.addRequestToQueue(
            ImageRequest(
                url,
                { response ->
                    _photos.value = PhotoState.PhotoSuccess(response)
                },
                0, 0,
                ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                {
                    _photos.value = PhotoState.Error(R.string.failed_to_retrieve_photo)
                }
            )
        )
    }

    fun retrieveThumbnail(url: String) {
        _photos.value = PhotoState.Loading
        repository.addRequestToQueue(
            ImageRequest(
                url,
                { response ->
                    _photos.value = PhotoState.ThumbnailSuccess(response)
                },
                0, 0,
                ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                {
                    _photos.value = PhotoState.Error(R.string.failed_to_retrieve_thumbnail)
                }
            )
        )
    }

    companion object {
        fun photosViewFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PhotosViewModel(
                        JsonPlaceholderAPI.getInstance(context)
                    ) as T
                }
            }
    }

    sealed interface PhotoState {
        data object Initial: PhotoState
        data object Loading: PhotoState
        data class Success(val photos: List<Photo>): PhotoState
        data class PhotoSuccess(val bitmap: Bitmap): PhotoState
        data class ThumbnailSuccess(val bitmap: Bitmap): PhotoState
        data class Error(val resId: Int): PhotoState
    }
}