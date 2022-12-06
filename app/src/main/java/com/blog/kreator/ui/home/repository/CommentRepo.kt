package com.blog.kreator.ui.home.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.di.RemoteService
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.ui.home.models.DeleteResponse
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class CommentRepo @Inject constructor(private val remoteService: RemoteService) {

    private var commentLiveData = MutableLiveData<NetworkResponse<CommentDetails>>()
    val commentData get() = commentLiveData

    private var commentsByPostLiveData = MutableLiveData<NetworkResponse<ArrayList<CommentDetails>>>()
    val commentByPostData get() = commentsByPostLiveData

    private var deleteCommentLiveData = MutableLiveData<NetworkResponse<DeleteResponse>>()
    val deleteCommentData get() = deleteCommentLiveData

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

    suspend fun updateComment(token:String, commentId:Int, commentDetails: CommentDetails){
        commentLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.updateComment(token , commentId, commentDetails)
            Log.d("updateComment", response.toString())
            handleResponse(response)
        }catch (e:Exception){
            commentLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getCommentByPostId(token : String , postId:Int){
        commentLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getCommentByPostId(token,postId)
            Log.d("GetCommentByPost", response.body().toString())
            handleResponse2(response)
        }catch (e:Exception){
            commentLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun deleteComment(token:String , commentId : Int){
        deleteCommentLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.deleteComment(token, commentId)
            Log.d("DeleteComment", response.body().toString())
            handleDeleteResponse(response)
        }catch (e:Exception){
            deleteCommentLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }


    private fun handleResponse(response: Response<CommentDetails>) {
        if (response.isSuccessful && response.body() != null) {
            commentLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            commentLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            commentLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        }
    }

    private fun handleResponse2(response: Response<ArrayList<CommentDetails>>) {
        if (response.isSuccessful && response.body() != null) {
            commentsByPostLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            commentsByPostLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            commentsByPostLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        }
    }

    private fun handleDeleteResponse(response: Response<DeleteResponse>){
        if (response.isSuccessful && response.body() != null){
            deleteCommentLiveData.postValue(NetworkResponse.Success(response.body()!!))
        }else if (response.errorBody() != null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            deleteCommentLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        }else{
            deleteCommentLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

}