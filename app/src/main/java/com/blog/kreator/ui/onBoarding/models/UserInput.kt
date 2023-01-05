package com.blog.kreator.ui.onBoarding.models

import com.google.gson.annotations.SerializedName

data class UserInput(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("about") var about: String? = null,
    @SerializedName("isVerified") var isVerified: Boolean? = null
)
