package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentWelcomeBinding
import com.blog.kreator.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requireActivity().window.navigationBarColor = resources.getColor(R.color.black)
        requireActivity().window.statusBarColor = resources.getColor(R.color.black)

        if (sessionManager.getToken() != null && sessionManager.getCategories() != null) {
            findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
        } else if (sessionManager.getToken() != null && sessionManager.getCategories() == null && sessionManager.getVerifiedEmail()) {
            findNavController().navigate(R.id.action_welcomeFragment_to_categoriesFragment)
        }

        binding.securityLinks.movementMethod = LinkMovementMethod.getInstance() /** Activate the textView links */

        binding.getStarted.setOnClickListener {
//            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
            findNavController().navigate(R.id.action_welcomeFragment_to_verifyEmailFragment)
        }
//        binding.haveAccount.setOnClickListener {
//            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
//        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        // Clear the flag while closing the fragment so that other fragments may not be affected.
        (requireActivity()).window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) /** Remove full screen Flag */
    }

}