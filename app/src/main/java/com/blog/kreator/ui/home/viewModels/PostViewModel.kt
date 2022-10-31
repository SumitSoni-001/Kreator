package com.blog.kreator.ui.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.ui.home.repository.PostRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val postRepo: PostRepo) : ViewModel() {

    val postData get() = postRepo.postData

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

}