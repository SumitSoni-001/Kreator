package com.blog.kreator.ui.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.models.PostInput
import com.blog.kreator.ui.home.repository.PostRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val postRepo: PostRepo) : ViewModel() {

    val postData get() = postRepo.postData
    val singlePostData get() = postRepo.singlePostData

    fun createPost(token: String, userId: Int, catId: Int, postInput: PostInput) {
        viewModelScope.launch {
            postRepo.createPost(token, userId, catId, postInput)
        }
    }

    fun updatePost(token:String , postId : Int,postInput: PostInput){
        viewModelScope.launch {
            postRepo.updatePost(token, postId,postInput)
        }
    }

    fun getPostByUser(userId:Int){
        viewModelScope.launch {
            postRepo.getPostByUser(userId)
        }
    }

    fun uploadImage(token:String ,postId:Int,image:MultipartBody.Part) {
        viewModelScope.launch {
            postRepo.uploadImage(token,postId,image)
        }
    }

    fun getAllPosts() {
        viewModelScope.launch {
            postRepo.getAllPosts()
        }
    }

    fun getPostByCategory(categoryId: Int) {
        viewModelScope.launch {
            postRepo.getPostsByCategory(categoryId)
        }
    }

    fun getPostByPostId(postId: Int) {
        viewModelScope.launch {
            postRepo.getPostByPostId(postId)
        }
    }

}