package com.blog.kreator.di

sealed class NetworkResponse<T>(val data : T? = null, val message : String? = null){
    class Success<T>(data : T) : NetworkResponse<T>(data)
    class Error<T>(msg : String? , data : T? = null) : NetworkResponse<T>(data , msg)
    class Loading<T> : NetworkResponse<T>()
}
