package com.blog.kreator.ui.onBoarding.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.MainActivity
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentRegisterBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.GetIdToken
import com.blog.kreator.utils.SessionManager
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var loader: KProgressHUD
    private var signedIn = false
    private var name : String = ""
    private var email : String = ""
    private var password : String = ""
    private var about : String = ""
    private lateinit var auth : FirebaseAuth
    private lateinit var actionCodeSettings : ActionCodeSettings

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity()).window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        (requireActivity()).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

//        if (name!=null && email!=null&&password!=null){
//            binding.etName.setText(name)
//            binding.etEmail.setText(email)
//            binding.etPassword.setText(password)
//            binding.etAbout.setText(about)
//        }

//        if (sessionManager.getVerifiedEmail()) {
//            Toast.makeText(requireContext(), "Email Verified Successfully", Toast.LENGTH_SHORT).show()
//        }

        auth = FirebaseAuth.getInstance()
        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        actionCodeSettings = ActionCodeSettings.newBuilder() // URL you want to redirect back to. The domain (www.example.com) for this
                .setUrl("https://kreator.page.link/verifyemail") // This must be true
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                    "com.blog.kreator",
                    true,
                    null
                ).build()

/*
        authViewModel.firebaseAnonymousSignIn()
        authViewModel.anonymousLiveData.observe(viewLifecycleOwner){
            if (it != null){
                signedIn = true
            }
        }
*/

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvLogin.setOnClickListener {
            it.hideKeyboard()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            if (binding.etName.text!!.isEmpty()) {
                binding.nameField.error = "Enter your name"
            } else if (binding.etEmail.text!!.isEmpty()) {
                binding.emailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches())
            ) {
                binding.emailField.error = "Enter a valid email address"
            } else if (binding.etPassword.text!!.isEmpty()) {
                binding.passwordField.error = "Enter a strong password"
            } else if (!binding.termsServices.isChecked) {
                Toast.makeText(requireContext(), "Please Agree to the Terms of Services", Toast.LENGTH_SHORT).show()
            } else {

                name= binding.etName.text.toString()
                email = binding.etEmail.text.toString()
                password = binding.etPassword.text.toString()
                about = binding.etAbout.text.toString()

                val userModel = UserInput(name = name, email = email, password = password, about = about, isVerified = false)

                it.hideKeyboard()

                if (!sessionManager.getVerifiedEmail()) {
                    sendVerificationEmail(binding.etEmail.text.toString())
                } else {
                    userModel.isVerified = true
                    authViewModel.registerUser(userModel)
                }

                /*
                if (auth.currentUser == null) {
                    auth.createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("firebaseAuth", "createUserWithEmail:success")
                                val user: FirebaseUser = auth.currentUser!!
                                user.sendEmailVerification().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Verification email sent, Please verify your Email", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Email not sent", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    auth.signInWithEmailAndPassword(binding.etEmail.text.toString(),binding.etPassword.text.toString()).addOnCompleteListener {
                        if (auth.currentUser!!.isEmailVerified){
                            Toast.makeText(requireContext(), " User Registerer Successfully", Toast.LENGTH_SHORT).show()
//                        authViewModel.registerUser(userModel)
                        }else{
                            Log.d("verificationError", it.exception.toString())
                            Toast.makeText(requireContext(), "Verify your email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                */
            }
        }

        authObserver()

    }

    private fun generateToken(/*email: String*/) {
        if (signedIn) {
            val token = GetIdToken(object : GetIdToken.AuthToken {
                override fun getAuthIdToken(token: String) {
                    val authToken = "Bearer $token"
                    Toast.makeText(requireContext(), "$authToken", Toast.LENGTH_SHORT).show()
                }
            })
            token.getToken()
        } else {
            Toast.makeText(requireContext(), "Error Connecting, Please try again later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun View.hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun authObserver() {
        authViewModel.authResponseData.observe(viewLifecycleOwner, Observer {
            loader.dismiss()
            when (it) {
                is NetworkResponse.Success -> {
//                    delete firebase user ?
                    sessionManager.setToken(it.data?.token.toString())
                    sessionManager.setUserId(it.data?.user?.id.toString())
                    sessionManager.setUserName(it.data?.user?.name.toString())
                    sessionManager.setEmail(it.data?.user?.email.toString())
                    sessionManager.setAbout(it.data?.user?.about.toString())
                    sessionManager.setProfilePic(it.data?.user?.userImage?:"default.png")       // comment out
                    findNavController().navigate(R.id.action_registerFragment_to_onBoardingFragment)
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
                }
            }
        })
    }

    private fun sendVerificationEmail(email : String){
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    sessionManager.setEmail(binding.etEmail.text.toString())
                    sessionManager.setEmail(email)
                    Toast.makeText(requireContext(), "Verification Email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                it.localizedMessage?.let { it1 ->
                    Log.e("emailError", it1)
                }
            }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putString("name",name)
//        outState.putString("email",email)
//        outState.putString("password",password)
//        outState.putString("about",about)
//        super.onSaveInstanceState(outState)
//    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        name = savedInstanceState?.getString("name")!!
//        email = savedInstanceState?.getString("email")!!
//        password = savedInstanceState?.getString("password")!!
//        about = savedInstanceState?.getString("about")!!
//        super.onViewStateRestored(savedInstanceState)
//    }

}