package com.blog.kreator.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var postsList: ArrayList<PostDetails> = ArrayList()
    private var bookmarkedPostsList: ArrayList<BookmarkResponse> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private val postViewModel by viewModels<PostViewModel>()
    private val bookmarkViewModel by viewModels<BookmarkViewModel>()
    private var isBookmarked = false
    private var isDataLoaded = false
    private var bookmarkedPostPosition = -1
    private var postPosition = -1

    @Inject
    lateinit var sessionManager: SessionManager
    private var catId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryList.clear()
        categoryList.add("All")
        categoryList.addAll(sessionManager.getCategories())

        postsAdapter = PostsAdapter(requireContext(),bookmarkedPostsList)
        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.postsRcv.layoutManager = linearLayoutManager
        binding.postsRcv.setHasFixedSize(true)
        binding.postsRcv.adapter = postsAdapter

        categoryAdapter = CategoryAdapter(requireContext(), categoryList)
        binding.categoryRCV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRCV.setHasFixedSize(true)
        binding.categoryRCV.adapter = categoryAdapter

        postsAdapter.setOnItemClickListener(object : PostsAdapter.ItemClickListener {
            override fun onItemClick(position: Int, bookmarkPosition: Int) {
//                categoryList.clear()
                isBookmarked = bookmarkPosition != -1
                val bundle = bundleOf("id" to postsList[position].postId, "isBookmarked" to isBookmarked, "bookmarkPos" to bookmarkPosition)
                findNavController().navigate(R.id.action_mainFragment_to_viewPostFragment, bundle)
            }
            override fun onBookmarkClick(position: Int, bookmarkPosition: Int) {
                bookmarkedPostPosition = bookmarkPosition
                postPosition = position
                if (bookmarkPosition == -1){
                    isBookmarked = true
                    bookmarkViewModel.addBookmark(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!, postsList[position].postId!!)
                }else{
                    isBookmarked = false
                    bookmarkViewModel.deleteBookmark(sessionManager.getToken()!!, bookmarkedPostsList[bookmarkPosition].id!!)
                }
            }
        })
        categoryAdapter.setonItemClickListener(object : CategoryAdapter.ItemClickListener {
            override fun onItemClick(
                position: Int,
                categoryLayout: SampleCategoryLayoutBinding,
                tvCategoryList: ArrayList<TextView>
            ) {
                postsList.clear()
//                for (item in tvCategoryList){
//                    item.setBackgroundResource(R.drawable.category_default)
//                    item.setTextColor(Color.parseColor("#000000"))
//                }l̥
//                categoryLayout.category.setBackgroundResource(R.drawable.category_selected)
//                categoryLayout.category.setTextColor(Color.parseColor("#FFFFFF"))

                categoryLayout.category.isPressed = !categoryLayout.category.isPressed

                val currentCategory = categoryList[position]
                if (position != 0) {
                    for (item in 0 until Constants.ALL_CATEGORIES.size) {
                        val selectedCategory = currentCategory.equals(
                            Constants.ALL_CATEGORIES[item],
                            ignoreCase = true
                        )
                        if (selectedCategory) {
//                        Toast.makeText(requireContext(), "${item+1} :1 $currentCategory", Toast.LENGTH_SHORT).show()
                            catId = item + 1
                            postViewModel.getPostByCategory(sessionManager.getToken().toString(),item + 1)
                        }
                    }
                } else {
                    postViewModel.getAllPosts(sessionManager.getToken().toString())
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

        postViewModel.getAllPosts(sessionManager.getToken().toString())
        bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()!!.toInt())

        postObserver()
        bookmarkedPostsObserver()
        bookmarkObserver()
    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner, Observer {
            binding.errorAnime.visibility = View.INVISIBLE
            binding.noBlogFound.visibility = View.INVISIBLE
            binding.btnRetry.visibility = View.INVISIBLE
            binding.categoryRCV.visibility = View.VISIBLE
            binding.createBlogFAB.visibility = View.VISIBLE
            binding.postsRcv.visibility = View.VISIBLE
            binding.helloGroup.visibility = View.GONE
            binding.postsRcv.hideShimmerAdapter()
            when (it) {
                is NetworkResponse.Success -> {
                    binding.errorAnime.visibility = View.INVISIBLE
                    binding.btnRetry.visibility = View.INVISIBLE
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
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    binding.postsRcv.showShimmerAdapter()
                    binding.categoryRCV.visibility = View.GONE
                }
            }
        })
    }

    private fun bookmarkObserver(){
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner) {
            when(it){
                is NetworkResponse.Success -> {
//                    Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    if (!isBookmarked){ // delete bookmark
                        if (it.data?.status == true) {
                            bookmarkedPostsList.removeAt(bookmarkedPostPosition)
                            postsAdapter.notifyItemChanged(postPosition)
                        }
                    }else{  // Add Bookmark
                        if (it.data?.status == true){
                            // change bookmark icon to bookmarked
                            postsAdapter.notifyItemChanged(postPosition)
                        }
                    }
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {}
            }
        }
    }

    private fun bookmarkedPostsObserver(){
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner){
            when(it){
                is NetworkResponse.Success -> {
                    if (it.data != null){
                        bookmarkedPostsList.clear()
                        bookmarkedPostsList.addAll(it.data)
                    }
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {}
            }
        }
    }

}