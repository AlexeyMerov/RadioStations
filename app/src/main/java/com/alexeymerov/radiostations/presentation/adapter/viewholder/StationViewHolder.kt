package com.alexeymerov.radiostations.presentation.adapter.viewholder

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.alexeymerov.radiostations.databinding.ItemAudioBinding
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory


class StationViewHolder(
    private val binding: ItemAudioBinding,
    private val requestManager: RequestManager,
    onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {

    // https://bumptech.github.io/glide/doc/transitions.html#cross-fading-with-placeholders-and-transparent-images
    private val crossFadeFactory = DrawableCrossFadeFactory.Builder(100).setCrossFadeEnabled(true).build()

    init {
        binding.root.setOnClickListener { onItemClick(bindingAdapterPosition) }
    }

    override fun bind(currentItem: CategoryItemDto) {
        binding.categoryNameTv.precomputeAndSetText(currentItem.text)
        requestManager
            .load(currentItem.image)
            .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
            .into(binding.imageView)
    }

    private fun AppCompatTextView.precomputeAndSetText(text: String) {
        val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
        val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
        setTextFuture(textFuture)
    }
}