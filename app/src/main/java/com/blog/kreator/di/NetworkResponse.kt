package com.blog.kreator.di

/** A network request result in one of the following and have some data and message.
 *  Initially the Network request shows loading state. */
sealed class NetworkResponse<T>(val data : T? = null, val message : String? = null){
    class Success<T>(data : T) : NetworkResponse<T>(data)
    class Error<T>(msg : String? , data : T? = null) : NetworkResponse<T>(data , msg)
    class Loading<T> : NetworkResponse<T>()
}
