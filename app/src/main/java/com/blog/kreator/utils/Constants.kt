package com.blog.kreator.utils

object Constants {

    const val BASE_URL = "http://192.168.155.65:9090"
    const val REGISTER_USER = "/api/auth/register"
    const val LOGIN = "/api/auth/login"
    const val GET_ALL_POST = "/api/getAllPosts"
    const val GET_POST_BY_CATEGORY = "/api/categoryId/{categoryId}/getPost"
    const val UPLOAD_IMAGE = "/api/post/image/uploadImage/{postId}"
    const val DOWNLOAD_IMAGE = "/api/post/downloadImage/{imageName}"

}