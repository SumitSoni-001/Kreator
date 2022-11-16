package com.blog.kreator.ui.home.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.di.RemoteService
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.models.PostInput
import com.blog.kreator.ui.home.models.PostResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class PostRepo @Inject constructor(private val remoteService: RemoteService) {

    private val postLiveData = MutableLiveData<NetworkResponse<PostResponse>>()
    val postData get() = postLiveData

    private val singlePostLiveData = MutableLiveData<NetworkResponse<PostDetails>>()
    val singlePostData get() = singlePostLiveData

    suspend fun createPost(token: String, userId: Int, catId: Int, postInput: PostInput) {
        singlePostLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.createPost(token, userId, catId, postInput)
            Log.d("createPostData", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            singlePostData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun updatePost(token: String, postId: Int, postInput: PostInput) {
        singlePostLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.updatePost(token, postId, postInput)
            Log.d("updatedPostData", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            singlePostData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun uploadImage(token: String, postId: Int, image: MultipartBody.Part) {
        singlePostData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.uploadImage(token,image, postId)
            Log.d("uploadImage", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            singlePostData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getAllPosts() {
        postLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getAllPosts()
//            Log.d("getAllPost" , response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            postLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getPostsByCategory(categoryId: Int) {
        postLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getPostByCategory(categoryId)
//            Log.d("getPostByCategory" , response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            postLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getPostByUser(userId: Int) {
        postLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getPostByUser(userId)
            Log.d("getPostByUser" , response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            postLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getPostByPostId(postId: Int) {
        singlePostLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getPostByPostId(postId)
//            Log.d("getPostByPostId" , response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            singlePostLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    private fun handleResponse(response: Response<PostResponse>) {
        if (response.isSuccessful && response.body() != null) {
            postLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            postLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            postLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

    private fun handleResponse2(response: Response<PostDetails>) {
        if (response.isSuccessful && response.body() != null) {
            singlePostLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            singlePostLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            singlePostLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

}