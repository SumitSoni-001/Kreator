package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleCommentsLayoutBinding
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.utils.Constants
import com.squareup.picasso.Picasso

class CommentAdapter(private val context: Context) : ListAdapter<CommentDetails, CommentAdapter.CommentsViewHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(SampleCommentsLayoutBinding.inflate(LayoutInflater.from(context) , parent , false))
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = getItem(position)
        /** Load user profile photo */
//        Glide.with(context).load(Constants.userNameImage("Anonymous")).placeholder(R.drawable.user_placeholder).into(holder.binding.userImage)
        Picasso.get().load(Constants.userNameImage("Anonymous")).placeholder(R.drawable.user_placeholder).into(holder.binding.userImage)
//        holder.binding.name.text = item.user!!.name
        holder.binding.comment.text = item.content
//        holder.binding.time.text = item.postedOn
    }

    inner class CommentsViewHolder(val binding: SampleCommentsLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffUtil : ItemCallback<CommentDetails>() {
        override fun areItemsTheSame(oldItem: CommentDetails, newItem: CommentDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentDetails, newItem: CommentDetails): Boolean {
            return oldItem.id == newItem.id
        }
    }

}