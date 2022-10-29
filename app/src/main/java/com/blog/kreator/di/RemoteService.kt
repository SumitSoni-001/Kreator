package com.blog.kreator.di

import com.blog.kreator.utils.Constants
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import retrofit2.Response
import retrofit2.http.Body
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


}