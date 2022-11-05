package com.blog.kreator.ui.onBoarding.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blog.kreator.R
import com.blog.kreator.databinding.FragmentCategoriesBinding
import com.blog.kreator.utils.SessionManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections.addAll
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private var selectedCategoriesList: ArrayList<String> = ArrayList()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnNext.isEnabled = binding.chipGroup.checkedChipIds.size >= 3

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            for (item in 0 until binding.chipGroup.childCount) {
                val chip = binding.chipGroup.getChildAt(item) as Chip
                if (chip.isChecked) {
                    selectedCategoriesList.add(chip.text.toString())
                }
            }
            sessionManager.setCategories(selectedCategoriesList)
            findNavController().navigate(R.id.action_categoriesFragment_to_mainFragment)
        }

    }

}