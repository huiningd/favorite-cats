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
) {
    val isFavorite: Boolean
        get() = favoriteId != null
}


@JsonClass(generateAdapter = true)
data class Favorite(
    var id: String? = null,
    var user_id: String? = null,
    var image_id: String? = null,
    var sub_id: String? = null,
    var created_at: String? = null, //  "2021-04-20T13:34:56.000Z",
    var image: Image? = null
)
