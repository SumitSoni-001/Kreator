package com.blog.kreator.ui.home.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentCommentBinding
import com.blog.kreator.di.NetworkResponse
import com.blog.kreator.ui.home.adapters.CommentAdapter
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.ui.home.viewModels.CommentsViewModel
import com.blog.kreator.utils.SessionManager
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CommentFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentCommentBinding
    private val commentViewModel by viewModels<CommentsViewModel>()
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentsList : ArrayList<CommentDetails>
    private lateinit var progress: KProgressHUD
    private var itemPosition = 0
    private var clickedCommentId = 0
    private var updatingComment = false

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

        commentsList = ArrayList()
        commentAdapter = CommentAdapter(requireContext())
        commentAdapter.setOnItemClickListener(object : CommentAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, moreOptions: View) {
                itemPosition = position
                clickedCommentId = commentsList[position].id!!
                /** Create PopUp menu */
                PopupMenu(requireContext(), moreOptions).apply {
                    setOnMenuItemClickListener(this@CommentFragment)
                    inflate(R.menu.articles_menu)
                    show()
                }
            }
        })
        binding.commentRCV.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRCV.setHasFixedSize(true)
        binding.commentRCV.adapter = commentAdapter

        binding.backToPost.setOnClickListener {
            it.hideKeyboard()
            findNavController().popBackStack()
        }
        binding.send.setOnClickListener {
            val comment = binding.etComment.text.toString()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            if (comment.isNotEmpty()) {
                val commentDetails = CommentDetails(content = comment, postedOn = date)
                if (updatingComment){
                    commentViewModel.updateComment(sessionManager.getToken().toString(),clickedCommentId, commentDetails)
                }else{
                    commentViewModel.createComment(sessionManager.getToken().toString(), postId, sessionManager.getUserId()!!.toInt(), commentDetails)
                }
            }
        }

        commentViewModel.getCommentByPostId(sessionManager.getToken().toString(), postId)
        commentObserver()
        commentObserver2()
        deleteCommentObserver()
    }

    private fun commentObserver() { /** Fetching comments by postId */
        commentViewModel.commentByPostData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                binding.noComments.visibility = View.GONE
                binding.commentRCV.hideShimmerAdapter()
                when (it) {
                    is NetworkResponse.Success -> {
                        val commentData = it.data
                        if (commentData != null){
                            if (commentData.size > 0) {
                                binding.tvComment.text = "Comments (${commentData?.size})"
                            }else{
                                binding.noComments.visibility = View.VISIBLE
                            }
                            commentsList.clear()
                            commentsList.addAll(commentData)
//                        commentAdapter.submitList(commentData)
                            commentAdapter.submitList(commentsList)
                        }
                    }
                    is NetworkResponse.Error -> {
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {
                        binding.commentRCV.showShimmerAdapter()
                    }
                }
            }
        }
    }

    private fun commentObserver2() { /** Adding or Updating Comment */
        commentViewModel.commentData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                progress.dismiss()
                when (it) {
                    is NetworkResponse.Success -> {
//                    commentViewModel.getCommentByPostId(sessionManager.getToken().toString(), postId)
                        if (!updatingComment){ /** New Comment */
                            commentsList.add(it.data!!)
                            commentAdapter.notifyItemInserted(it.data.id!!)
                        }
                        /** If comment is updated */
                        commentAdapter.notifyItemChanged(itemPosition)
                        binding.etComment.setText("")
                        if (commentsList.size > 0){
                            binding.noComments.visibility = View.GONE
                            binding.tvComment.text = "Comments (${commentsList.size})"
                        }
                    }
                    is NetworkResponse.Error -> {
                        Toasty.error(requireContext(), "${it.message}", Toasty.LENGTH_SHORT, true).show()
                    }
                    is NetworkResponse.Loading -> {
//                    progress.show()
                    }
                }
            }
        }
    }

    private fun deleteCommentObserver() {   /** Delete Comment */
        commentViewModel.deleteCommentData.observe(viewLifecycleOwner) {
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED){
                when (it) {
                    is NetworkResponse.Success -> {
                        val response = it.data
                        if (response?.status == true) {
                            Toasty.info(requireContext(), "${response.message}", Toasty.LENGTH_SHORT,true).show()
                            commentsList.removeAt(itemPosition)
                            commentAdapter.notifyItemRemoved(itemPosition)
                            binding.tvComment.text = "Comments (${commentsList.size})"
                            if (commentsList.size == 0){
                                binding.tvComment.text = "Comments"
                                binding.noComments.visibility = View.VISIBLE
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.edit -> {
                updatingComment = true
                binding.etComment.requestFocus()  /** Request focus on EditText and open keyboard */
                showKeyboard()
//                binding.etComment.setText("")
                binding.etComment.setText(commentsList[itemPosition].content.toString())
                return true
            }
            R.id.delete -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("Deleted comment will not be recovered.Are you sure?")
                    .setCancelable(false)
                    .setNegativeButton("CANCEL") { dialog, which -> dialog?.dismiss() }
                    .setPositiveButton("DELETE") { dialog, which ->
                        commentViewModel.deleteComment(sessionManager.getToken()!! , clickedCommentId)
                    }
                    .show()

                return true
            }
            else -> false
        }
    }

    private fun showKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun View.hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}