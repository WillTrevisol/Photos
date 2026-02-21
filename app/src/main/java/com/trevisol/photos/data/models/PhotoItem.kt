package com.trevisol.photos.data.models

import com.trevisol.photos.domain.entities.Photo

data class PhotoItem(
    val albumId: Int,
    val id: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String
) {
    fun toDomainEntity(): Photo = Photo(
        thumbnailUrl = "${thumbnailUrl.replace("via.placeholder.com", "dummyjson.com/image/")}/webp",
        url = "${url.replace("via.placeholder.com", "dummyjson.com/image/")}/webp",
        title = title
    )
}
