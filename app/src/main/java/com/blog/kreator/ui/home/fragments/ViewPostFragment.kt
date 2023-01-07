package com.blog.kreator.ui.home.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blog.kreator.MainActivity
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentViewPostBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.ui.profile.viewModels.BookmarkViewModel
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.blog.kreator.utils.SessionManager
import com.github.irshulx.Editor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlin.collections.set

/** Field Dependency Injection can not be applied here, because RichTextEditor do not support DI. */
//@AndroidEntryPoint
class ViewPostFragment : Fragment() {
    private var _binding: FragmentViewPostBinding? = null
    private val binding get() = _binding!!
//    private val postViewModel by viewModels<PostViewModel>()
    private lateinit var postViewModel : PostViewModel
    private lateinit var bookmarkViewModel : BookmarkViewModel
    private var bookmarkedPostsList: ArrayList<BookmarkResponse> = ArrayList()
    private var postId: Int = 0
    private var isBookmarked : Boolean = false
    private var bookmarkPosition = -1
//    private var bookmarkId = 0
    private lateinit var editor : Editor    /** WYSIWYG rEditor */
//    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPostBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel = ViewModelProvider(context as MainActivity)[PostViewModel::class.java]
        bookmarkViewModel = ViewModelProvider(context as MainActivity)[BookmarkViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        postId = arguments?.getInt("id")!!

        editor = binding.postContent
        editor.setEditorImageLayout(R.layout.editor_image_layout) /** Custom Image layout for Editor's images (* Currently not in use *) */

        postViewModel.getPostByPostId(sessionManager.getToken().toString(),postId)
        bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!)

        binding.commentFAB.setOnClickListener {
            val bundle = bundleOf("id" to postId)
            findNavController().navigate(R.id.action_viewPostFragment_to_commentFragment, bundle)
        }
        binding.backToMain.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.share.setOnClickListener {
            // Share the post which contains the play-store url the app
            /** Share the post with post Id and a dynamic link which navigates to Kreator and then inside mainFragment
             *  fetch the post using the postId and then popup the post like medium. */
        }
        binding.bookmarkPost.setOnClickListener {
            if (isBookmarked){
                bookmarkViewModel.deleteBookmark(sessionManager.getToken()!!, bookmarkedPostsList[bookmarkPosition].id!!)
//                bookmarkViewModel.deleteBookmark(sessionManager.getToken()!!, bookmarkId)
                binding.bookmarkPost.setImageResource(R.drawable.bookmark)
            }else{
                bookmarkViewModel.addBookmark(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!, postId)
                binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
            }
            isBookmarked = !isBookmarked
//            bookmarkObserver()
        }
        binding.more.setOnClickListener {
            // Load a menu
        }

        postObserver()
        bookmarkedPostsObserver()
        bookmarkObserver()
    }

    private fun postObserver() { /** Fetch Post data by postId */
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                binding.postItemsLayout.visibility = View.VISIBLE
                binding.commentFAB.visibility = View.VISIBLE
//            binding.shimmer.shimmerLayout.stopShimmerAnimation()
                binding.shimmer.shimmerLayout.visibility = View.GONE
                when (it) {
                    is NetworkResponse.Success -> {
                        val postData = it.data
                        Log.d("viewPostData", postData.toString())

//                    val deserializedContent = binding.postContent.getContentDeserialized(postData?.content)
                        val deserializedContent = editor.getContentDeserialized(postData?.content) /** Deserialize the editor serialized data from server */
                        /** binding.postContent.text = postData?.content */
                        editor.render(deserializedContent)
                        binding.postTitle.text = postData?.postTitle
                        binding.postedOn.text = FormatTime.getFormattedTime(postData?.date!!)
                        binding.username.text = postData.user?.name
                        Picasso.get().load(CustomImage.downloadImage(postData.image!!)).placeholder(R.drawable.placeholder).into(binding.postImage)
                        val profileUrl = CustomImage.downloadProfile(postData.user?.userImage , postData.user?.name.toString())
                        Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(binding.userProfile)
                    }
                    is NetworkResponse.Error -> {
                        binding.postItemsLayout.visibility = View.GONE
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                        Snackbar.make(binding.root,R.string.loading_snackbar,Snackbar.LENGTH_LONG).setAnimationMode(
                            BaseTransientBottomBar.ANIMATION_MODE_SLIDE).setBackgroundTint(Color.parseColor("#E5DEFF"))
                            .setActionTextColor(Color.parseColor("#2E296B")).setAction(R.string.tryAgain) {
                                val intent = Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS)
                                requireActivity().startActivity(intent)
                            }.show()
                    }
                    is NetworkResponse.Loading -> {
//                    binding.shimmer.shimmerLayout.startShimmerAnimation()
                        binding.shimmer.shimmerLayout.visibility = View.VISIBLE
                        binding.postItemsLayout.visibility = View.GONE
                        binding.commentFAB.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun bookmarkObserver(){ /** Add or Delete Bookmark */
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when (it) {
                    is NetworkResponse.Success -> {
                        if (it.data?.status == true) {
                            if (isBookmarked) {
                                Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT, true).show()
                                binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
                                bookmarkViewModel.getBookmarkByUser(
                                    sessionManager.getToken()!!,
                                    sessionManager.getUserId()?.toInt()!!
                                )
                            } else {
//                        if (it.data?.status == true){
                                Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT, true).show()
                                binding.bookmarkPost.setImageResource(R.drawable.bookmark)
                                bookmarkedPostsList.removeAt(bookmarkPosition)
//                        }
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {}
                }
            }
            }
    }

    private fun bookmarkedPostsObserver(){  /** Fetch bookmarks by userId */
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner){
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when (it) {
                    is NetworkResponse.Success -> {
                        if (it.data != null) {
                            bookmarkedPostsList.clear()
                            bookmarkedPostsList.addAll(it.data)
                            /** Check whether current loggedIn user has bookmarked this post or not */
                            bookmarkedPostsList.forEach { bookmarkResponse ->
                                if (postId == bookmarkResponse.post?.postId) {
                                    bookmarkPosition = bookmarkedPostsList.indexOf(bookmarkResponse)
                                    binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
                                    isBookmarked = true
//                                bookmarkId = bookmarkedPostsList[bookmarkPosition].id!!
                                }
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
