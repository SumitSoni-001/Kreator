package com.blog.kreator.ui.profile.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentEditProfileBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val profilePic = registerForActivityResult(ActivityResultContracts.GetContent()){uri : Uri? ->
        if (uri != null){
            binding.userImg.setImageURI(uri)
            // Post the Image after creating api
        }
    }
    private val userViewModel by viewModels<AuthViewModel>()
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var loader: KProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Updating Details...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        sessionManager.apply {
            binding.etName.setText(getUserName())
            binding.etEmail.setText(getEmail())
            binding.etAbout.setText(getAbout())
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvAddPicture.setOnClickListener {
            profilePic.launch("image/*")
        }
        binding.btnSaveDetails.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val about = binding.etAbout.text.toString()

            if (name.isEmpty()) {
                binding.nameField.error = "Enter your name"
            } else if (binding.etEmail.text!!.isEmpty()) {
                binding.emailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches())
            ) {
                binding.emailField.error = "Enter correct Email"
            }else {
                val userModel = UserInput(name=name,email=email,about=about)
                it.hideKeyboard()
                userViewModel.updateUser(sessionManager.getToken().toString(), sessionManager.getUserId()!!.toInt(),userModel)
            }
        }
        userObserver()

    }

    private fun View.hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun userObserver() {
        userViewModel.userResponseData.observe(viewLifecycleOwner) {
            loader.dismiss()
            when (it) {
                is NetworkResponse.Success -> {
                    sessionManager.setUserName(it.data?.name.toString())
                    sessionManager.setEmail(it.data?.email.toString())
                    sessionManager.setAbout(it.data?.about.toString())
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    loader.show()
                }
            }
        }
    }

}