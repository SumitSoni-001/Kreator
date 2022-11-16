package com.blog.kreator.ui.profile.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentProfileBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.home.models.PostResponse
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.ui.profile.adapters.ArticlesAdapter
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var savedAdapter: ArticlesAdapter
    private lateinit var articleList: ArrayList<PostDetails>
    private var clickedPostID = 0
    private val postViewModel by viewModels<PostViewModel>()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleList = ArrayList()
        articlesAdapter = ArticlesAdapter(requireContext())
        articlesAdapter.setOnItemClickListener(object : ArticlesAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("id" to articleList[position].postId)
                findNavController().navigate(R.id.action_profileFragment_to_viewPostFragment, bundle)
            }
            override fun onMoreClick(position: Int, moreImg: ImageView) {
                clickedPostID = articleList[position].postId!!
                PopupMenu(requireContext(), moreImg).apply {
                    setOnMenuItemClickListener(this@ProfileFragment)
                    inflate(R.menu.articles_menu)
                    show()
                }
//                val menuHelper = MenuPopupHelper(context!!, (menu.menu as MenuBuilder?)!!, moreImg)
//                val menuHelper = MenuPopupHelper(context, menu.menu as MenuBuilder,moreImg)
//                menuHelper.setForceShowIcon(true)
//                menuHelper.show()
            }
        })

        binding.articlesRcv.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRcv.setHasFixedSize(true)
        binding.articlesRcv.adapter = articlesAdapter

        Picasso.get().load(Constants.userNameImage(sessionManager.getUserName().toString()))
            .placeholder(R.drawable.user_placeholder).into(binding.userImage)
        binding.username.text = sessionManager.getUserName()
        val kreatorUsername = sessionManager.getUserName()?.replace(" ", "")?.lowercase()
        binding.kreatorUsername.text = "@$kreatorUsername"

        binding.tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvArticle.setOnClickListener {
            binding.tvArticle.setTextColor(Color.parseColor("#04BB58"))
            binding.articleSlider.visibility = View.VISIBLE
            binding.tvSaved.setTextColor(Color.parseColor("#7D8492"))
            binding.savedSlider.visibility = View.INVISIBLE
            binding.tvNotfound.text = "You don't have any posts."
            postViewModel.getPostByUser(sessionManager.getUserId()!!.toInt())
        }
        binding.tvSaved.setOnClickListener {
            binding.tvSaved.setTextColor(Color.parseColor("#04BB58"))
            binding.savedSlider.visibility = View.VISIBLE
            binding.tvArticle.setTextColor(Color.parseColor("#7D8492"))
            binding.articleSlider.visibility = View.INVISIBLE
            binding.tvNotfound.text = "You don't have any saved posts."
        }

        postViewModel.getPostByUser(sessionManager.getUserId()!!.toInt())
        postObserver()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.edit -> {
                // Update the blog
                Toast.makeText(requireContext(), "Update Blog", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.delete -> {
                // delete the post
                Toast.makeText(requireContext(), "Delete Blog", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> false
        }
    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner) {
            binding.articlesRcv.hideShimmerAdapter()
            binding.notFound.visibility = View.INVISIBLE
            binding.tvNotfound.visibility = View.INVISIBLE
            when (it) {
                is NetworkResponse.Success -> {
                    val response = it.data?.postDto
                    if (response != null) {
                        articleList.clear()
                        articleList.addAll(response)
//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        articlesAdapter.submitList(articleList)
                    } else {
                        binding.notFound.visibility = View.VISIBLE
                        binding.tvNotfound.visibility = View.VISIBLE
                    }
                }
                is NetworkResponse.Error -> {
                    binding.notFound.visibility = View.VISIBLE
                    binding.tvNotfound.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    binding.articlesRcv.showShimmerAdapter()
                }
            }
        }
    }

}