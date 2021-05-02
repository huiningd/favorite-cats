package kk.huining.favcats.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Breed(
    var id: String? = null,
    var name: String? = null,
    var wikipedia_url: String? = null,
    var origin: String? = null,
    var description: String? = null,
    var life_span: String? = null,
    var intelligence: Int? = null,
    var affection_level: Int? = null,
    var stranger_friendly: Int? = null,
    //var image: Image? = null
)
