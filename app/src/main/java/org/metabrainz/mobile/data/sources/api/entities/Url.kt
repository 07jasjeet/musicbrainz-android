package org.metabrainz.mobile.data.sources.api.entities

import com.google.gson.annotations.SerializedName

class Url {
    @SerializedName("id")
    var mbid: String? = null
    var resource: String? = null
}