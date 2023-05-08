package com.alexeymerov.radiostations.presentation.adapter.viewholder

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.alexeymerov.radiostations.databinding.ItemCategoryHeaderBinding
import com.alexeymerov.radiostations.domain.dto.CategoriesDto

class HeaderViewHolder(private val binding: ItemCategoryHeaderBinding) : BaseViewHolder(binding.root) {

    override fun bind(currentItem: CategoriesDto) {
        binding.categoryNameTv.precomputeAndSetText(currentItem.text)
    }

    private fun AppCompatTextView.precomputeAndSetText(text: String) {
        val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
        val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
        setTextFuture(textFuture)
    }
}