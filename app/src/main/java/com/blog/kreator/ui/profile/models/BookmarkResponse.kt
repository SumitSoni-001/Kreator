package com.blog.kreator.ui.profile.models

import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.google.gson.annotations.SerializedName

data class BookmarkResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("post") var post : PostDetails? = PostDetails(),
//    @SerializedName("user") var user : GetUserDetails? = GetUserDetails()
)
