package com.blog.kreator.ui.onBoarding.models

import com.google.gson.annotations.SerializedName

data class GetUserDetails (
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
//    @SerializedName("password") var password: String? = null,
    @SerializedName("about") var about: String? = null,
    @SerializedName("comments") var comments: ArrayList<String> = arrayListOf(),
    @SerializedName("roles") var roles: ArrayList<Roles> = arrayListOf()
)