package com.blog.kreator.ui.profile.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleSavedRcvBinding
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import java.util.*

class SavedAdapter(private val context: Context) : ListAdapter<BookmarkResponse, SavedAdapter.BookmarksViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int)
        fun onBookmarkClick(position : Int)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        return BookmarksViewHolder(SampleSavedRcvBinding.inflate(LayoutInflater.from(context), parent, false) , mListener)
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        val item = getItem(position)
        Picasso.get().load(CustomImage.downloadProfile(item.post?.user?.userImage,item.post?.user?.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Picasso.get().load(CustomImage.downloadImage(item.post?.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.post?.user!!.name
        holder.binding.title.text = item.post?.postTitle
        holder.binding.time.text = FormatTime.getFormattedTime(item.post?.date!!)
        holder.binding.categoryName.text = item.post?.category?.categoryTitle
        holder.binding.categoryName.setTextColor(randomColor())
//        if (item.user?.id == SessionManager(context).getUserId()?.toInt()){
//            holder.binding.bookmark.setImageResource(R.drawable.bookmarked)
//        }else{
//            holder.binding.bookmark.setImageResource(R.drawable.bookmark)
//        }
    }

    private fun randomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    inner class BookmarksViewHolder(val binding: SampleSavedRcvBinding, private val listener : ItemClickListener) : RecyclerView.ViewHolder(binding.root){
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

    class DiffUtil : ItemCallback<BookmarkResponse>() {
        override fun areItemsTheSame(oldItem: BookmarkResponse, newItem: BookmarkResponse): Boolean {
            return oldItem.post?.postId == newItem.post?.postId
        }

        override fun areContentsTheSame(oldItem: BookmarkResponse, newItem: BookmarkResponse): Boolean {
            return oldItem.post?.postId == newItem.post?.postId
        }

    }

}