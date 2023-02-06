package com.blog.kreator

import android.content.Intent
import android.content.res.Resources.Theme
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.blog.kreator.databinding.ActivityMainBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.CoroutinePoller
import com.blog.kreator.utils.CustomToast
import com.blog.kreator.utils.NetworkListener
import com.blog.kreator.utils.SessionManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val authViewModel : AuthViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager
    private var timer : CountDownTimer? = null
//    private lateinit var coroutinePoller: CoroutinePoller
//    private lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application.setTheme(R.style.Theme_Kreator)
//        setTheme(R.style.Theme_Kreator)
//        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        coroutinePoller = CoroutinePoller(userViewModel, Dispatchers.IO)

        if (sessionManager.getCategories() != null) {
            timer()
//            timer.start()
            userObserver()
        }

    }

    /** It will refresh the jwt token every 30 minutes */
    @OptIn(DelicateCoroutinesApi::class)
    private fun timer(){
        GlobalScope.launch(Dispatchers.Main){
            timer = object : CountDownTimer((30*60-5)*1000,1000){   // Generate new token after every 30min - 5sec
                override fun onTick(millisUntilFinished: Long) {
//                    Log.d("Timer","${millisUntilFinished.div(1000)}")
                }
                override fun onFinish() {
                    sessionManager.getEmail()?.let {
                        authViewModel.getUserByEmail(it)
                    }
                }
            }.start()
        }
    }

    private fun userObserver(){
        authViewModel.authResponseData.observe(this@MainActivity){
            when(it){
                is NetworkResponse.Success -> {
                    val token = it.data?.token
                    if (token != null) {
                        timer?.start()
                        sessionManager.setToken(token)
                    }
                }
                is NetworkResponse.Error -> {}
                is NetworkResponse.Loading -> {}
            }
        }
    }

  /*
  /** Making api call every 30 minutes. (Generate token using kotlin coroutines & flow)  */
    private fun userObserver2(){
        GlobalScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                userViewModel.userData.collect{
                    when(it){
                        is NetworkResponse.Success -> {
                            val token = it.data?.token
                            if (token != null){
                                Toast.makeText(this@SplashActivity, "Api Called", Toast.LENGTH_SHORT).show()
                                sessionManager.setToken(token)
                            }
                        }
                        is NetworkResponse.Error -> {
                            Toast.makeText(this@SplashActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                        }
                        is NetworkResponse.Loading -> {}
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun polling(){
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.getEmail()?.let {
                coroutinePoller.poll(30000,it).collect{
                    val token = it.data?.token
                    token?.let { it1 ->
                        sessionManager.setToken(it1)
                        runOnUiThread {
                            Toast.makeText(this@SplashActivity, "Token Generated\n$token", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
  */

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.handleDeepLink(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.let {
            it.cancel()
        }
    }
}