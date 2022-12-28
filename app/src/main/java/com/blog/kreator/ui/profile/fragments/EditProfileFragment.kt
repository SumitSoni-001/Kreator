package com.blog.kreator.ui.profile.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.BuildConfig
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentEditProfileBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.onBoarding.models.UserInput
import com.blog.kreator.ui.onBoarding.viewModels.AuthViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.SessionManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val userViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var loader: KProgressHUD
    private var profileUri: String = ""
    private lateinit var part: MultipartBody.Part
    private val profilePic = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                profileUri = uri.toString()

                binding.btnCrop.visibility = View.VISIBLE
                binding.cropImageView.visibility = View.VISIBLE
                binding.editProfileGroup.visibility = View.INVISIBLE
                binding.parentLayout.setBackgroundColor(Color.parseColor("#2E2E2E"))

                binding.cropImageView.setImageUriAsync(uri)

//            binding.userImg.setImageURI(uri)
//
//            val filesDir = requireContext().applicationContext.filesDir
//            val file = File(filesDir, "profile.png")
//            val inputStream = requireContext().contentResolver.openInputStream(uri)
//            val outputStream = FileOutputStream(file)
//            inputStream!!.copyTo(outputStream)
//            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//            part = MultipartBody.Part.createFormData("profile", file.name, requestBody)
            } else {
                Toasty.error(requireContext(), "File not found", Toasty.LENGTH_SHORT,true).show()
            }
        }

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
            val profileUrl = CustomImage.downloadProfile(getProfilePic(), getUserName().toString())
            Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder)
                .into(binding.userImg)
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvAddPicture.setOnClickListener {
            profilePic.launch("image/*")
//            cropImage.launch(CropImageContractOptions(uri = null, cropImageOptions = CropImageOptions(imageSourceIncludeGallery = true,imageSourceIncludeCamera = false, allowRotation = true, backgroundColor = resources.getColor(R.color.black), guidelines = CropImageView.Guidelines.ON)))
        }
        binding.btnSaveDetails.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val about = binding.etAbout.text.toString()

            if (name.isEmpty()) {
                binding.nameField.error = "Enter your name"
            } else if (binding.etEmail.text!!.isEmpty()) {
                binding.emailField.error = "Enter your Email"
            } else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString())
                    .matches())
            ) {
                binding.emailField.error = "Enter correct Email"
            } else {
                val userModel = UserInput(name = name, email = email, about = about)
                it.hideKeyboard()
                userViewModel.updateUser(
                    sessionManager.getToken().toString(),
                    sessionManager.getUserId()!!.toInt(),
                    userModel
                )
                if (profileUri.isNotEmpty()) {
                    userViewModel.uploadProfile(
                        sessionManager.getToken().toString(),
                        sessionManager.getUserId()!!.toInt(),
                        part
                    )
//                    Toasty.success(requireContext(), "Profile Photo Updated", Toasty.LENGTH_SHORT, true).show()
                }
            }
        }
        binding.btnCrop.setOnClickListener {
            binding.btnCrop.visibility = View.INVISIBLE
            binding.cropImageView.visibility = View.INVISIBLE
            binding.editProfileGroup.visibility = View.VISIBLE
            binding.parentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
            val croppedImage = binding.cropImageView.getCroppedImage()
            handleCropImageResult(croppedImage)
        }

        userObserver()
    }

    private fun handleCropImageResult(croppedImage: Bitmap?) {
        val uri = bitmapToUri(croppedImage!!)
        binding.userImg.setImageURI(uri)

        val filesDir = requireContext().applicationContext.filesDir
        val file = File(filesDir, "profile.png")
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        part = MultipartBody.Part.createFormData("profile", file.name, requestBody)
    }

    private fun bitmapToUri(bitmapImage: Bitmap): Uri? {
        val result = WeakReference(Bitmap.createScaledBitmap(bitmapImage,bitmapImage.width,bitmapImage.height,false).copy(Bitmap.Config.RGB_565,true))
        val newBitmap = result.get()

        val imagesFolder = File(requireContext().cacheDir, "images")
        var uri: Uri? = null;
        try {
            imagesFolder.mkdirs();
            val file = File(imagesFolder, "cropped_image.jpg");
            val stream = FileOutputStream(file);
            newBitmap?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(requireContext().applicationContext, BuildConfig.APPLICATION_ID+".provider",file)

        } catch (e : FileNotFoundException) {
            e.printStackTrace()
        } catch (e : IOException) {
            e.printStackTrace()
        }

        return uri
    }

    private fun View.hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    sessionManager.setProfilePic(it.data?.userImage.toString())
                    Toasty.success(requireContext(), "Profile Updated Successfully", Toasty.LENGTH_SHORT, true).show()
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