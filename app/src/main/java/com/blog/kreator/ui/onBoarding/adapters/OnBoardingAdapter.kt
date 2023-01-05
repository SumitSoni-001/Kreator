package com.blog.kreator.ui.onBoarding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blog.kreator.R

class OnBoardingAdapter(private val context: Context) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    private val backgrounds = listOf(
        R.layout.onboarding1_layout,
        R.layout.onboarding2_layout,
        R.layout.onboarding3_layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(LayoutInflater.from(context).inflate(viewType , parent , false))
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        backgrounds[holder.absoluteAdapterPosition]
    }

    override fun getItemCount(): Int {
        return backgrounds.size
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return backgrounds[position]
    }

    inner class OnBoardingViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView)

}
