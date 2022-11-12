package com.blog.kreator.ui.home.models

import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.google.gson.annotations.SerializedName

data class PostInput(
    @SerializedName("content") var content: String? = null,
    @SerializedName("date") var date: String? = null,
//    @SerializedName("image") var image: String? = null,
//    @SerializedName("postId") var postId: Int? = null,
    @SerializedName("postTitle") var postTitle: String? = null,
)
