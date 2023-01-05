package com.blog.kreator.ui.profile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.di.RemoteService
import com.blog.kreator.ui.home.models.DeleteResponse
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.profile.models.BookmarkResponse
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class BookmarkRepo @Inject constructor(private val remoteService: RemoteService) {
    private val bookmarkLiveData = MutableLiveData<NetworkResponse<DeleteResponse>>()
    val bookmarkData : LiveData<NetworkResponse<DeleteResponse>>
    get() = bookmarkLiveData

    private val bookmarksListLiveData = MutableLiveData<NetworkResponse<ArrayList<BookmarkResponse>>>()
    val bookmarksListData : LiveData<NetworkResponse<ArrayList<BookmarkResponse>>>
    get() = bookmarksListLiveData

//    private val deleteBookmarkLiveData = MutableLiveData<NetworkResponse<DeleteResponse>>()
//    val deleteBookmarkData get() = deleteBookmarkLiveData

    suspend fun addBookmark(token: String, userId: Int, postId: Int) {
        bookmarkLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.addBookmark(token, userId, postId)
            Log.d("addBookmark", response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            bookmarkLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    suspend fun getBookmarksByUser(token: String, userId: Int){
        bookmarksListLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getBookmarksByUser(token, userId)
            Log.d("getBookmarkByUser", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            bookmarksListLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

/*
    suspend fun getBookmarkedPosts(token: String, userId: Int){
        try {
            val response = remoteService.getBookmarkedPosts(token, userId)
            Log.d("getBookmarkedPosts", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            bookmarksListLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }
*/

    suspend fun deleteBookmark(token: String, bookmarkId: Int){
        try {
            val response = remoteService.deleteBookmark(token, bookmarkId)
            Log.d("deleteBookmark", response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            bookmarkLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
        }
    }

    private fun handleResponse(response: Response<DeleteResponse>) {
        if (response.isSuccessful && response.body() != null) {
            bookmarkLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            bookmarkLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            bookmarkLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        }
    }

    private fun handleResponse2(response: Response<ArrayList<BookmarkResponse>>) {
        if (response.isSuccessful && response.body() != null) {
            bookmarksListLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            bookmarksListLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            bookmarksListLiveData.postValue(NetworkResponse.Error("Something went wrong!!"))
        }
    }

/*
    private fun handleDeleteResponse(response: Response<DeleteResponse>){
        if (response.isSuccessful && response.body() != null){
            deleteBookmarkLiveData.postValue(NetworkResponse.Success(response.body()!!))
        }else if (response.errorBody() != null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            deleteBookmarkLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        }else{
            deleteBookmarkLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }
*/

}