package com.blog.kreator.ui.onBoarding.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentLoginBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.LoginDetails
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var loader : KProgressHUD

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.etEmail.setText(sessionManager.getEmail()?:"")
        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        binding.securityLinks.movementMethod = LinkMovementMethod.getInstance()
        binding.backImg.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.tvRegister.setOnClickListener {
            it.hideKeyboard()
            findNavController().popBackStack()
        }
        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.toString().trim().isEmpty()) {
                binding.emailField.error = "Enter registered email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches())){
                binding.emailField.error = "Enter a valid email address"
            }else if (binding.etPassword.text.toString().trim().isEmpty()) {
                binding.passwordField.error = "Enter correct password"
            } else {
                it.hideKeyboard()
                authViewModel.loginUser(LoginDetails(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim()))
            }
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_requestEmailFragment)
        }

        authObserver()
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
                    sessionManager.setToken(it.data?.token.toString())
                    sessionManager.setUserId(it.data?.user?.id.toString())
                    sessionManager.setUserName(it.data?.user?.name.toString())
                    sessionManager.setEmail(it.data?.user?.email.toString())
                    sessionManager.setAbout(it.data?.user?.about.toString())
                    sessionManager.setProfilePic(it.data?.user?.userImage?:"default.png")
                    sessionManager.setVerifiedEmail(it.data?.user?.isVerified?:false)
                    findNavController().navigate(R.id.action_loginFragment_to_onBoardingFragment2)
                }
                is NetworkResponse.Error -> {
                    Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_LONG, true).show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
                }
            }
        })
    }

}