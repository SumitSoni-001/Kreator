package com.blog.kreator.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentViewPostBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.utils.Constants
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewPostFragment : Fragment() {

    private lateinit var binding: FragmentViewPostBinding
    private val postViewModel by viewModels<PostViewModel>()
    private var postId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPostBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getInt("id")!!

        postViewModel.getPostByPostId(postId)
        postObserver()

        binding.commentFAB.setOnClickListener {
            val bundle = bundleOf("id" to postId)
            findNavController().navigate(R.id.action_viewPostFragment_to_commentFragment , bundle)
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
        binding.postItemsLayout.visibility = View.VISIBLE
        binding.commentFAB.visibility = View.VISIBLE
        binding.shimmer.shimmerLayout.stopShimmerAnimation()
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResponse.Success -> {
                    val postData = it.data
                    binding.postTitle.text = postData?.postTitle
                    binding.postedOn.text = postData?.date
                    binding.postContent.text = postData?.content
                    Glide.with(requireContext()).load(Constants.downloadImage(postData?.image!!))
                        .placeholder(R.drawable.placeholder).into(binding.postImage)
                    binding.username.text = postData?.user?.name
                    val url = Constants.userNameImage(postData?.user?.name.toString())
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.user_placeholder)
                        .into(binding.userProfile)

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

}