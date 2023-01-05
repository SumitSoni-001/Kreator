package com.blog.kreator.ui.profile.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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
import com.blog.kreator.ui.profile.adapters.SavedAdapter
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.ui.profile.viewModels.BookmarkViewModel
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import es.dmoral.toasty.Toasty.info
import okhttp3.internal.notify
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var articleList: ArrayList<PostDetails>
    private lateinit var bookmarkList : ArrayList<BookmarkResponse>
    private lateinit var savedAdapter: SavedAdapter
    private var clickedPostID = 0
    private var itemPosition = 0
    private var bookmarkPosition = 0
    private val postViewModel by viewModels<PostViewModel>()
    private val bookmarkViewModel by viewModels<BookmarkViewModel>()

    @Inject
    lateinit var sessionManager: SessionManager

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
                itemPosition = position
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

        bookmarkList = ArrayList()
        savedAdapter = SavedAdapter(requireContext())
        savedAdapter.setOnItemClickListener(object : SavedAdapter.ItemClickListener{
            override fun onItemClick(position: Int) {
                val bundle = bundleOf("id" to bookmarkList[position].post?.postId)
                findNavController().navigate(R.id.action_profileFragment_to_viewPostFragment, bundle)
            }
            override fun onBookmarkClick(position: Int) {
                bookmarkPosition = position
                bookmarkViewModel.deleteBookmark(sessionManager.getToken().toString(), bookmarkList[position].id!!)
            }
        })

        binding.articlesRcv.layoutManager = LinearLayoutManager(requireContext())
        binding.articlesRcv.setHasFixedSize(true)
        binding.articlesRcv.adapter = articlesAdapter

        val profileUrl = CustomImage.downloadProfile(sessionManager.getProfilePic() , sessionManager.getUserName().toString())
        Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(binding.userImage)
        binding.username.text = sessionManager.getUserName()
        val kreatorUsername = sessionManager.getUserName()?.replace(" ", "")?.lowercase()
        binding.kreatorUsername.text = "@$kreatorUsername"

        postViewModel.getPostByUser(sessionManager.getToken().toString(),sessionManager.getUserId()!!.toInt())

        binding.tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.goBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvArticle.setOnClickListener {
            binding.articlesRcv.adapter = articlesAdapter
            binding.tvArticle.setTextColor(Color.parseColor("#04BB58"))
            binding.articleSlider.visibility = View.VISIBLE
            binding.tvSaved.setTextColor(Color.parseColor("#7D8492"))
            binding.savedSlider.visibility = View.INVISIBLE
            binding.tvNotfound.text = "You don't have any posts."
            postViewModel.getPostByUser(sessionManager.getToken().toString(),sessionManager.getUserId()!!.toInt())
        }
        binding.tvSaved.setOnClickListener {
            binding.articlesRcv.adapter = savedAdapter
            binding.tvSaved.setTextColor(Color.parseColor("#04BB58"))
            binding.savedSlider.visibility = View.VISIBLE
            binding.tvArticle.setTextColor(Color.parseColor("#7D8492"))
            binding.articleSlider.visibility = View.INVISIBLE
            binding.tvNotfound.text = "You don't have any saved posts."
            bookmarkViewModel.getBookmarkByUser(sessionManager.getToken().toString(), sessionManager.getUserId()?.toInt()!!)
//            bookmarkViewModel.getBookmarkedPosts(sessionManager.getToken().toString(), sessionManager.getUserId()?.toInt()!!)
        }

        postObserver()
        deletePostObserver()
        bookmarkObserver()
        deleteBookmarkObserver()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.edit -> {
                val bundle = bundleOf("id" to articleList[itemPosition].postId)
                findNavController().navigate(R.id.action_profileFragment_to_updateFragment, bundle)
                return true
            }
            R.id.delete -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("Deleted post will not be recovered.Are you sure?")
                    .setCancelable(false)
                    .setNegativeButton("CANCEL") { dialog, which -> dialog?.dismiss() }
                    .setPositiveButton("DELETE") { dialog, which ->
                        postViewModel.deletePost(sessionManager.getToken()!!, clickedPostID)
                    }
                    .show()

                return true
            }
            else -> false
        }
    }

    private fun postObserver() {
        postViewModel.postData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                binding.articlesRcv.hideShimmerAdapter()
                binding.notFound.visibility = View.INVISIBLE
                binding.tvNotfound.visibility = View.INVISIBLE
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data?.postDto
                        if (response?.isNotEmpty() == true) {
                            articleList.clear()
                            articleList.addAll(response)
                            articlesAdapter.submitList(articleList)
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
                        binding.articlesRcv.showShimmerAdapter()
                    }
                }
            }
        }
    }

    private fun deletePostObserver() {
        postViewModel.deletePostData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data
                        if (response != null) {
                            Toasty.info(requireContext(), "${response.message}", Toasty.LENGTH_SHORT).show()
                            articleList.removeAt(itemPosition)
                            articlesAdapter.notifyItemRemoved(itemPosition)
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

    private fun bookmarkObserver(){
        bookmarkViewModel.bookmarkListData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                binding.articlesRcv.hideShimmerAdapter()
                binding.notFound.visibility = View.INVISIBLE
                binding.tvNotfound.visibility = View.INVISIBLE
                when(it){
                    is NetworkResponse.Success -> {
                        val bookmarkData = it.data
                        if (bookmarkData?.isNotEmpty() == true){
                            bookmarkList.clear()
                            bookmarkList.addAll(bookmarkData)
                            savedAdapter.submitList(bookmarkList)
                        }else {
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
                        binding.articlesRcv.hideShimmerAdapter()
                    }
                }
            }
        }
    }

    private fun deleteBookmarkObserver(){
        bookmarkViewModel.bookmarkData.observe(viewLifecycleOwner){
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when(it){
                    is NetworkResponse.Success -> {
//                    Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                        if (it.data?.status == true){
                            bookmarkList.removeAt(bookmarkPosition)
                            savedAdapter.notifyItemRemoved(bookmarkPosition)
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

}