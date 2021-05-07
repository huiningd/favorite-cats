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
data class AddToFavoriteRequest (
    var image_id: String,
    // optional. A custom value you can set yourself, and then use to split the results of GET /favourites by, e.g. your own user's id.
    var sub_id: String? = null
)


@JsonClass(generateAdapter = true)
data class UploadImageResponse (
    var id: String? = null,
    var url: String? = null,
    var pending: Int? = null, // 0 or 1
    var approved: Int? = null, // 0 or 1
)

@JsonClass(generateAdapter = true)
data class ServerErrorResponse (
    var message: String? = null,
    var status: Int? = null,
    var level: String? = null
)