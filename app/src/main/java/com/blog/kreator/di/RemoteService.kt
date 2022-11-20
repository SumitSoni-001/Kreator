package com.blog.kreator.di

import com.blog.kreator.ui.home.models.*
import com.blog.kreator.utils.Constants
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.google.android.material.datepicker.CalendarConstraints
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

interface RemoteService {

    @POST(Constants.REGISTER_USER)
    suspend fun registerUser(@Body body: UserInput): Response<AuthResponse>

    @POST(Constants.LOGIN)
    suspend fun login(@Body body: LoginDetails): Response<AuthResponse>

    @PUT(Constants.UPDATE_USER)
    suspend fun updateUser(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Path("userId") userId: Int,
        @Body body: UserInput
    ): Response<GetUserDetails>

//    @GET(Constants.GET_USER)
//    suspend fun getUserById(@Path("userId") userId:Int):Response<GetUserDetails>

//    @GET(Constants.GET_USER)
//    suspend fun getAllUsers():Response<List<GetUserDetails>>

    @POST(Constants.CREATE_POST)
    suspend fun createPost(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Path("userId") userId: Int,
        @Path("categoryId") categoryId: Int,
        @Body body: PostInput
    ): Response<PostDetails>

    @PUT(Constants.UPDATE_POST)
    suspend fun updatePost(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Path("postId") postId: Int,
        @Body body: PostInput
    ): Response<PostDetails>

    @Multipart
    @POST(Constants.UPLOAD_PROFILE)
    suspend fun uploadProfile(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Part image: MultipartBody.Part,
        @Path("userId") userId: Int
    ): Response<GetUserDetails>

    @DELETE(Constants.DELETE_POST)
    suspend fun deletePost(
        @Header(Constants.AUTH_TOKEN) token : String,
        @Path("postId") postId : Int
    ) : Response<DeleteResponse>

    @GET(Constants.GET_POST_BY_USER)
    suspend fun getPostByUser(@Header(Constants.AUTH_TOKEN) token : String, @Path("userId") userId: Int): Response<PostResponse>

    @GET(Constants.GET_ALL_POST)
    suspend fun getAllPosts(@Header(Constants.AUTH_TOKEN) token : String): Response<PostResponse>

    @GET(Constants.GET_POST_BY_CATEGORY)
    suspend fun getPostByCategory(@Header(Constants.AUTH_TOKEN) token : String, @Path("categoryId") categoryId: Int): Response<PostResponse>

    @Multipart
    @POST(Constants.UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Part image: MultipartBody.Part,
        @Path("postId") postId: Int
    ): Response<PostDetails>

//    @GET(Constants.DOWNLOAD_IMAGE)
//    suspend fun downloadImage(@Header(Constants.AUTH_TOKEN) token : String, @Path("imageName") imageName : String)

    @GET(Constants.GET_POST_BY_POST_ID)
    suspend fun getPostByPostId(@Header(Constants.AUTH_TOKEN) token : String, @Path("postId") postId: Int): Response<PostDetails>

    @POST(Constants.CREATE_COMMENT)
    suspend fun createComment(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Path("postId") postId: Int,
        @Path("userId") userId: Int,
        @Body body: CommentDetails
    ): Response<CommentDetails>

}