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
    private lateinit var editor : Editor
//    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPostBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel = ViewModelProvider(context as MainActivity)[PostViewModel::class.java]
        bookmarkViewModel = ViewModelProvider(context as MainActivity)[BookmarkViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        postId = arguments?.getInt("id")!!
//        isBookmarked = arguments?.getBoolean("isBookmarked")!!
//        bookmarkPosition = arguments?.getInt("bookmarkPos")!!

        editor = binding.postContent

//        if (isBookmarked){
//            binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
//        }else{
//            binding.bookmarkPost.setImageResource(R.drawable.bookmark)
//        }

//        binding.postContent.headingTypeface = getHeadingTypeFace()
//        binding.postContent.contentTypeface = getContentFace()
        editor.setEditorImageLayout(R.layout.editor_image_layout)

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

    private fun postObserver() {
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
//       postViewModel.singlePostData.observeOnceAfterInit(viewLifecycleOwner){
            binding.postItemsLayout.visibility = View.VISIBLE
            binding.commentFAB.visibility = View.VISIBLE
//            binding.shimmer.shimmerLayout.stopShimmerAnimation()
            binding.shimmer.shimmerLayout.visibility = View.GONE
            when (it) {
                is NetworkResponse.Success -> {
                    val postData = it.data
                    Log.d("viewPostData", postData.toString())
//                    val deserializedContent = binding.postContent.getContentDeserialized(postData?.content)
                    val deserializedContent = editor.getContentDeserialized(postData?.content)
                    binding.postTitle.text = postData?.postTitle
                    binding.postedOn.text = FormatTime.getFormattedTime(postData?.date!!)

                    /** binding.postContent.text = postData?.content */
                    editor.render(deserializedContent)

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

    private fun bookmarkObserver(){
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner) {
//        bookmarkViewModel.bookmarkData.observeOnceAfterInit(viewLifecycleOwner){
            when(it){
                is NetworkResponse.Success -> {
                    if (it.data?.status == true){
                        if (isBookmarked){
                            Toasty.info(requireContext(),"${it.data.message}", Toasty.LENGTH_SHORT,true).show()
                            binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
                            bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!)
                        }else{
//                        if (it.data?.status == true){
                            Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT,true).show()
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

    private fun bookmarkedPostsObserver(){
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner){
//        bookmarkViewModel.bookmarkListData.observeOnceAfterInit(viewLifecycleOwner){
            when(it){
                is NetworkResponse.Success -> {
                    if (it.data != null){
                        bookmarkedPostsList.clear()
                        bookmarkedPostsList.addAll(it.data)
                        bookmarkedPostsList.forEach { bookmarkResponse ->
                            if (postId == bookmarkResponse.post?.postId){
                                bookmarkPosition = bookmarkedPostsList.indexOf(bookmarkResponse)
                                binding.bookmarkPost.setImageResource(R.drawable.bookmarked)
                                isBookmarked = true
//                                bookmarkId = bookmarkedPostsList[bookmarkPosition].id!!
                            }
                        }
                    }
                }
                is NetworkResponse.Error -> {
                    Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_LONG, true).show()
                }
                is NetworkResponse.Loading -> {}
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

    override fun onDestroyView() {
        super.onDestroyView()
//        bookmarkViewModel.bookmarkData.removeObservers(this)
//        bookmarkViewModel.bookmarkListData.removeObservers(this)
//        postViewModel.singlePostData.removeObservers(this)
        _binding = null
    }

//    private fun <T> LiveData<T>.observeOnceAfterInit(owner: LifecycleOwner, observer: (T) -> Unit) {
//        var firstObservation = true
//
//        observe(owner, object : Observer<T> {
//            override fun onChanged(value: T) {
//                if (firstObservation) {
//                    firstObservation = false
//                } else {
//                    removeObserver(this)
//                    observer(value)
//                }
//            }
//        })
//    }

}
