package com.blog.kreator.ui.profile.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentUserBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.ui.profile.adapters.UserAdapter
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.ui.profile.viewModels.BookmarkViewModel
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private var articleList: ArrayList<PostDetails> = ArrayList()
    private var bookmarkList : ArrayList<BookmarkResponse> = ArrayList()
    private var userId = -1
    private var postPosition = -1
    private var bookmarkPosition = -1
    private var isBookmarked = false
    private val postViewModel : PostViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt("userId")!!
        Picasso.get().load(arguments?.getString("profile")!!).placeholder(R.drawable.user_placeholder).into(binding.userImage)
        binding.username.text = arguments?.getString("username")
        binding.kreatorUsername.text = "@".plus(arguments?.getString("username")?.replace(" ", "")?.lowercase())
        binding.tvNotfound.text = arguments?.getString("username").plus(" don't have any posts")
        binding.about.text = arguments?.getString("about")

        postViewModel.getPostByUser(sessionManager.getToken()!!, userId)
        bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()!!.toInt())

        userAdapter = UserAdapter(requireContext(),bookmarkList)
        binding.userArticlesRCV.layoutManager = LinearLayoutManager(requireContext())
        binding.userArticlesRCV.setHasFixedSize(true)
        binding.userArticlesRCV.adapter = userAdapter
        userAdapter.setOnItemClickListener(object : UserAdapter.ItemClickListener{
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("id" to articleList[position].postId)
                findNavController().navigate(R.id.action_userFragment_to_viewPostFragment, bundle)
            }

            override fun onBookmarkClick(
                position: Int,
                bookmarkPosition: Int,
                bookmarkImg: ImageView
            ) {
                this@UserFragment.bookmarkPosition = bookmarkPosition
                postPosition = position
                if (this@UserFragment.bookmarkPosition == -1){
                    isBookmarked = true
                    bookmarkImg.setImageResource(R.drawable.bookmarked)
                    bookmarkViewModel.addBookmark(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!, articleList[position].postId!!)
                }else{
                    isBookmarked = false
                    bookmarkViewModel.deleteBookmark(sessionManager.getToken()!!, bookmarkList[this@UserFragment.bookmarkPosition].id!!)
                }
            }

        })

        binding.tvArticle.setOnClickListener {
            binding.articleSlider.visibility = View.VISIBLE
            binding.tvArticle.setTextColor(Color.parseColor("#04BB58"))
            binding.tvAbout.setTextColor(Color.parseColor("#7D8492"))
            binding.aboutSlider.visibility = View.INVISIBLE
            binding.about.visibility = View.GONE
            binding.userArticlesRCV.visibility = View.VISIBLE
            postViewModel.getPostByUser(sessionManager.getToken().toString(),userId)
        }
        binding.tvAbout.setOnClickListener {
            binding.aboutSlider.visibility = View.VISIBLE
            binding.tvAbout.setTextColor(Color.parseColor("#04BB58"))
            binding.tvArticle.setTextColor(Color.parseColor("#7D8492"))
            binding.articleSlider.visibility = View.INVISIBLE
            binding.about.visibility = View.VISIBLE
            binding.userArticlesRCV.visibility = View.GONE
            binding.notFound.visibility = View.GONE
            binding.tvNotfound.visibility = View.GONE
        }
        binding.goBack.setOnClickListener{
            findNavController().popBackStack()
        }

        postObserver()
        bookmarkObserver()
        bookmarkedPostsObserver()
    }

    private fun postObserver() {    /** Fetch all the posts by userId */
        postViewModel.postData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                binding.userArticlesRCV.hideShimmerAdapter()
                binding.notFound.visibility = View.INVISIBLE
                binding.tvNotfound.visibility = View.INVISIBLE
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data?.postDto
                        if (response?.isNotEmpty() == true) {
                            articleList.clear()
                            articleList.addAll(response)
                            userAdapter.submitList(articleList)
                        } else {
                            binding.notFound.visibility = View.VISIBLE
                            binding.tvNotfound.visibility = View.VISIBLE
                        }
                    }
                    is NetworkResponse.Error -> {
                        binding.notFound.visibility = View.VISIBLE
                        binding.tvNotfound.visibility = View.VISIBLE
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {
                        binding.userArticlesRCV.showShimmerAdapter()
                    }
                }
            }
        }
    }

    private fun bookmarkObserver(){/** Add or Delete Bookmark */
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                when (it) {
                    is NetworkResponse.Success -> {
                        if (!isBookmarked) { // delete bookmark
                            if (it.data?.status == true && bookmarkPosition != -1) {
                                Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT, true).show()
                                bookmarkList.removeAt(bookmarkPosition)
                                userAdapter.notifyItemChanged(postPosition)
                            }
                        } else {  // Add Bookmark
                            if (it.data?.status == true) {
                                Toasty.info(requireContext(), "${it.data.message}", Toasty.LENGTH_SHORT, true).show()
                                bookmarkViewModel.getBookmarkByUser(sessionManager.getToken()!!, sessionManager.getUserId()?.toInt()!!)
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

    private fun bookmarkedPostsObserver(){  /** Fetch Bookmarks by user Id */
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner){
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when(it){
                    is NetworkResponse.Success -> {
                        if (it.data != null){
                            bookmarkList.clear()
                            bookmarkList.addAll(it.data)
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