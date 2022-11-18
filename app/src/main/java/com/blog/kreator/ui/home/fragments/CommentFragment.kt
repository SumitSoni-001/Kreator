package com.blog.kreator.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.databinding.FragmentCommentBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.adapters.CommentAdapter
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.ui.home.viewModels.CommentsViewModel
import com.blog.kreator.ui.home.viewModels.PostViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentFragment : Fragment() {

    private lateinit var binding: FragmentCommentBinding
    private val postViewModel by viewModels<PostViewModel>()
    private val commentViewModel by viewModels<CommentsViewModel>()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var progress : KProgressHUD
    @Inject
    lateinit var sessionManager: SessionManager
    private var postId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = KProgressHUD.create(requireActivity())
            .setCancellable(false)
            .setLabel("Please wait...")
            .setDimAmount(0.5f)

        postId = arguments?.getInt("id")!!

        commentAdapter = CommentAdapter(requireContext())
        binding.commentRCV.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRCV.setHasFixedSize(true)
        binding.commentRCV.adapter = commentAdapter

        binding.backToPost.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.send.setOnClickListener {
            val comment = binding.etComment.text.toString()
            if (comment.isNotEmpty()) {
                val commentDetails = CommentDetails(comment)
                commentViewModel.createComment(sessionManager.getToken().toString(),postId, sessionManager.getUserId()!!.toInt(), commentDetails)
            }
        }

        postViewModel.getPostByPostId(sessionManager.getToken().toString(),postId)
        commentObserver()
        commentObserver2()

    }

    private fun commentObserver() {
        postViewModel.singlePostData.observe(viewLifecycleOwner) {
        binding.commentRCV.hideShimmerAdapter()
            when (it) {
                is NetworkResponse.Success -> {
                    val postData = it.data
                    if (postData?.comments?.size!! > 0) {
                        binding.tvComment.text = "Comments (${postData?.comments?.size})"
                    }
                    commentAdapter.submitList(postData?.comments)
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    binding.commentRCV.showShimmerAdapter()
                }
            }
        }
    }

    private fun commentObserver2() {
        commentViewModel.commentData.observe(viewLifecycleOwner , Observer {
        progress.dismiss()
            when(it){
                is NetworkResponse.Success -> {
                    postViewModel.getPostByPostId(sessionManager.getToken().toString(),postId)
                    binding.etComment.setText("")
                }
                is NetworkResponse.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is NetworkResponse.Loading -> {
                    progress.show()
                }
            }
        })
    }

}