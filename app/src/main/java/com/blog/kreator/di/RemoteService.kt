package com.blog.kreator.di

import com.blog.kreator.ui.home.models.*
import com.blog.kreator.utils.Constants
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
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
import retrofit2.http.Query

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

    @GET(Constants.GET_USER_BY_EMAIL)
    suspend fun getUserByEmail(@Query("email") email:String):Response<AuthResponse>
//    suspend fun getUserByEmail(@Header(Constants.AUTH_TOKEN) token : String, @Query("email") email:String):Response<GetUserDetails>

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

    @PUT(Constants.UPDATE_COMMENT)
    suspend fun updateComment(
        @Header(Constants.AUTH_TOKEN) token: String,
        @Path("commentId") commentId: Int,
        @Body body : CommentDetails
    ): Response<CommentDetails>

    @DELETE(Constants.DELETE_COMMENT)
    suspend fun deleteComment(@Header(Constants.AUTH_TOKEN) token : String, @Path("commentId") commentId : Int) : Response<DeleteResponse>

    @GET(Constants.GET_COMMENTS_BY_POST_ID)
    suspend fun getCommentByPostId(@Header(Constants.AUTH_TOKEN) token : String, @Path("postId") postId : Int) : Response<ArrayList<CommentDetails>>

    @POST(Constants.ADD_BOOKMARK)
    suspend fun addBookmark(@Header(Constants.AUTH_TOKEN) token : String, @Path("userId") userId : Int, @Path("postId") postId : Int) : Response<DeleteResponse>

    @GET(Constants.GET_BOOKMARK_BY_USER)
    suspend fun getBookmarksByUser(@Header(Constants.AUTH_TOKEN) token : String, @Path("userId") userId : Int) : Response<ArrayList<BookmarkResponse>>

    @GET(Constants.GET_BOOKMARKED_POSTS)
    suspend fun getBookmarkedPosts(@Header(Constants.AUTH_TOKEN) token : String, @Path("userId") userId : Int) : Response<ArrayList<PostDetails>>

    @DELETE(Constants.DELETE_BOOKMARK)
    suspend fun deleteBookmark(@Header(Constants.AUTH_TOKEN) token : String, @Path("bookmarkId") bookmarkId : Int) : Response<DeleteResponse>

}