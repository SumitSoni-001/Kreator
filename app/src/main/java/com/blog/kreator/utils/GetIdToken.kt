package com.blog.kreator.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class GetIdToken(tokenCallback: AuthToken) {
    private var tokenCallback: AuthToken? = tokenCallback

    fun getToken(): String? {
        var token: String? = null

        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result.token
                    tokenCallback?.getAuthIdToken(token.toString())
                    Log.d("token", "Bearer $token")
                } else {
                    Log.e("TokenError", task.exception?.localizedMessage.toString())
                }
            }

        return token
    }

    interface AuthToken{
        fun getAuthIdToken(token: String)
    }
}