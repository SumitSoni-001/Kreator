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
import com.squareup.picasso.Picasso
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
    private var catId = 4
    private var coverImgUri: String = ""
    private var updated = false
    private lateinit var part: MultipartBody.Part
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
                Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
            }
        }
    private var getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                Toast.makeText(requireContext(), "Success $bitmap", Toast.LENGTH_SHORT).show()
                editor.insertImage(bitmap)
            } else {
                Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()
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
                postViewModel.updatePost(sessionManager.getToken().toString(), postId, postInput)
            } else {
                Toast.makeText(requireContext(), "Add all the Details", Toast.LENGTH_SHORT).show()
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
            binding.loadingAnime.visibility = View.GONE
            if (it != null) {
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data
                        if (updated) {
                            if (coverImgUri.isNotEmpty()) {
                                postViewModel.uploadImage(sessionManager.getToken()!!, response?.postId!!, part)
                                Toast.makeText(requireContext(), "Image updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            binding.loadingAnime.visibility = View.GONE
                            Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
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
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResponse.Loading -> {
                        binding.loadingAnime.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}