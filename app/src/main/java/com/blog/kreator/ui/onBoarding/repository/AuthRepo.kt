package com.blog.kreator.ui.onBoarding.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.RemoteService
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthRepo @Inject constructor(private val remoteService: RemoteService) {

    private val authResponseLiveData = MutableLiveData<NetworkResponse<AuthResponse>>()
    val authResponseData: LiveData<NetworkResponse<AuthResponse>>
        get() = authResponseLiveData


    suspend fun registerUser(userModel: UserInput) {
        authResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.registerUser(userModel)
            Log.d("Register", response.body().toString())
            handleResponse(response)

        } catch (e: SocketTimeoutException) {
            Log.e("Danger", "Server Error : ${e.localizedMessage}")
            authResponseLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

    suspend fun loginUser(loginDetails: LoginDetails) {
        authResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.login(loginDetails)
            Log.d("Login", response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("danger", "Server Error")
            authResponseLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

    private fun handleResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful && response.body() != null) {
            authResponseLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            authResponseLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            authResponseLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

}