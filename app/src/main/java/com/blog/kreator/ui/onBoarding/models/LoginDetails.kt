package com.blog.kreator.ui.onBoarding.models

import com.google.gson.annotations.SerializedName

data class LoginDetails(
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null
)
