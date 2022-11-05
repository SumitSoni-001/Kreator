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

        val tvCategory = holder.binding.category
        if (!tvCategoryList.contains(tvCategory)) {
            tvCategoryList.add(tvCategory)
        }

        /**    var isCategorySelected = tvCategory.isPressed */

        tvCategory.text = list[position]

        /*
        tvCategory.setOnClickListener{
//            isCategorySelected = !isCategorySelected

            for (item in tvCategoryList){
                item.setBackgroundResource(R.drawable.category_default)
                item.setTextColor(context.resources.getColor(R.color.black))
            }
            tvCategory.setBackgroundResource(R.drawable.category_selected)
            tvCategory.setTextColor(context.resources.getColor(R.color.white))

        }
        */

        if (tvCategory.isPressed){
            tvCategory.setBackgroundResource(R.drawable.category_selected)
            tvCategory.setTextColor(Color.parseColor("#FFFFFF"))
        } else{
            tvCategory.setBackgroundResource(R.drawable.category_default)
            tvCategory.setTextColor(Color.parseColor("#000000"))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class CategoryViewHolder(
        val binding: SampleCategoryLayoutBinding,
        val listener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.category.setOnClickListener {
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