package com.blog.kreator.ui.onBoarding.models

import com.blog.kreator.ui.home.models.CommentDetails
import com.google.gson.annotations.SerializedName

data class GetUserDetails(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
//    @SerializedName("password") var password: String? = null,
    @SerializedName("userImage") var userImage: String? = null,
    @SerializedName("about") var about: String? = null,
    @SerializedName("comments") var comments: ArrayList<CommentDetails> = arrayListOf(),
    @SerializedName("roles") var roles: ArrayList<Roles> = arrayListOf()
)