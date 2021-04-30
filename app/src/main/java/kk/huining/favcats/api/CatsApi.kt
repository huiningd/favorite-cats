package kk.huining.favcats.api

import kk.huining.favcats.data.model.Image
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {

    //[{"breeds":[],"id":"ykIQfV8BR","url":"https://cdn2.thecatapi.com/images/ykIQfV8BR.false","width":2187,"height":2187,"sub_id":"8AC0FC11-EC30-478E-892A-3E1CF623CCCD","created_at":"2021-04-21T15:31:10.000Z","original_filename":"file.jpg","breed_ids":null}]
    @GET("images/")
    suspend fun getUploadedImages(): Response<List<Image>>

    @GET("images/{id}")
    suspend fun getImageById(): Response<Image>

    @DELETE("images/{id}")
    suspend fun deleteImageById(@Path("id") id: Int): Response<Int>

    // POST /images/upload

}