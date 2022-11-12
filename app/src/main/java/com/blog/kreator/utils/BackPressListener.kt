package com.blog.kreator.utils

import java.text.SimpleDateFormat
import java.util.*

interface BackPressListener {
    fun onBackPressed(): Boolean
}

//fun main() {
//    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss" , Locale.getDefault())
//    val date = sdf.format(Date())
//    print(date)
//}