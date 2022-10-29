package com.blog.kreator.ui.onBoarding.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.AuthResponse
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {

    val authResponseData : LiveData<NetworkResponse<AuthResponse>>
    get() = authRepo.authResponseData

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

}