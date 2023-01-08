package com.blog.kreator.ui.home.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentMainBinding
import com.blog.kreator.databinding.SampleCategoryLayoutBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.adapters.CategoryAdapter
import com.blog.kreator.ui.home.adapters.PostsAdapter
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.ui.profile.viewModels.BookmarkViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.NetworkListener
import com.blog.kreator.utils.SessionManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var postsList: ArrayList<PostDetails> = ArrayList()
    private var bookmarkedPostsList: ArrayList<BookmarkResponse> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private val postViewModel: PostViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private var isBookmarked = false
    private var isDataLoaded = false    // Check whether the api is called or not
    private var bookmarkedPostPosition = -1
    private var postPosition = -1
    private var catId: Int = 0  // Default Category is "ALL"
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var networkListener: NetworkListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor = resources.getColor(R.color.white)
        requireActivity().window.statusBarColor = resources.getColor(R.color.white)

        categoryList.clear()
        categoryList.add("All")
        categoryList.addAll(sessionManager.getCategories()!!)

        /** Accessing network state */
        lifecycleScope.launchWhenCreated {
            val snackbar = Snackbar.make(binding.root,R.string.noConnection,Snackbar.LENGTH_INDEFINITE).setAnimationMode(
                BaseTransientBottomBar.ANIMATION_MODE_SLIDE).setBackgroundTint(Color.parseColor("#E5DEFF"))
                .setActionTextColor(Color.parseColor("#2E296B")).setAction(R.string.tryAgain) {
                val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                requireActivity().startActivity(intent)
            }
            networkListener.isConnected.collectLatest {
                if (it){
                    snackbar.dismiss()
                }else{
                    snackbar.show()
                }
            }
        }

        postViewModel.getAllPosts(sessionManager.getToken().toString())
        bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()!!.toInt())

        postsAdapter = PostsAdapter(requireContext(),bookmarkedPostsList)
        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {  /** Recycler scroll disabled as the layout parent is scrollView. */
            override fun canScrollVertically(): Boolean = false
        }
        binding.postsRcv.layoutManager = linearLayoutManager
        binding.postsRcv.setHasFixedSize(true)
        binding.postsRcv.adapter = postsAdapter
        postsAdapter.setOnItemClickListener(object : PostsAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
//                categoryList.clear()
                val bundle = bundleOf("id" to postsList[position].postId)
                findNavController().navigate(R.id.action_mainFragment_to_viewPostFragment, bundle)
            }
            override fun onBookmarkClick(position: Int, bookmarkPosition: Int, bookmarkImg: ImageView) {
                bookmarkedPostPosition = bookmarkPosition
                postPosition = position
                if (bookmarkPosition == -1){
                    isBookmarked = true
                    bookmarkImg.setImageResource(R.drawable.bookmarked)
                    bookmarkViewModel.addBookmark(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!, postsList[position].postId!!)
                }else{
                    isBookmarked = false
//                    bookmarkImg.setImageResource(R.drawable.bookmark)
                    bookmarkViewModel.deleteBookmark(sessionManager.getToken()!!, bookmarkedPostsList[bookmarkPosition].id!!)
                }
            }
        })

        categoryAdapter = CategoryAdapter(requireContext(), categoryList)
        binding.categoryRCV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRCV.setHasFixedSize(true)
        binding.categoryRCV.adapter = categoryAdapter
        categoryAdapter.setonItemClickListener(object : CategoryAdapter.ItemClickListener {
            override fun onItemClick(
                position: Int,
                categoryLayout: SampleCategoryLayoutBinding,
                tvCategoryList: ArrayList<TextView>
            ) {
                postsList.clear()
                for (item in tvCategoryList){
                    item.setBackgroundResource(R.drawable.category_default)
                    item.setTextColor(Color.parseColor("#000000"))
                }
                categoryLayout.tvCategory.setBackgroundResource(R.drawable.category_selected)
                categoryLayout.tvCategory.setTextColor(Color.parseColor("#FFFFFF"))

                val currentCategory = categoryList[position]
                bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()!!.toInt())
                if (position != 0) {
                    for (item in 0 until Constants.ALL_CATEGORIES.size) {
                        val selectedCategory = currentCategory.equals(Constants.ALL_CATEGORIES[item], ignoreCase = true)    /** Compare selected category with the saved categoryList */
                        if (selectedCategory) {
//                        Toast.makeText(requireContext(), "${item+1} :1 $currentCategory", Toast.LENGTH_SHORT).show()
                            catId = item + 1
                            postViewModel.getPostByCategory(sessionManager.getToken().toString(),item + 1)
                        }
                    }
                } else {
                    postViewModel.getAllPosts(sessionManager.getToken().toString()) /** As there's no category as ALL on server, This is manually added at position 0 */
                }
            }
        })

        binding.createBlogFAB.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_createPostFragment)
        }
        binding.btnRetry.setOnClickListener {
            if (catId == 0){
                postViewModel.getAllPosts(sessionManager.getToken().toString())
            }else{
                postViewModel.getPostByCategory(sessionManager.getToken().toString(),catId)
            }
        }
        binding.menu.setOnClickListener {
            if (isDataLoaded) {
                findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
            }
        }

        postObserver()
        bookmarkObserver()
        bookmarkedPostsObserver()
    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner, Observer {
            /** It helps to overcome the observer bug (The observer is called automatically next time if once called) and create new observer everyTime. */
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
//                binding.errorAnime.visibility = View.GONE
                binding.noBlogFound.visibility = View.GONE
                binding.btnRetry.visibility = View.GONE
//            binding.categoryRCV.visibility = View.VISIBLE
                binding.createBlogFAB.visibility = View.VISIBLE
                binding.postsRcv.visibility = View.VISIBLE
                binding.helloGroup.visibility = View.GONE
                binding.postsRcv.hideShimmerAdapter()
                when (it) {
                    is NetworkResponse.Success -> {
                        binding.categoryRCV.visibility = View.VISIBLE
//                        binding.errorAnime.visibility = View.GONE
                        binding.btnRetry.visibility = View.GONE
                        binding.helloGroup.visibility = View.VISIBLE
                        binding.tvName.text = sessionManager.getUserName().toString()
                        val profileUrl = CustomImage.downloadProfile(sessionManager.getProfilePic() , sessionManager.getUserName().toString())
                        Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(binding.menu)
                        if (it.data?.postDto != null && it.data.postDto.isNotEmpty()) {
                            postsList.clear()
                            postsList.addAll(it.data.postDto)
//                        for (item in 0 until (it.data!!.postDto.size)) {
//                            postsList.add(it.data.postDto[item])
//                        }
                            postsAdapter.submitList(postsList)
                            isDataLoaded=true
                        } else {
                            binding.noBlogFound.visibility = View.VISIBLE
                            binding.btnRetry.visibility = View.VISIBLE
                        }
                    }
                    is NetworkResponse.Error -> {
//                    binding.categoryRCV.visibility = View.GONE
                        binding.postsRcv.visibility = View.GONE
                        binding.createBlogFAB.visibility = View.GONE
                        binding.noBlogFound.visibility = View.VISIBLE
                        binding.btnRetry.visibility = View.VISIBLE
//                    Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {
                        binding.postsRcv.showShimmerAdapter()
//                    binding.categoryRCV.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun bookmarkObserver(){/** Add or Delete Bookmark */
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when (it) {
                    is NetworkResponse.Success -> {
                        if (!isBookmarked) { // delete bookmark
                            if (it.data?.status == true && bookmarkedPostPosition != -1) {
                                Toasty.info(requireContext(), "${it.data?.message}", Toasty.LENGTH_SHORT, true).show()
                                bookmarkedPostsList.removeAt(bookmarkedPostPosition)
                                postsAdapter.notifyItemChanged(postPosition)
                            }
                        } else {  // Add Bookmark
                            if (it.data?.status == true) {
                                Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT, true).show()
                                bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!)
//                            postsAdapter.notifyItemChanged(postPosition)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {}
                }
            }
        })
    }

    private fun bookmarkedPostsObserver(){  /** Fetch Bookmarks by user Id */
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner){
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when(it){
                    is NetworkResponse.Success -> {
                        if (it.data != null){
                            bookmarkedPostsList.clear()
                            bookmarkedPostsList.addAll(it.data)
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