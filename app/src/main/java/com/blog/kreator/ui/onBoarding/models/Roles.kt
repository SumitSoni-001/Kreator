package com.blog.kreator.ui.onBoarding.models

import com.google.gson.annotations.SerializedName

data class Roles(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null
)
