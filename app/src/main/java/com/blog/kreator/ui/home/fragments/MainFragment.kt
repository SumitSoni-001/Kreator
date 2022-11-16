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
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var postsList: ArrayList<PostDetails> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private val postViewModel by viewModels<PostViewModel>()

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
        categoryList.add("All")
        categoryList.addAll(sessionManager.getCategories())

        postsAdapter = PostsAdapter(requireContext())
        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.postsRcv.layoutManager = linearLayoutManager
        binding.postsRcv.setHasFixedSize(true)
        binding.postsRcv.adapter = postsAdapter

        categoryAdapter = CategoryAdapter(requireContext(), categoryList)
        binding.categoryRCV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRCV.setHasFixedSize(true)
        binding.categoryRCV.adapter = categoryAdapter

        postsAdapter.setOnItemClickListener(object : PostsAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                categoryList.clear()
                val bundle = bundleOf("id" to postsList[position].postId)
                findNavController().navigate(R.id.action_mainFragment_to_viewPostFragment, bundle)
            }

            override fun onBookmarkClick(position: Int) {

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
                            postViewModel.getPostByCategory(item + 1)
                        }
                    }
                } else {
                    postViewModel.getAllPosts()
                }
            }
        })
        binding.createBlogFAB.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_createPostFragment)
        }
        binding.btnRetry.setOnClickListener {
            if (catId == 0){
                postViewModel.getAllPosts()
            }else{
                postViewModel.getPostByCategory(catId)
            }
        }
        binding.menu.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
        }

        postViewModel.getAllPosts()
        postObserver()

    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner, Observer {
            binding.errorAnime.visibility = View.INVISIBLE
            binding.noBlogFound.visibility = View.INVISIBLE
            binding.btnRetry.visibility = View.INVISIBLE
            binding.categoryRCV.visibility = View.VISIBLE
            binding.createBlogFAB.visibility = View.VISIBLE
            binding.postsRcv.visibility = View.VISIBLE
            binding.postsRcv.hideShimmerAdapter()
            when (it) {
                is NetworkResponse.Success -> {
                    binding.errorAnime.visibility = View.INVISIBLE
                    binding.btnRetry.visibility = View.INVISIBLE
                    if (it.data?.postDto != null) {
                        postsList.clear()
                        for (item in 0 until (it.data!!.postDto.size)) {
                            postsList.add(it.data.postDto[item])
                        }
//                        Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                        postsAdapter.submitList(postsList)
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

}