package com.blog.kreator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.blog.kreator.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                val deepLink: Uri?
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("deepLink", deepLink.toString())
                    auth.signInWithEmailLink(sessionManager.getEmail()!!, deepLink.toString())
                        .addOnSuccessListener {
                            Log.e("Verification", "Verified success")
                            sessionManager.setVerifiedEmail(true)
                            Toast.makeText(this, "Email Verified Successfully", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            sessionManager.setVerifiedEmail(false)
                            Log.e("Verification", e.localizedMessage ?: "Something went wrong")
//                        }
                        }

                }

            }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.handleDeepLink(intent)
    }

//    override fun onBackPressed() {
//        val count = supportFragmentManager.backStackEntryCount
//
//        if (count == 0) {
//            super.onBackPressed()
//            //additional code
//        } else {
//            supportFragmentManager.popBackStack()
//        }
//    }

}