package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentPasswordResetSuccessBinding

class PasswordResetSuccessFragment : Fragment() {

    private lateinit var binding : FragmentPasswordResetSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordResetSuccessBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOkay.setOnClickListener{
            findNavController().navigate(R.id.action_passwordResetSuccessFragment_to_loginFragment)
        }

    }

}