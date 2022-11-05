package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentRegisterBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var loader: KProgressHUD

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

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            if (binding.etName.text!!.isEmpty()) {
                binding.nameField.error = "Enter you name"
            } else if (binding.etEmail.text!!.isEmpty()) {
                binding.emailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                    .matches())
            ) {
                binding.emailField.error = "Enter correct Email"
            } else if (binding.etPassword.text!!.isEmpty()) {
                binding.passwordField.error = "Enter a strong password"
            } else if (!binding.termsServices.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "Please Agree to the Terms of Services",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val userModel = UserInput()
                userModel.name = binding.etName.text.toString()
                userModel.email = binding.etEmail.text.toString()
                userModel.password = binding.etPassword.text.toString()
                userModel.about = binding.etAbout.text.toString()

                authViewModel.registerUser(userModel)
            }
        }

        authObserver()

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

}