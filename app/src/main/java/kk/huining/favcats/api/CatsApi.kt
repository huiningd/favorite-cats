package kk.huining.favcats.api

import kk.huining.favcats.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * The Cats API (free)
 * https://thecatapi.com/
 */
interface CatsApi {

    /**
     * Fetch random images in small size.
     * Other optional queries can be used are:
     * mime_types, format, order, page, has_breeds, category_ids
     */
    @GET("images/search")
    suspend fun getRandomImagesSmall(
        // optional. The size of image to return - small, med or full. small is perfect for Discord. Defaults to med
        @Query("size") size: String = "small",
        // optional. number of results to return. Without an API Key you can only pass 1, with a Key you can pass up to 25. Default is 1
        @Query("limit") limit: Int = 24,
        // optional. Comma delimited string of integers, matching the id's of the Categories to filter the search. These categories can found in the /v1/categories request. e.g. category_ids=2
        //@Query("category_ids") category_ids: String = "",
    ): Response<List<Image>>

    @GET("images/search")
    suspend fun searchImagesByBreed(
        // optional. The size of image to return - small, med or full. small is perfect for Discord. Defaults to med
        @Query("size") size: String = "small",
        // optional. number of results to return. Without an API Key you can only pass 1, with a Key you can pass up to 25. Default is 1
        @Query("limit") limit: Int = 24,
        // optional 	Comma delimited string of integers, matching the id's of the Breeds to filter the search. These categories can found in the /v1/breeds request
        @Query("breed_ids") breed_ids: String,
    ): Response<List<Image>>

    @GET("breeds")
    @Headers("No-Authentication: true")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 100,
        @Query("page") page: Int = 0
    ): Response<List<Breed>>

    @GET("breeds/{id}")
    @Headers("No-Authentication: true")
    suspend fun getBreedById(@Path("id") id: Int): Response<Breed>

    @GET ("favourites")
    suspend fun getFavourites(): Response<List<Favorite>>

    @GET("favourites/{id}")
    suspend fun getFavouriteById(@Path("id") id: String): Response<Favorite>

    @DELETE("favourites/{id}")
    suspend fun removeFavouriteById(@Path("id") id: String): Response<DefaultResponse>

    @POST("favourites")
    suspend fun addToFavourites(@Body body: AddToFavoriteRequest): Response<AddToFavoriteResponse>

    @GET("images/")
    suspend fun getUploadedImages(
        @Query("limit") limit: Int = 20,
    ): Response<List<Image>>

    @GET("images/{id}")
    suspend fun getImageById(@Path("id") id: String): Response<Image>

    @DELETE("images/{id}")
    suspend fun deleteImageById(@Path("id") id: Int): Response<Int>

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<UploadImageResponse>

}