package com.blog.kreator.ui.onBoarding.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentVerifyEmailBinding
import com.blog.kreator.utils.SessionManager
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class VerifyEmailFragment : Fragment() {

    private var _binding : FragmentVerifyEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var actionCodeSettings : ActionCodeSettings
    @Inject
    lateinit var sessionManager : SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      _binding = FragmentVerifyEmailBinding.inflate(inflater)

        if (sessionManager.getVerifiedEmail()){
            findNavController().navigate(R.id.action_verifyEmailFragment_to_registerFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window.navigationBarColor = resources.getColor(R.color.white)
        requireActivity().window.statusBarColor = resources.getColor(R.color.white)

        binding.policy.movementMethod = LinkMovementMethod.getInstance()
        actionCodeSettings = ActionCodeSettings.newBuilder()    /** Setting Up Dynamic Link for Password-less Authentication(or Email Verification) */
            .setUrl("https://kreator.page.link/verifyemail")
//                .setUrl("https://kreator.page.link")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.blog.kreator", true, null)
            .build()

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnContinue.setOnClickListener {
            if (binding.etYourEmail.text.toString().trim().isEmpty()) {
                binding.enterEmailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etYourEmail.text.toString().trim()).matches())) {
                binding.enterEmailField.error = "Enter a valid email address"
            }else{
                binding.etYourEmail.hideKeyboard()
                sendVerificationEmail(binding.etYourEmail.text.toString().trim())
            }
        }

    }

    private fun sendVerificationEmail(email : String){
        Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sessionManager.setEmail(email)
                    Toasty.info(requireContext(), "Verification Email sent", Toasty.LENGTH_SHORT, true).show()
                } else {
                    Toasty.error(requireContext(), "Something went wrong", Toasty.LENGTH_SHORT, true).show()
                }
            }.addOnFailureListener {
                it.localizedMessage?.let { it1 ->
                    Log.e("emailError", it1)
                }
            }
    }

    private fun View.hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null /** The Fragment is no longer binded to any Layout once destroyed */
    }

}