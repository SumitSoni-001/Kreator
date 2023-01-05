package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleCommentsLayoutBinding
import com.blog.kreator.ui.home.models.CommentDetails
import com.blog.kreator.ui.onBoarding.models.GetUserDetails
import com.blog.kreator.utils.Constants
import com.blog.kreator.utils.CustomImage
import com.blog.kreator.utils.FormatTime
import com.blog.kreator.utils.SessionManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class CommentAdapter(private val context: Context) : ListAdapter<CommentDetails, CommentAdapter.CommentsViewHolder>(DiffUtil()) {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position:Int , moreOptions:View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(SampleCommentsLayoutBinding.inflate(LayoutInflater.from(context) , parent , false),mListener)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = getItem(position)

        val jsonString = Gson().fromJson(item.userDetails , GetUserDetails::class.java)

        Picasso.get().load(CustomImage.downloadProfile(jsonString.userImage , jsonString.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.userImage)
        holder.binding.name.text = jsonString.name
        holder.binding.comment.text = item.content
        holder.binding.time.text = FormatTime.getFormattedTime(item.postedOn.toString())

        if (SessionManager(context).getUserId().toString() == jsonString.id.toString()){
            holder.binding.more.visibility = View.VISIBLE
        }else{
            holder.binding.more.visibility = View.INVISIBLE
        }
        holder.binding.tvEdited.visibility = View.INVISIBLE

        if (item.isEdited == true){
            holder.binding.tvEdited.visibility = View.VISIBLE
        }else{
            holder.binding.tvEdited.visibility = View.INVISIBLE
        }

    }

    inner class CommentsViewHolder(val binding: SampleCommentsLayoutBinding, private val listener : OnItemClickListener) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.more.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position,binding.more)
                }
            }
        }
    }

    class DiffUtil : ItemCallback<CommentDetails>() {
        override fun areItemsTheSame(oldItem: CommentDetails, newItem: CommentDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentDetails, newItem: CommentDetails): Boolean {
            return oldItem.id == newItem.id
        }
    }

}