package kk.huining.favcats.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefaultResponse (
    var message: String? = null,
    var id: Int? = null
)

@JsonClass(generateAdapter = true)
data class AddToFavoriteResponse (
    var message: String,
    var id: Long
)

@JsonClass(generateAdapter = true)
data class AddFavRequest (
    var image_id: String,
    // optional. A custom value you can set yourself, and then use to split the results of GET /favourites by, e.g. your own user's id.
    var sub_id: String? = null
)