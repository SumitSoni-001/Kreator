package com.blog.kreator.ui.home.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blog.kreator.MainActivity
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentViewPostBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.SessionManager
import com.github.irshulx.Editor
import com.squareup.picasso.Picasso
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.set

//@AndroidEntryPoint
class ViewPostFragment : Fragment() {
    private lateinit var binding: FragmentViewPostBinding
//    private val postViewModel by viewModels<PostViewModel>()
    private lateinit var postViewModel : PostViewModel
    private var postId: Int = 0
    private lateinit var editor : Editor
//    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPostBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel = ViewModelProvider(context as MainActivity)[PostViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        postId = arguments?.getInt("id")!!
        editor = binding.postContent

//        binding.postContent.headingTypeface = getHeadingTypeFace()
//        binding.postContent.contentTypeface = getContentFace()
        editor.setEditorImageLayout(R.layout.editor_image_layout)

        postViewModel.getPostByPostId(postId)
        postObserver()

        binding.commentFAB.setOnClickListener {
            val bundle = bundleOf("id" to postId)
            findNavController().navigate(R.id.action_viewPostFragment_to_commentFragment, bundle)
        }
        binding.backToMain.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.share.setOnClickListener {
            // Share the post which contains the playstore url the app
        }
        binding.bookmarkPost.setOnClickListener {
            // Bookmark the post (Create the entity bookmark or add a field in user entity for bookmarked posts)
        }
        binding.more.setOnClickListener {
            // Load a menu
        }

    }

    private fun postObserver() {
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
            binding.postItemsLayout.visibility = View.VISIBLE
            binding.commentFAB.visibility = View.VISIBLE
            binding.shimmer.shimmerLayout.stopShimmerAnimation()
            when (it) {
                is NetworkResponse.Success -> {
                    val postData = it.data
//                    val deserializedContent = binding.postContent.getContentDeserialized(postData?.content)
                    val deserializedContent = editor.getContentDeserialized(sessionManager.getContent())
                    binding.postTitle.text = postData?.postTitle
                    binding.postedOn.text = postData?.date

                    /** binding.postContent.text = postData?.content */
                    editor.render(deserializedContent)

                    binding.username.text = postData?.user?.name
                    Picasso.get().load(Constants.downloadImage(postData?.image!!)).placeholder(R.drawable.placeholder).into(binding.postImage)
                    val url = Constants.userNameImage(postData?.user?.name.toString())
                    Picasso.get().load(url).placeholder(R.drawable.user_placeholder).into(binding.userProfile)
                }
                is NetworkResponse.Error -> {
                    binding.postItemsLayout.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResponse.Loading -> {
                    binding.shimmer.shimmerLayout.startShimmerAnimation()
                    binding.postItemsLayout.visibility = View.GONE
                    binding.commentFAB.visibility = View.GONE
                }
            }
        }
    }

    private  fun getHeadingTypeFace(): Map<Int, String>?{
        val typefaceMap: MutableMap<Int, String> = HashMap()
        typefaceMap[Typeface.NORMAL] = "fonts/poppins_semibold.ttf"
        typefaceMap[Typeface.BOLD] = "fonts/poppins_bold.ttf"
        typefaceMap[Typeface.ITALIC] = "fonts/poppins_semibold_italic.ttf"
        typefaceMap[Typeface.BOLD_ITALIC] = "fonts/poppins_bold_italic.ttf"
        return typefaceMap
    }

    private  fun getContentFace(): Map<Int, String>?{
        val typefaceMap: MutableMap<Int, String> = HashMap()
        typefaceMap[Typeface.NORMAL] = "fonts/poppins_medium.ttf"
        typefaceMap[Typeface.BOLD] = "fonts/poppins_bold.ttf"
        typefaceMap[Typeface.ITALIC] = "fonts/poppins_medium_italic.ttf"
        typefaceMap[Typeface.BOLD_ITALIC] = "fonts/poppins_semibold_italic.ttf"
        return typefaceMap
    }

}
