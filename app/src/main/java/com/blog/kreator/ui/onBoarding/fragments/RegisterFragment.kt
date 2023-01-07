package com.blog.kreator.ui.onBoarding.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentRegisterBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.GetIdToken
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
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
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  /** For making the button to come up along with keyboard. */
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

//        if (sessionManager.getVerifiedEmail()) {
//            Toasty.success(requireContext(), "Email Verified Successfully", Toasty.LENGTH_SHORT, true).show()
//        }
        binding.etEmail.setText(sessionManager.getEmail()?:"")

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setCancellable(false)
            .setDimAmount(0.5f)
//        CustomToast.initialize()

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvLogin.setOnClickListener {
            it.hideKeyboard()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            if (binding.etName.text.toString().trim().isEmpty()) {
                binding.nameField.error = "Enter your name"
            } else if (binding.etEmail.text.toString().trim().isEmpty()) {
                binding.emailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) /** Match email pattern */
            ) {
                binding.emailField.error = "Enter a valid email address"
            } else if (binding.etPassword.text.toString().trim().isEmpty()) {
                binding.passwordField.error = "Enter a strong password"
            } else if (!binding.termsServices.isChecked) {
                Toasty.info(requireContext(), "Please Agree to the Terms of Services", Toasty.LENGTH_SHORT, true).show()
            } else {

                name= binding.etName.text.toString().trim()
                email = binding.etEmail.text.toString().trim()
                password = binding.etPassword.text.toString().trim()
                about = binding.etAbout.text.toString().trim()

                val userModel = UserInput(name = name, email = email, password = password, about = about, isVerified = false)

                it.hideKeyboard()
                userModel.isVerified = true
                authViewModel.registerUser(userModel)
            }
        }

        authObserver()
    }

    /** Generate token if the user is signedIn (i.e If an Anonymous User is created)*/
    private fun generateToken(/*email: String*/) {
        if (signedIn) {
            val token = GetIdToken(object : GetIdToken.AuthToken {
                override fun getAuthIdToken(token: String) {
                    val authToken = "Bearer $token"
                }
            })
            token.getToken()
        } else {
            Toasty.error(requireContext(), "Error Connecting, Please try again later", Toasty.LENGTH_SHORT, true).show()
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
//                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
                }
            }
        })
    }

}