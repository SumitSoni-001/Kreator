package com.blog.kreator.ui.home.models

import com.google.gson.annotations.SerializedName

data class DeleteResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: Boolean? = null
)
