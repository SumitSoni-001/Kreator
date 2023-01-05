package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentRequestEmailBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class RequestEmailFragment : Fragment() {

    private lateinit var binding: FragmentRequestEmailBinding
    private val authViewModel by viewModels<AuthViewModel>()
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var loader : KProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestEmailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        binding.btnNext.setOnClickListener {
            if (binding.etResetEmail.text.toString().isEmpty()) {
                binding.resetEmailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etResetEmail.text.toString()).matches())) {
                binding.resetEmailField.error = "Enter a valid email address"
            }else{
//                generateToken(binding.etResetEmail.text.toString())
                authViewModel.getUserByEmail(binding.etResetEmail.text.toString())
            }
        }
        binding.arrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rememberPassword.setOnClickListener {
            findNavController().popBackStack()
        }

        userObserver()
    }

    private fun userObserver() {
        authViewModel.authResponseData.observe(viewLifecycleOwner) {
            loader.dismiss()
            when (it) {
                is NetworkResponse.Success -> {
                    val response = it.data
                    Log.d("getUserByEmail",response.toString())
                    sessionManager.setUserId(response?.user?.id.toString())
                    sessionManager.setUserName(response?.user?.name.toString())
                    sessionManager.setEmail(response?.user?.email.toString())
                    sessionManager.setAbout(response?.user?.about.toString())
                    sessionManager.setProfilePic(response?.user?.userImage.toString())
                    val bundle = bundleOf("token" to "Bearer ${response?.token}")
                    findNavController().navigate(R.id.action_requestEmailFragment_to_resetPasswordFragment, bundle)
                }
                is NetworkResponse.Error -> {
                    Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
                }
            }
        }
    }

}