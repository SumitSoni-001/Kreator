package com.blog.kreator.ui.home.fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blog.kreator.MainActivity
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentPostUpdateBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.models.PostInput
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.SessionManager
import com.github.irshulx.Editor
import com.github.irshulx.EditorListener
import com.github.irshulx.models.EditorTextStyle
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class UpdatePostFragment : Fragment() {
    private lateinit var binding: FragmentPostUpdateBinding
    private lateinit var editor: Editor
    private lateinit var postViewModel: PostViewModel
    private lateinit var sessionManager: SessionManager
    private var postId = 0
//    private var catId = 4
    private var coverImgUri: String = ""
    private var updated = false     /** True, if the content is changed by user */
    private lateinit var part: MultipartBody.Part
    private lateinit var loader : KProgressHUD
//    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var getCoverImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                coverImgUri = uri.toString()
                binding.coverImage.setImageURI(uri)
                /** Acc. to android policy we can not access other apps data directly , so we have created a file in our application
                 *  and stores the content of selected file into our file and then performing further operations with the file. */
                // 1) create a file in our App's directory
                val filesDir = requireContext().applicationContext.filesDir
                val file = File(filesDir, "image.png")
                // 2) copy the content of selected image in the file that we have created
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                inputStream!!.copyTo(outputStream)  // Now the content of selected image is copied to our file
                // 3) creating multipart
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                part = MultipartBody.Part.createFormData("image", file.name, requestBody)
            } else {
                Toasty.error(requireContext(), "File not found", Toasty.LENGTH_SHORT, true).show()
            }
        }
    private var getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                Toasty.success(requireContext(), "Success $bitmap", Toasty.LENGTH_SHORT, true).show()
                editor.insertImage(bitmap)
            } else {
                Toasty.error(requireContext(), "Error Occurred", Toasty.LENGTH_SHORT, true).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostUpdateBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        sessionManager = SessionManager(requireContext())
        postViewModel = ViewModelProvider(context as MainActivity)[PostViewModel::class.java]

        loader = KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Loading...")
            .setCancellable(false)
            .setDimAmount(0.5f)

        postId = arguments?.getInt("id")!!
        postViewModel.getPostByPostId(sessionManager.getToken().toString(),postId)

        editor = binding.updateEditor
        setUpEditor()

        binding.Back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.update.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = editor.contentAsSerialized
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val postInput = PostInput(content, date, title)
            if (title.isNotEmpty() && content.isNotEmpty()/* && coverImgUri.isNotEmpty()*/) {
                Log.d("postInput", postInput.toString())
                updated = true
                loader.setLabel("Publishing...")
                postViewModel.updatePost(sessionManager.getToken().toString(), postId, postInput)
            } else {
                Toasty.warning(requireContext(), "Add all the Details", Toasty.LENGTH_SHORT,true).show()
            }
        }
        binding.addCoverImage.setOnClickListener {
            getCoverImage.launch("image/*")
        }

        postObserver()
    }

    private fun setUpEditor() {
        binding.actionH1.setOnClickListener {
            editor.updateTextStyle(EditorTextStyle.H1)
        }
        binding.actionBold.setOnClickListener {
            editor.updateTextStyle(EditorTextStyle.BOLD)
        }
        binding.actionItalic.setOnClickListener {
            editor.updateTextStyle(EditorTextStyle.ITALIC)
        }
        binding.actionIndent.setOnClickListener {
            editor.updateTextStyle(EditorTextStyle.INDENT)
        }
        binding.actionBlockquote.setOnClickListener {
            editor.updateTextStyle(EditorTextStyle.BLOCKQUOTE)
        }
        binding.actionBulleted.setOnClickListener {
            editor.insertList(false)
        }
        binding.actionUnorderedNumbered.setOnClickListener {
            editor.insertList(true)
        }
//        binding.actionInsertImage.setOnClickListener {
////            editor.openImagePicker()
//            getImage.launch("image/*")
//        }
        binding.actionInsertLink.setOnClickListener {
            val linkDialog = Dialog(requireContext())
            linkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            linkDialog.setContentView(R.layout.insert_link_dialog)
            val etLink = linkDialog.findViewById<EditText>(R.id.etLink)
            linkDialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                linkDialog.cancel()
            }
            linkDialog.findViewById<Button>(R.id.btnInsert).setOnClickListener {
                if (etLink.text.toString().isNotEmpty()) {
                    editor.insertLink(etLink.text.toString())
                    editor.updateTextColor("#539bf5")
                    linkDialog.cancel()
                } else {
                    etLink.error = "Enter a Url"
                }
            }
            linkDialog.show()
            linkDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            linkDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            linkDialog.window?.setGravity(Gravity.CENTER)
        }
        binding.actionErase.setOnClickListener {
            editor.clearAllContents()
        }
        editor.editorListener = object : EditorListener {
            override fun onTextChanged(editText: EditText?, text: Editable?) {}
            override fun onUpload(image: Bitmap?, uuid: String?) {
                editor.onImageUploadComplete(image.toString(), uuid)
            }

            override fun onRenderMacro(
                name: String?,
                props: MutableMap<String, Any>?,
                index: Int
            ): View {
                TODO("Not yet implemented")
            }
        }
        editor.render()
    }

    private fun postObserver() {
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                loader.dismiss()
                if (it != null) {
                    when (it) {
                        is NetworkResponse.Success -> {
                            val response = it.data
                            if (updated) {  /** If post-content is updated successfully and the user has changed image, then update the image else update content only. */
                                if (coverImgUri.isNotEmpty()) {
                                    postViewModel.uploadImage(sessionManager.getToken()!!, response?.postId!!, part)
                                    coverImgUri = ""
//                                    Toasty.success(requireContext(), "Image updated successfully", Toasty.LENGTH_SHORT,true).show()
                                }else{
                                    Toasty.success(requireContext(), "Post updated successfully", Toasty.LENGTH_SHORT, true).show()
                                }
                            } else {    /** Load updated post data, if user has just changed the content */
                                if (!response?.image.equals("default.png") && response?.image != null) {
                                    Picasso.get().load(CustomImage.downloadImage(response?.image!!)).placeholder(R.drawable.placeholder).into(binding.coverImage)
                                } else {
                                    binding.coverImage.setImageResource(R.drawable.placeholder)
                                }
                                binding.etTitle.setText(response?.postTitle.toString())
//                        binding.categorySpinner.setPromptId(response?.category?.categoryId!! - 1)
                                editor.render(editor.getContentDeserialized(response?.content))
                            }
                        }
                        is NetworkResponse.Error -> {
                            Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                        }
                        is NetworkResponse.Loading -> {
                            loader.show()
//                            binding.loadingAnime.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

}