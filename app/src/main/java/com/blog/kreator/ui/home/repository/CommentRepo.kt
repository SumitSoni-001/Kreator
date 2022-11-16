package com.blog.kreator.ui.home.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.di.RemoteService
import com.blog.kreator.ui.home.models.CommentDetails
import retrofit2.Response
import javax.inject.Inject

class CommentRepo @Inject constructor(private val remoteService: RemoteService) {

    private var commentLiveData = MutableLiveData<NetworkResponse<CommentDetails>>()
    val commentData get() = commentLiveData

    suspend fun createComment(token: String, userId: Int, postId: Int , commentDetails: CommentDetails) {
        commentLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.createComment(token, postId, userId, commentDetails)
            Log.d("addComment" , response.body().toString())
            handleResponse(response)
        } catch (e:Exception){
            commentLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    private fun handleResponse(response: Response<CommentDetails>) {
        if (response.isSuccessful && response.body() != null) {
            commentLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            commentLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        } else {
            commentLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        }
    }

}