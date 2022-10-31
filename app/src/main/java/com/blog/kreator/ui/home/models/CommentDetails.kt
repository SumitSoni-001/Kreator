package com.blog.kreator.ui.home.models

import com.google.gson.annotations.SerializedName

data class CommentDetails(
    @SerializedName("content") var content: String? = null,
    @SerializedName("id") var id: Int? = null

)
