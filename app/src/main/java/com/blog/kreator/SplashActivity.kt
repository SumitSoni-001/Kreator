package com.blog.kreator

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
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
    private lateinit var kreatorAnime : LottieAnimationView
    private var gone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        application.setTheme(R.style.Theme_Kreator)
//        setTheme(R.style.Theme_Kreator)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_splash)
sessionManager.setEmail("sumit123@dev.com")
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = resources.getColor(R.color.black)
        window.statusBarColor = resources.getColor(R.color.black)

        kreatorAnime = findViewById(R.id.kreatorAnime)

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                val deepLink: Uri?
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("deepLink", deepLink.toString())
                    FirebaseAuth.getInstance().signInWithEmailLink(sessionManager.getEmail()!!, deepLink.toString())
                        .addOnSuccessListener {
                            Log.e("Verification", "Verified success")
                            sessionManager.setVerifiedEmail(true)
                            gone = true
                            Toasty.success(this@SplashActivity, "Email Verified Successfully", Toast.LENGTH_SHORT, true).show()
                        }.addOnFailureListener { e ->
                            sessionManager.setVerifiedEmail(false)
                            Log.e("Verification", e.localizedMessage ?: "Something went wrong")
//                        }
                        }
                }
            }
        if (!gone){
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            },2000)
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