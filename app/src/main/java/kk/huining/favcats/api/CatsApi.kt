package kk.huining.favcats.api

import kk.huining.favcats.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * The Cats API (free)
 * https://thecatapi.com/
 */
interface CatsApi {

    /**
     * Search for images.
     *
     * Other optional queries can be used are:
     * mime_types 	optional 	Comma delimited string of the image types to return gif, jpg, orpng. Defaults to return all types jpg,gif,png.
     * format 	optional 	Response format json, or src. src will redirect straight to the image, so is useful for putting a link straight into HTML as the 'src' on an 'img' tag. Defaults to json
     * order 	optional 	The order to return results in. RANDOM, ASC or DESC. If either ASC or DESC is passed then the Pagination headers will be on the response allowing you to see the total amount of results, and your current page. Default is RANDOM
     * page 	optional 	Integer - used for Paginating through all the results. Only used when order is ASC or DESC
     * has_breeds 	optional 	Only return images which have breed data attached. Integer - 0 or 1. Default is 0
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
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 0
    ): Response<List<Breed>>

    @GET("breeds/{id}")
    @Headers("No-Authentication: true")
    suspend fun getBreedById(@Path("id") id: Int): Response<Breed>

    @GET ("favourites")
    suspend fun getFavourites(): Response<List<Image>>

    @GET("favourites/{id}")
    suspend fun getFavouriteById(@Path("id") id: String): Response<Image>

    @DELETE("favourites/{id}")
    suspend fun removeFavouriteById(@Path("id") id: String): Response<DefaultResponse>

    @POST("favourites")
    suspend fun addToFavourites(@Body body: AddFavRequest): Response<AddToFavoriteResponse>

    @GET("images/")
    suspend fun getUploadedImages(): Response<List<Image>>

    @GET("images/{id}")
    suspend fun getImageById(@Path("id") id: String): Response<Image>

    @DELETE("images/{id}")
    suspend fun deleteImageById(@Path("id") id: Int): Response<Int>

    //@POST("images/upload")
    //suspend fun uploadCatImage(@Body body: File) : Response<Int>

}