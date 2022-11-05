package com.blog.kreator.ui.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.ui.home.repository.CommentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(private val commentRepo: CommentRepo) : ViewModel() {

    val commentData = commentRepo.commentData

    fun createComment(token: String, userId: Int, postId: Int, commentDetails: CommentDetails) {
        viewModelScope.launch {
            commentRepo.createComment(token,postId, userId, commentDetails)
        }

    }

}