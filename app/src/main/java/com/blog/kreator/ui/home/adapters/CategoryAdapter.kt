package com.blog.kreator.ui.home.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R
import com.blog.kreator.databinding.SampleCategoryLayoutBinding

class CategoryAdapter(private val context: Context, private val list: ArrayList<String>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private lateinit var mListener: ItemClickListener
    private val tvCategoryList = ArrayList<TextView>()

    interface ItemClickListener {
        fun onItemClick(position: Int, categoryLayout: SampleCategoryLayoutBinding , tvCategoryList : ArrayList<TextView>)
    }

    fun setonItemClickListener(listener: ItemClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            SampleCategoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val tvCategory = holder.binding.tvCategory
        if (!tvCategoryList.contains(tvCategory)) {
            tvCategoryList.add(tvCategory)
        }
        tvCategory.text = list[position]

        val all = tvCategoryList[0]
        all.setBackgroundResource(R.drawable.category_selected)
        all.setTextColor(context.resources.getColor(R.color.white))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class CategoryViewHolder(
        val binding: SampleCategoryLayoutBinding,
        val listener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvCategory.setOnClickListener {
                listener.let {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, binding , tvCategoryList)
                    }
                }
            }
        }
    }

}