package kk.huining.favcats.data.model


/**
"id": "S1bsCGxrf",
"url": "http://78.media.tumblr.com/2bc94b9eec2d00f5d28110ba191da896/tumblr_nyled8DYKd1qg9kado1_1280.jpg",
"width": null,
"height": null,
"mime_type": "image/jpeg",
"entities": [],
"breeds": [
{
"id": 3,
"name": "Alaskan Malamute",
"wikipedia_url": "https://en.wikipedia.org/wiki/Alaskan_Malamute"
},
{
"id": 2,
"name": "Akita",
"wikipedia_url": "https://en.wikipedia.org/wiki/Akita_(dog)"
}
],
"animals": [],
"categories": []*/

data class Image(
    var id: String? = null,
    var url: String? = null,
    var isFavorite: Boolean = false,
    var width: Int? = null,
    var height: Int? = null,
    var mime_type: String? = null,
    //var entities: List,
    //var animals: List,
    //var categories: List,
    val breeds: List<Breed> = emptyList(),
)

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