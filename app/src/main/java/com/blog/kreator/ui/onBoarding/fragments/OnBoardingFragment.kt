package com.blog.kreator.ui.onBoarding.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentOnboardingBinding
import com.blog.kreator.ui.onBoarding.adapters.OnBoardingAdapter

@RequiresApi(Build.VERSION_CODES.M)
class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var onBoardingAdapter: OnBoardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBoardingAdapter = OnBoardingAdapter(requireContext())
        binding.viewPager.adapter = onBoardingAdapter
        binding.viewPager.isUserInputEnabled = true
        setUpIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.btnNext.text = "Go Next"
                        binding.skip.visibility = View.VISIBLE
                        setUpIndicator(0)
                    }
                    1 -> {
                        binding.btnNext.text = "Go Next"
                        binding.skip.visibility = View.VISIBLE
                        setUpIndicator(1)
                    }
                    2 -> {
                        binding.btnNext.text = "Let's Go"
                        binding.skip.visibility = View.INVISIBLE
                        setUpIndicator(2)
                    }
                }

            }
        })

        binding.btnNext.setOnClickListener {
            nextPage()
        }

        binding.skip.setOnClickListener {
            binding.viewPager.setCurrentItem(2 , true)
        }

    }

    private fun nextPage() {
        if (binding.viewPager.currentItem == 2){
            findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
        }else{
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1 , true)
        }
    }

    private fun setUpIndicator(pos: Int) {
        val dots = arrayOfNulls<TextView>(3)
        binding.dotLayout.removeAllViews()

        for (i in 0..2) {
            dots[i] = TextView(requireContext())
            dots[i]?.text = Html.fromHtml("&#8226")     // Rectangle = &#9644
            dots[i]?.textSize = 45F
            dots[i]?.setTextColor(resources.getColor(R.color.dotInactive, requireContext().applicationContext.theme))
            binding.dotLayout.addView(dots[i])

        }
        dots[pos]?.setTextColor(resources.getColor(R.color.dotActive, requireContext().applicationContext.theme))
    }

}