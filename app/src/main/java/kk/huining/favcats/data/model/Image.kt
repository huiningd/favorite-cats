package kk.huining.favcats.data.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Image(
    var id: String? = null,
    var url: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var mime_type: String? = null,
    val breeds: List<Breed> = emptyList(),
    var favoriteId: String? = null,
    var created_at: String? = null, // my uploaded image has created_at (ISO8601 string)
) {
    val isFavorite: Boolean
        get() = favoriteId != null
        // isFavorite must be val! no public setter, this value can only be calculated
}


@JsonClass(generateAdapter = true)
data class Favorite(
    var id: String? = null,
    var user_id: String? = null,
    var image_id: String? = null,
    var sub_id: String? = null,
    var created_at: String? = null, // ISO8601 "2021-04-20T13:34:56.000Z",
    var image: Image? = null
)
