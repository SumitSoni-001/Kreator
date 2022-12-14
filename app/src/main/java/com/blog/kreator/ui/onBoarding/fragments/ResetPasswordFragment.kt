package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentResetPasswordBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private lateinit var _binding: FragmentResetPasswordBinding
    private val binding get() = _binding
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var loader: KProgressHUD
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = arguments?.getString("token").toString()

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setDimAmount(0.5f)

        binding.backToEmail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnResetPassword.setOnClickListener {
            if (binding.etNewPassword.text.toString().isEmpty()) {
                binding.newPasswordField.error = "Enter a strong password"
            } else if (binding.etNewPassword.text.toString().length < 8) {
                binding.newPasswordField.error = "Password must be of atleast 8 character."
            } else if (!binding.etNewPassword.text.toString()
                    .equals(binding.etConfirmPassword.text.toString(), false)
            ) {
                binding.confirmNewPasswordField.error = "Password not matching"
            } else {
                val password = binding.etNewPassword.text.toString()
                val updatedUserDetails: UserInput
                sessionManager.apply {
                    updatedUserDetails = UserInput(
                        name = getUserName(),
                        email = getEmail(),
                        password = password,
                        about = getAbout()
                    )
                }
                authViewModel.updateUser(
                    token,
                    sessionManager.getUserId()!!.toInt(),
                    updatedUserDetails
                )
            }
        }
        userObserver()
    }

    private fun userObserver() {
        authViewModel.userResponseData.observe(viewLifecycleOwner) {
//            binding.loading.cancelAnimation()
//            binding.loading.visibility = View.GONE
            loader.dismiss()
            when (it) {
                is NetworkResponse.Success -> {
                    token = ""
                    sessionManager.setUserId(it.data?.id.toString())
                    sessionManager.setUserName(it.data?.name.toString())
                    sessionManager.setEmail(it.data?.email.toString())
                    sessionManager.setAbout(it.data?.about.toString())
                    sessionManager.setProfilePic(it.data?.userImage.toString())
                    findNavController().navigate(R.id.action_resetPasswordFragment_to_passwordResetSuccessFragment)
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
//                    binding.loading.playAnimation()
//                    binding.loading.visibility = View.VISIBLE
                }
            }
        }
    }

}