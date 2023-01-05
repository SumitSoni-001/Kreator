package com.blog.kreator.utils

import android.content.Context
import android.util.Log
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.repository.AuthRepo
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class CoroutinePoller(
    private val authViewModel: AuthViewModel,
    private val dispatcher: CoroutineDispatcher,
//    private val context: Context
    ) /*: Poller */{

   /* @ExperimentalCoroutinesApi
    override fun poll(delay: Long, email:String): Flow<NetworkResponse<AuthResponse>> {
        return channelFlow {
            while (!isClosedForSend){
                authViewModel.getUserByEmail2(email)
                val data = authViewModel.userData.value
                Log.d("Poller", "Poller Running")
                delay(delay)
                send(data)
//                SessionManager(context).setToken(data.data?.token?:"")
            }
        }.flowOn(dispatcher)
    }

    override fun close() {
        Log.d("Poller", "Poller Cancelled")
        dispatcher.cancel()
    }*/

}