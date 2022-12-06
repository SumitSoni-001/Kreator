package com.blog.kreator.ui.profile.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.ui.profile.repository.BookmarkRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(private val bookmarkRepo: BookmarkRepo) : ViewModel() {

    val bookmarkData get() = bookmarkRepo.bookmarkData
    val bookmarkListData get() = bookmarkRepo.bookmarksListData
//    val deleteBookmarkData get() = bookmarkRepo.deleteBookmarkData

    fun addBookmark(token: String, userId: Int, postId: Int) {
        viewModelScope.launch {
            bookmarkRepo.addBookmark(token, userId, postId)
        }
    }

    fun getBookmarkByUser(token: String, userId: Int) {
        viewModelScope.launch {
            bookmarkRepo.getBookmarksByUser(token, userId)
        }
    }

/*
    fun getBookmarkedPosts(token:String,userId:Int){
        viewModelScope.launch {
            bookmarkRepo.getBookmarkedPosts(token ,userId)
        }
    }
*/

    fun deleteBookmark(token:String, bookmarkID : Int){
        viewModelScope.launch {
            bookmarkRepo.deleteBookmark(token,bookmarkID)
        }
    }

}