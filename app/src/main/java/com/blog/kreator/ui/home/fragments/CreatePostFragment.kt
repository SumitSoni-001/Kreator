package com.blog.kreator.ui.home.fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.Transliterator.Position
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blog.kreator.MainActivity
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentCreatePostBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.models.PostInput
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.SessionManager
import com.github.irshulx.Editor
import com.github.irshulx.EditorListener
import com.github.irshulx.models.EditorTextStyle
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.annotations.Contract
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var editor: Editor
    private lateinit var postViewModel: PostViewModel
    lateinit var sessionManager: SessionManager
    private var coverImgUri : String = ""
    private lateinit var part : MultipartBody.Part

    private var catId : Int = 1     // Make default catId to "Testing" but don't show this category to user.
    private lateinit var categoryAdapter : ArrayAdapter<String>
    private var getCoverImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri : Uri? ->
        if (uri != null){
            coverImgUri = uri.toString()
            /** Acc. to android policy we can not access other apps data directly , so we have created a file in our application
             *  and stores the content of selected file into our file and then performing further operations with the file. */
            // 1) create a file in our App's directory
            val filesDir = requireContext().applicationContext.filesDir
            val file = File(filesDir , "image.png")
            // 2) copy the content of selected image in the file that we have created
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream!!.copyTo(outputStream)  // Now the content of selected image is copied to our file
            // 3) creating multipart
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            part = MultipartBody.Part.createFormData("image",file.name,requestBody)
        }else{
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
        }
    }
    private var getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
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
        binding = FragmentCreatePostBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        sessionManager = SessionManager(requireContext())
        postViewModel = ViewModelProvider(context as MainActivity)[PostViewModel::class.java]

        categoryAdapter = ArrayAdapter(requireContext(),R.layout.category_dropdown_menu_item,Constants.ALL_CATEGORIES)
        categoryAdapter.setDropDownViewResource(R.layout.category_dropdown_menu_item)
        binding.categorySpinner.adapter = categoryAdapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                Toast.makeText(requireActivity(),"Selected item : ${position+1} - ${Constants.ALL_CATEGORIES[position]}", Toast.LENGTH_SHORT).show()
                catId = position+1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        editor = binding.editor
        setUpEditor()

        binding.Back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.publish.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = editor.contentAsSerialized
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val postInput = PostInput(content, date, title)
            if (title.isNotEmpty() && content.isNotEmpty() && coverImgUri.isNotEmpty()) {
                Log.d("postInput", postInput.toString())
                postViewModel.createPost(sessionManager.getToken().toString(), sessionManager.getUserId()?.toInt()!!,catId, postInput)
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
        binding.actionInsertImage.setOnClickListener {
//            editor.openImagePicker()
            getImage.launch("image/*")
        }
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
        postViewModel.singlePostData.observe(viewLifecycleOwner, Observer {
            if (it != null){
//            binding.loadingAnime.visibility = View.GONE
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data
                        if (response?.image.equals("default.png")) {
                            postViewModel.uploadImage(sessionManager.getToken()!!, response?.postId!!, part)
                        } else {
                            binding.loadingAnime.visibility = View.GONE
                            binding.etTitle.setText("")
                            editor.clearAllContents()
                            coverImgUri = ""
                        }
                    }
                    is NetworkResponse.Error -> {
                        binding.loadingAnime.visibility = View.GONE
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResponse.Loading -> {
                        binding.loadingAnime.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

}