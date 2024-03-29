package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SamplePostsRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class PostsAdapter(private val context: Context, private val bookmarkedList:ArrayList<BookmarkResponse>) : ListAdapter<PostDetails, PostsAdapter.PostsViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int,bookmarkPosition : Int)
        fun onBookmarkClick(position : Int, bookmarkPosition : Int, bookmarkImg: ImageView)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(SamplePostsRcvBinding.inflate(LayoutInflater.from(context), parent, false) , mListener)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.parentLayout.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation)
        val profileUrl = CustomImage.downloadProfile(item.user?.userImage , item.user?.name.toString())
        Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Picasso.get().load(CustomImage.downloadImage(item.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.user!!.name
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = FormatTime.getFormattedTime(item.date!!)
        val deserializedContent = holder.binding.editor.getContentAsHTML(item.content)
//        holder.binding.content.text = item.content
        holder.binding.content.text = Html.fromHtml(Html.fromHtml(deserializedContent).toString())
        holder.binding.category.text = item.category?.categoryTitle
        holder.binding.category.setBackgroundColor(randomColor())

        bookmarkedList.forEach {
            if (item.postId == it.post?.postId){
                holder.binding.bookmark.setImageResource(R.drawable.bookmarked)
            }
        }
    }

    private fun randomColor(): Int {
        val random = Random()
//        return Color.argb(255, random.nextInt(170), random.nextInt(150), random.nextInt(150))
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    inner class PostsViewHolder(val binding: SamplePostsRcvBinding, private val listener : ItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            listener.let {
                binding.postLayout.setOnClickListener {
                    val position = absoluteAdapterPosition
                    var bookmarkPosition = -1
                    bookmarkedList.forEach {
                        if (getItem(position).postId == it.post?.postId) {
                            bookmarkPosition = bookmarkedList.indexOf(it)
                        }
                    }
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, bookmarkPosition)
                    }
                }
                binding.bookmark.setOnClickListener {
                    val position = absoluteAdapterPosition
                    var bookmarkPosition = -1
                    bookmarkedList.forEach {
                        if (getItem(position).postId == it.post?.postId) {
                            bookmarkPosition = bookmarkedList.indexOf(it)
                        }
//                        else { -1 }
                    }
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBookmarkClick(position, bookmarkPosition,binding.bookmark)
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