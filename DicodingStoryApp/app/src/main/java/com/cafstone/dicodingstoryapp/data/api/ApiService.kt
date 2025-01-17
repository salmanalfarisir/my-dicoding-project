package com.cafstone.dicodingstoryapp.data.api

import com.cafstone.dicodingstoryapp.data.response.DetailStoryResponse
import com.cafstone.dicodingstoryapp.data.response.FileUploadResponse
import com.cafstone.dicodingstoryapp.data.response.GetAllStoriesResponse
import com.cafstone.dicodingstoryapp.data.response.LoginResponse
import com.cafstone.dicodingstoryapp.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStoriesAll(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): GetAllStoriesResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): GetAllStoriesResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): FileUploadResponse
}