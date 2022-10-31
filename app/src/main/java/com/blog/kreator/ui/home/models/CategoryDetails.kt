package com.blog.kreator.ui.home.models

import com.google.gson.annotations.SerializedName

data class CategoryDetails(
    @SerializedName("categoryDesc") var categoryDesc: String? = null,
    @SerializedName("categoryId") var categoryId: Int? = null,
    @SerializedName("categoryTitle") var categoryTitle: String? = null
)
