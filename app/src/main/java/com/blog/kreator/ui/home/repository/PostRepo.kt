package com.blog.kreator.ui.home.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.di.RemoteService
import com.blog.kreator.ui.home.models.PostResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class PostRepo @Inject constructor(private val remoteService: RemoteService) {

    private val postLiveData = MutableLiveData<NetworkResponse<PostResponse>>()
    val postData get() = postLiveData

    suspend fun getAllPosts(){
        postLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getAllPosts()
            Log.d("postData" , response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            postLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getPostsByCategory(categoryId : Int){
        postLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getPostByCategory(categoryId)
            Log.d("postData" , response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            postLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
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

}