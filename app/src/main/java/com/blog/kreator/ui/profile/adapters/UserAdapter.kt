package com.blog.kreator.ui.profile.adapters

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleSavedRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.ui.profile.models.BookmarkResponse
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.blog.kreator.utils.SessionManager
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(private val context: Context, private val bookmarkedList: ArrayList<BookmarkResponse>) : ListAdapter<PostDetails, UserAdapter.UserViewHolder>(DiffUtil()) {

    private lateinit var mListener : ItemClickListener

    interface ItemClickListener{
        fun onItemClick(position : Int)
        fun onBookmarkClick(position : Int, bookmarkPosition : Int, bookmarkImg: ImageView)
    }

    fun setOnItemClickListener(listener : ItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(SampleSavedRcvBinding.inflate(LayoutInflater.from(context),parent,false), mListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.savedLayout.animation = AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation)
        val profileUrl = CustomImage.downloadProfile(item.user?.userImage , item.user?.name.toString())
        Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Picasso.get().load(CustomImage.downloadImage(item.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.user!!.name
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = FormatTime.getFormattedTime(item.date!!)
        holder.binding.categoryName.text = item.category?.categoryTitle
        holder.binding.categoryName.setTextColor(randomColor())

        holder.binding.bookmark.setImageResource(R.drawable.bookmark)
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

    inner class UserViewHolder(val binding: SampleSavedRcvBinding, private val listener: ItemClickListener):RecyclerView.ViewHolder(binding.root){
        init {
            listener.let {
                binding.savedLayout.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
                binding.bookmark.setOnClickListener {
                    val position = absoluteAdapterPosition
                    var bookmarkPosition = -1
                    bookmarkedList.forEach {
                        if (getItem(position).postId == it.post?.postId) {
                            bookmarkPosition = bookmarkedList.indexOf(it)
                        }
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