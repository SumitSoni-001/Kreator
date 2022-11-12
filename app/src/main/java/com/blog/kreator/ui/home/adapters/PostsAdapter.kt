package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SamplePostsRcvBinding
import com.blog.kreator.ui.home.models.PostDetails
import com.blog.kreator.utils.Constants
import com.squareup.picasso.Picasso
import org.threeten.bp.temporal.ChronoField
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
//        Glide.with(context).load(Constants.userNameImage(item.user?.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
//        Glide.with(context).load(Constants.downloadImage(item.image!!)).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        Picasso.get().load(Constants.userNameImage(item.user?.name.toString())).placeholder(R.drawable.user_placeholder).into(holder.binding.profile)
        Picasso.get().load(Constants.downloadImage(item.image.toString())).placeholder(R.drawable.placeholder).into(holder.binding.postImage)
        holder.binding.name.text = item.user!!.name
        holder.binding.title.text = item.postTitle
        holder.binding.time.text = getFormattedTime(item.date!!).toString()
        holder.binding.content.text = item.content
        holder.binding.category.text = item.category?.categoryTitle
        holder.binding.category.setBackgroundColor(randomColor())

    }

    private fun randomColor(): Int {
        val random = Random()
//        return Color.argb(255, random.nextInt(170), random.nextInt(150), random.nextInt(150))
        return Color.argb(255, random.nextInt(255), random.nextInt(200), random.nextInt(60))
    }

    private fun getFormattedTime(dateTime:String) : String{
        val dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
        val date = dateTime.substring(0,10)
        val time = dateTime.substring(11,19)
        val year = dateTime.substring(0,4).toInt()
        val month = dateTime.substring(5,7).toInt()
        val day = dateTime.substring(8,10).toInt()
        val hour = dateTime.substring(11,13)
        val minute = dateTime.substring(14,16)
        val seconds = dateTime.substring(17,19)

        val outputFormat: DateFormat = SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val inputText: String = date
        var getDate: Date? = null
        try {
            getDate = inputFormat.parse(inputText)
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("Title", "------" + e.message.toString() + " = " + inputText)
        }
        var output = ""
        Log.d("checkTime" , ("2022-11-12" == org.threeten.bp.LocalDate.now().toString()).toString())
        /** DateTime Functionality */
        if (date.equals(org.threeten.bp.LocalDate.now().toString(),true)){
            output = if (time.equals(org.threeten.bp.LocalTime.now().toString(),true)){
                "Just now"
            }else if (hour.equals(org.threeten.bp.LocalTime.now().hour.toString(),true)){
                if (minute.equals(org.threeten.bp.LocalTime.now().minute.toString(),true)){
                    "${org.threeten.bp.LocalTime.now().second.minus(seconds.toInt())} seconds ago"
                }else{
                    "${org.threeten.bp.LocalTime.now().minute.minus(minute.toInt())} minutes ago"
                }
            }else{
                "${org.threeten.bp.LocalTime.now().hour.minus(hour.toInt())} hours ago"
            }
        }else if(date.equals(org.threeten.bp.LocalDate.now().minusDays(1).toString(),true)){
            output = "Yesterday"
        }else if(org.threeten.bp.LocalDate.of(year, month, day).get(ChronoField.MONTH_OF_YEAR) == org.threeten.bp.LocalDate.now().get(ChronoField.MONTH_OF_YEAR)) {
            output = if (org.threeten.bp.LocalDate.of(year, month, day).get(ChronoField.ALIGNED_WEEK_OF_MONTH) == org.threeten.bp.LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
//                "This Week"
                (7 - org.threeten.bp.LocalDate.now().dayOfWeek.value).toString() + " days ago"
            } else {
                outputFormat.format(getDate!!)
            }
        } else {
            output = outputFormat.format(getDate!!)
        }

        return output
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