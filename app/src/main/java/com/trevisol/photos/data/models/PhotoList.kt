package com.trevisol.photos.data.models

import com.trevisol.photos.domain.entities.Photo

class PhotoList : ArrayList<PhotoItem>() {
    fun toDomainList(): List<Photo> =
        this.map { photoItem -> photoItem.toDomainEntity()}
}
