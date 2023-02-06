package com.blog.kreator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieAnimationView
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.CustomToast
import com.blog.kreator.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager
    private val authViewModel : AuthViewModel by viewModels()
    private lateinit var kreatorAnime : LottieAnimationView
    private var gone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.navigationBarColor = resources.getColor(R.color.black)
        window.statusBarColor = resources.getColor(R.color.black)

        kreatorAnime = findViewById(R.id.kreatorAnime)
        CustomToast.initialize()    /** Initializing Toasty */

        /** Getting Dynamic Link for emailVerification. This code will execute when the user navigate back to the app
         * after clicking on email verification link in Mail app(Gmail, etc) */
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                val deepLink: Uri?
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("deepLink", deepLink.toString())

                    deepLink?.let { uri ->
                        if (uri.toString().contains("post_id")) {
                            val sharedPostId = uri.getQueryParameter("post_id")
                            sharedPostId?.let {
//                                Toasty.info(this@SplashActivity, "$sharedPostId", Toasty.LENGTH_SHORT).show()
                                sessionManager.setSharedPost(it.toInt())
                            }
                        } else{
                            /** SignIn the user with the deep link which we got through dynamic link. */
                            FirebaseAuth.getInstance().signInWithEmailLink(sessionManager.getEmail()!!, deepLink.toString())
                                .addOnSuccessListener {
                                    Log.e("Verification", "Verified success")
                                    sessionManager.setVerifiedEmail(true)
                                    gone = true
                                    Toasty.success(this@SplashActivity, "Email Verified Successfully", Toast.LENGTH_SHORT, true).show()
                                }.addOnFailureListener { e ->
                                    sessionManager.setVerifiedEmail(false)
                                    Log.e("Verification", e.localizedMessage ?: "Something went wrong")
                                }
                        }
                    }

                }
            }

        if (!gone){ /** Execute if the user is not navigated to MainActivity through deep link code */
            if (sessionManager.getCategories() != null){   /** The token will be generated only if the user has signedIn or Registered */
                sessionManager.getEmail().let {
                    authViewModel.getUserByEmail(it!!)  /** Generate Token */
                    userObserver()
                }
            }
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            },2000)
        }
    }

    private fun userObserver(){
        authViewModel.authResponseData.observe(this@SplashActivity){
            when(it){
                is NetworkResponse.Success -> {
                    val token = it.data?.token
                    if (token != null) {
                        sessionManager.setToken(token)
//                        Toast.makeText(this@SplashActivity, "New Intent", Toast.LENGTH_SHORT).show()
                    }
                }
                is NetworkResponse.Error -> {}
                is NetworkResponse.Loading -> {}
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        kreatorAnime.cancelAnimation()
        val myIntent = Intent(this@SplashActivity , MainActivity::class.java)
        startActivity(myIntent)
        finish()
    }

}