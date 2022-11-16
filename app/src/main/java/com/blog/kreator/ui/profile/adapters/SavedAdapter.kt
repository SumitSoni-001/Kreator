package com.blog.kreator.ui.profile.adapters

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SamplePostsRcvBinding
import com.blog.kreator.databinding.SampleSavedRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.FormatTime
import com.squareup.picasso.Picasso
import org.threeten.bp.temporal.ChronoField
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SavedAdapter(private val context: Context) : ListAdapter<PostDetails, SavedAdapter.PostsViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int)
        fun onBookmarkClick(position : Int)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(SampleSavedRcvBinding.inflate(LayoutInflater.from(context), parent, false) , mListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)
        Picasso.get().load(Constants.userNameImage(item.user?.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Picasso.get().load(Constants.downloadImage(item.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.user!!.name
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = FormatTime.getFormattedTime(item.date!!)
        holder.binding.categoryName.text = item.category?.categoryTitle
        holder.binding.categoryName.setTextColor(randomColor())
    }

    private fun randomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    inner class PostsViewHolder(val binding: SampleSavedRcvBinding, val listener : ItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            listener.let {
                binding.savedLayout.setOnClickListener {
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