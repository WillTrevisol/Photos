package com.trevisol.photos.domain.entities

data class Photo(
    val thumbnailUrl: String,
    val title: String,
    val url: String
) {
    override fun toString(): String = title
}