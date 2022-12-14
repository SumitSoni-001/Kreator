package com.blog.kreator.ui.onBoarding.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.repository.AuthRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {

    val authResponseData : LiveData<NetworkResponse<AuthResponse>>
    get() = authRepo.authResponseData

    val userResponseData get() = authRepo.userResponseData

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var anonymousLiveData = MutableLiveData<FirebaseUser>()

    fun firebaseAnonymousSignIn() {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Anonymous", "signInAnonymously:success")
                    val firebaseUser = task.result.user
                    if (firebaseUser != null) {
                        anonymousLiveData.value = firebaseUser
                    }
                } else {
                    Log.w("Anonymous", "signInAnonymously:failure", task.exception)
                }
            }
    }

    fun registerUser(userModel: UserInput) {
        viewModelScope.launch {
            authRepo.registerUser(userModel)
        }
    }

    fun loginUser(loginDetails: LoginDetails) {
        viewModelScope.launch {
            authRepo.loginUser(loginDetails)
        }
    }

    fun updateUser(token:String,userId:Int,userInput: UserInput){
        viewModelScope.launch {
            authRepo.updateUser(token,userId,userInput)
        }
    }

    fun getUserByEmail(email:String){
        viewModelScope.launch {
            authRepo.getUserByEmail(email)
        }
    }

    fun uploadProfile(token:String,userId:Int,profile:MultipartBody.Part){
        viewModelScope.launch {
            authRepo.uploadImage(token,userId,profile)
        }
    }

}