package com.blog.kreator.utils

object Constants {

    const val BASE_URL = "http://192.168.72.65:9090"
    const val AUTH_TOKEN = "Authorization"
    const val REGISTER_USER = "/api/auth/register"
    const val LOGIN = "/api/auth/login"
    const val UPDATE_USER = "/api/users/update/{userId}"
    const val GET_ALL_POST = "/api/getAllPosts"
    const val GET_POST_BY_CATEGORY = "/api/categoryId/{categoryId}/getPost"
    const val UPLOAD_IMAGE = "/api/post/image/uploadImage/{postId}"
    const val GET_POST_BY_POST_ID = "/api/postId/{postId}/getPost"
    const val CREATE_COMMENT = "/api/comment/user/{userId}/post/{postId}/create"
    const val CREATE_POST = "/api/user/{userId}/category/{categoryId}/createPost"
    const val UPDATE_POST = "/api/postId/{postId}/updatePost"
    const val GET_POST_BY_USER = "/api/userId/{userId}/getPost"



    val ALL_CATEGORIES = arrayOf("Travel", "Food", "Psychology", "Books", "Science", "Education", "Android", "ios", "Technology", "Artificial Intelligence", "Movies", "Health", "Software Engineering", "Life", "Work", "Programming", "Business", "Design", "Fashion", "Web Development", "App Development", "Art, Blockchain", "Java Script", "Ui/Ux", "Flutter", "Java", "Relationship", "Philosophy", "History", "Money", "Crypto", "Sports", "Freelancing", "Photography", "Web Framework", "Animals")

    fun userNameImage(name : String) : String{
        return "https://ui-avatars.com/api?name=$name&background=random"
    }
    fun downloadImage(image : String): String{
        return "$BASE_URL/api/post/downloadImage/$image"
    }

}