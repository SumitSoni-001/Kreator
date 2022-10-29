package com.blog.kreator.ui.onBoarding.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") var token: String?,
    @SerializedName("user") var user: GetUserDetails
)
