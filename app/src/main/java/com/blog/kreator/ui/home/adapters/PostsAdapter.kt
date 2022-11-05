package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SamplePostsRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.utils.Constants
import com.bumptech.glide.Glide
import java.util.Random

class PostsAdapter(private val context: Context) : ListAdapter<PostDetails, PostsAdapter.PostsViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int)
        fun onBookmarkClick(position : Int)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(SamplePostsRcvBinding.inflate(LayoutInflater.from(context), parent, false) , mListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)
        /** Load user profile photo */
        Glide.with(context).load(Constants.userNameImage(item.user?.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Glide.with(context).load(Constants.downloadImage(item.image!!)).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.user!!.name
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = item.date
        holder.binding.content.text = item.content
        holder.binding.category.text = item.category?.categoryTitle
        holder.binding.category.setBackgroundColor(randomColor())

    }

    private fun randomColor(): Int {
        val random = Random()
//        return Color.argb(255, random.nextInt(170), random.nextInt(150), random.nextInt(150))
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    inner class PostsViewHolder(val binding: SamplePostsRcvBinding , val listener : ItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            listener.let {
                binding.postLayout.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position)
                    }
                }
                binding.bookmark.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onBookmarkClick(position)
                    }
                }
            }
        }
    }

    class DiffUtil : ItemCallback<PostDetails>() {
        override fun areItemsTheSame(oldItem: PostDetails, newItem: PostDetails): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostDetails, newItem: PostDetails): Boolean {
            return oldItem.postId == newItem.postId
        }

    }

}