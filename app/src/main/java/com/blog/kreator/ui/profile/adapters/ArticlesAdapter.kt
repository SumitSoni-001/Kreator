package com.blog.kreator.ui.profile.adapters

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleArticlesRcvBinding
import com.blog.kreator.databinding.SamplePostsRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.squareup.picasso.Picasso
import org.threeten.bp.temporal.ChronoField
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ArticlesAdapter(private val context: Context) : ListAdapter<PostDetails, ArticlesAdapter.PostsViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int)
        fun onMoreClick(position : Int , moreImg : ImageView)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(SampleArticlesRcvBinding.inflate(LayoutInflater.from(context), parent, false) , mListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.articleLayout.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation)
        Picasso.get().load(CustomImage.downloadImage(item.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = FormatTime.getFormattedTime(item.date!!)
        holder.binding.categoryName.text = item.category?.categoryTitle
        holder.binding.categoryName.setTextColor(randomColor())
    }

    private fun randomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    inner class PostsViewHolder(val binding: SampleArticlesRcvBinding, val listener : ItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            listener.let {
                binding.articleLayout.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position)
                    }
                }
                binding.more.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        listener.onMoreClick(position , it as ImageView)
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