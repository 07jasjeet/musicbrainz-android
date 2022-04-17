package org.metabrainz.android.data.sources.api.entities.listens

data class Listen(
    val inserted_at: String,
    val listened_at: Int,
    val recording_msid: String,
    val track_metadata: TrackMetadata,
    val user_name: String
)