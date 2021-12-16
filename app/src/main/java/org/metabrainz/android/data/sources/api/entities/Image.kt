package org.metabrainz.android.data.sources.api.entities

data class Image(
    val approved: Boolean,
    val back: Boolean,
    val comment: String,
    val edit: Int,
    val front: Boolean,
    val id: String,
    val image: String,
    val thumbnails: Thumbnails,
    val types: List<String>
)