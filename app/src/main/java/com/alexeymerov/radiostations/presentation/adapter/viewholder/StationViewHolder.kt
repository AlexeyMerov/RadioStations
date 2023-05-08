package com.alexeymerov.radiostations.presentation.adapter.viewholder

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.databinding.ItemAudioBinding
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StationViewHolder(private val binding: ItemAudioBinding, onItemClick: (Int) -> Unit) : BaseViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onItemClick(bindingAdapterPosition) }
    }

    override fun bind(currentItem: CategoriesDto) {
        binding.categoryNameTv.precomputeAndSetText(currentItem.text)
        Glide.with(binding.imageView.context)// todo make it right. It's not the most optimized way.
            .load(currentItem.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.full_image)
            .error(R.drawable.full_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.imageView)
    }

    private fun AppCompatTextView.precomputeAndSetText(text: String) {
        val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
        val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
        setTextFuture(textFuture)
    }
}