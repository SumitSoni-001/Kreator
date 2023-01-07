package com.blog.kreator.ui.onBoarding.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blog.kreator.di.RemoteService
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

/** Repo Flow:- User make api request, it will be handled in HandleResponse Method which checks whether the response is successful, or some api error Occurred
 * else a default error will be sent. And finally the response will be stored inside liveData which is sent to ViewModel.*/
class AuthRepo @Inject constructor(private val remoteService: RemoteService) {

    private val authResponseLiveData = MutableLiveData<NetworkResponse<AuthResponse>>()
    val authResponseData: LiveData<NetworkResponse<AuthResponse>>
    get() = authResponseLiveData

    private val userResponseLiveData = MutableLiveData<NetworkResponse<GetUserDetails>>()
    val userResponseData : LiveData<NetworkResponse<GetUserDetails>>
    get() = userResponseLiveData

//    private val getUserFlow = MutableStateFlow<NetworkResponse<AuthResponse>>(NetworkResponse.Loading())
//    val getUserData : StateFlow<NetworkResponse<AuthResponse>>
//    get() = getUserFlow

    suspend fun registerUser(userModel: UserInput) {
        authResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.registerUser(userModel)
            Log.d("Register", response.body().toString())
            handleResponse(response)

        } catch (e: Exception) {
            Log.e("Danger", "Server Error : ${e.localizedMessage}")
            authResponseLiveData.postValue(NetworkResponse.Error("Server Problem : ${e.localizedMessage}"))
        }
    }

    suspend fun loginUser(loginDetails: LoginDetails) {
        authResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.login(loginDetails)
            Log.d("Login", response.body().toString())
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("Danger", "Server Error")
            authResponseLiveData.postValue(NetworkResponse.Error("Server Problem : ${e.localizedMessage}"))
        }
    }

    suspend fun updateUser(token:String,userId:Int,userInput: UserInput){
        userResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.updateUser(token,userId,userInput)
            Log.d("updatedDetails" , response.body().toString())
            handleResponse2(response)
        }catch (e:Exception){
            authResponseLiveData.postValue(NetworkResponse.Error("Server Problem : ${e.localizedMessage}"))
        }
    }

    suspend fun getUserByEmail(email:String){
        userResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.getUserByEmail(email)
            Log.d("getUserByEmail" , response.body().toString())
            handleResponse(response)
        }catch (e:Exception){
            authResponseLiveData.postValue(NetworkResponse.Error("Server Problem : ${e.localizedMessage}"))
        }
    }

//    suspend fun getUserByEmail2(email:String){
//        val response = remoteService.getUserByEmail(email)
//        if (response.isSuccessful && response.body()!=null){
//            getUserFlow.emit(NetworkResponse.Success(response.body()!!))
//        }else if (response.errorBody() != null){
//            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
//            getUserFlow.emit(NetworkResponse.Error(errorObj.getString("message")))
//        }else{
//            getUserFlow.emit(NetworkResponse.Error("Something went wrong"))
//        }
//    }

    suspend fun uploadImage(token: String, userId : Int, profile: MultipartBody.Part) {
        userResponseLiveData.postValue(NetworkResponse.Loading())
        try {
            val response = remoteService.uploadProfile(token,profile, userId)
            Log.d("uploadProfile", response.body().toString())
            handleResponse2(response)
        } catch (e: Exception) {
            userResponseLiveData.postValue(NetworkResponse.Error("Server Error : ${e.localizedMessage}"))
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

    private fun handleResponse2(response: Response<GetUserDetails>) {
        if (response.isSuccessful && response.body() != null) {
            userResponseLiveData.postValue(NetworkResponse.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            userResponseLiveData.postValue(NetworkResponse.Error(errorObj.getString("message")))
        } else {
            userResponseLiveData.postValue(NetworkResponse.Error("Something went wrong"))
        }
    }

}