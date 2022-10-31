package com.blog.kreator.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.databinding.FragmentMainBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.adapters.PostsAdapter
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.viewModels.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var postsAdapter: PostsAdapter
    private val postViewModel by viewModels<PostViewModel>()
    private var postsList: ArrayList<PostDetails> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsAdapter = PostsAdapter(requireContext())

        binding.postsRcv.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRcv.setHasFixedSize(true)
        binding.postsRcv.adapter = postsAdapter

        postViewModel.getAllPosts()
        postObserver()

    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner, Observer {
            binding.postsRcv.hideShimmerAdapter()
            when (it) {
                is NetworkResponse.Success -> {
                    for (item in 0 until (it.data!!.postDto.size)) {
                        postsList.add(it.data.postDto[item])
                    }
                    postsAdapter.submitList(postsList)
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    binding.postsRcv.showShimmerAdapter()
                }
            }
        })
    }



}