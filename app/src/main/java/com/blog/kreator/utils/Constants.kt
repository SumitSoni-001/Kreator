package com.blog.kreator.utils

class Constants {

    companion object {
        const val BASE_URL = "http://192.168.182.65:9090"
        const val AUTH_TOKEN = "Authorization"
        const val SHARED_PREFS = "SHARED_PREFS"
        const val REGISTER_USER = "/api/auth/register"
        const val LOGIN = "/api/auth/login"
        const val UPDATE_USER = "/api/users/update/{userId}"
        const val CREATE_POST = "/api/user/{userId}/category/{categoryId}/createPost"
        const val UPDATE_POST = "/api/postId/{postId}/updatePost"
        const val GET_POST_BY_POST_ID = "/api/postId/{postId}/getPost"
        const val GET_POST_BY_CATEGORY = "/api/categoryId/{categoryId}/getPost"
        const val GET_POST_BY_USER = "/api/userId/{userId}/getPost"
        const val GET_ALL_POST = "/api/getAllPosts"
        const val DELETE_POST = "/api/postId/{postId}/delete"
        const val UPLOAD_IMAGE = "/api/post/image/uploadImage/{postId}"
        const val UPLOAD_PROFILE = "/api/users/uploadProfilePic/{userId}"
        const val CREATE_COMMENT = "/api/comment/user/{userId}/post/{postId}/create"
        const val UPDATE_COMMENT = "api/comment/{commentId}/updateComment"
        const val GET_COMMENTS_BY_POST_ID = "/api/comment/postId/{postId}/getComments"
        const val DELETE_COMMENT = "/api/comment/delete/{commentId}"
        const val ADD_BOOKMARK = "/api/bookmark/userId/{userId}/postId/{postId}/createBookmark"
        const val GET_BOOKMARK_BY_USER = "/api/bookmark/userId/{userId}/geBookmarksByUser"
        const val GET_BOOKMARKED_POSTS = "/api/post/userId/{userId}/bookmarkedPosts"
        const val DELETE_BOOKMARK = "/api/bookmark/bookmarkId/{bookmarkId}/deleteBookmark"

        //    const val GET_USER_BY_EMAIL = "api/users/get/"
        const val GET_USER_BY_EMAIL = "api/auth/getUser/"


        val ALL_CATEGORIES = arrayOf(
            "Travel", "Food", "Psychology", "Books", "Science", "Education", "Android", "ios", "Technology",
            "Artificial Intelligence", "Movies", "Health", "Software Engineering", "Life", "Work", "Programming",
            "Business", "Design", "Fashion", "Web Development", "App Development", "Art", "Blockchain", "Java Script",
            "Ui/Ux", "Flutter", "Java", "Relationship", "Philosophy", "History", "Money", "Crypto", "Sports",
            "Freelancing", "Photography", "Web Framework", "Animals")
    }

}