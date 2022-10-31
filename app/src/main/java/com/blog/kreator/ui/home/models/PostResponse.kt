package com.blog.kreator.ui.home.models

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("currentPageElements") var currentPageElements: Int? = null,
    @SerializedName("lastPage") var lastPage: Boolean? = null,
    @SerializedName("pageNumber") var pageNumber: Int? = null,
    @SerializedName("pageSize") var pageSize: Int? = null,
    @SerializedName("postDto") var postDto: ArrayList<PostDetails> = arrayListOf(),
    @SerializedName("totalElements") var totalElements: Int? = null,
    @SerializedName("totalPages") var totalPages: Int? = null
)
