package com.blog.kreator.utils

object CustomImage {

    private fun userNameImage(name: String): String {
        return "https://ui-avatars.com/api?name=$name&background=random"
    }

    fun downloadImage(image: String): String {
        return "${Constants.BASE_URL}/api/post/downloadImage/$image"
    }

    fun downloadProfile(image: String?, name: String): String {
        return if (image == null || image == "default.png") {
            userNameImage(name)
        } else {
            "${Constants.BASE_URL}/api/users/downloadProfilePic/$image"
        }
    }

}