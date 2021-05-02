package kk.huining.favcats.data.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Image(
    var id: String? = null,
    var url: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var mime_type: String? = null,
    //var entities: List,
    //var animals: List,
    //var categories: List,
    val breeds: List<Breed> = emptyList(),
    var favoriteId: String? = null,
) {
    val isFavorite: Boolean
        get() = favoriteId != null
}



