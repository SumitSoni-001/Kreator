package com.blog.kreator.ui.home.models

import com.google.gson.annotations.SerializedName

data class CommentDetails(
    @SerializedName("content") var content: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("isEdited") var isEdited: Boolean? = null,
    @SerializedName("postedOn") var postedOn: String? = null,
    @SerializedName("userDetails") var userDetails : String? = null

)
