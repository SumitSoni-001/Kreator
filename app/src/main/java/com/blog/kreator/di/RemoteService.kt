package com.blog.kreator.di

import com.blog.kreator.ui.home.models.PostResponse
import com.blog.kreator.utils.Constants
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.google.android.material.datepicker.CalendarConstraints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteService {

    @POST(Constants.REGISTER_USER)
    suspend fun registerUser(@Body body: UserInput): Response<AuthResponse>

    @POST(Constants.LOGIN)
    suspend fun login(@Body body : LoginDetails) : Response<AuthResponse>

//    @GET(Constants.GET_USER)
//    suspend fun getUserById(@Path("userId") userId:Int):Response<GetUserDetails>

//    @GET(Constants.GET_USER)
//    suspend fun getAllUsers():Response<List<GetUserDetails>>

    @GET(Constants.GET_ALL_POST)
    suspend fun getAllPosts() : Response<PostResponse>

    @GET(Constants.GET_POST_BY_CATEGORY)
    suspend fun getPostByCategory(@Path("categoryId") categoryId : Int) : Response<PostResponse>

    @POST(Constants.UPLOAD_IMAGE)
    suspend fun uploadImage(@Path("postId") postId : Int) : Response<PostResponse>

//    @GET(Constants.DOWNLOAD_IMAGE)
//    suspend fun downloadImage(@Path("imageName") imageName : String)


}