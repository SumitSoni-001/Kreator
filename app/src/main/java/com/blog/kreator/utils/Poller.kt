package com.blog.kreator.utils

import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Poller {
    @ExperimentalCoroutinesApi
    fun poll(delay: Long, email:String): Flow<NetworkResponse<AuthResponse>>
    fun close()
}