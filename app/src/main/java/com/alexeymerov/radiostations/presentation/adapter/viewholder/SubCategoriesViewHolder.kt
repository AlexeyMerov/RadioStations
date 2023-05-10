package com.alexeymerov.radiostations.presentation.adapter.viewholder

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.alexeymerov.radiostations.databinding.ItemSubcategoryBinding
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto

class SubCategoriesViewHolder(
    private val binding: ItemSubcategoryBinding,
    onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onItemClick(bindingAdapterPosition) }
    }

    override fun bind(currentItem: CategoryItemDto) {
        binding.categoryNameTv.precomputeAndSetText(currentItem.text)
    }

    private fun AppCompatTextView.precomputeAndSetText(text: String) {
        val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
        val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
        setTextFuture(textFuture)
    }
}