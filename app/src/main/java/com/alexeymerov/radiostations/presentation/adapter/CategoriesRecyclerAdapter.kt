package com.alexeymerov.radiostations.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.databinding.ItemAudioBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryHeaderBinding
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CategoriesRecyclerAdapter @Inject constructor() : BaseRecyclerAdapter<CategoriesDto, CategoriesRecyclerAdapter.ViewHolder>() {

    lateinit var onClick: (CategoriesDto) -> Unit

    override val differ: AsyncListDiffer<CategoriesDto> = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int): Int {
        val item = getListItem(position)
        return when {
            item.isHeader -> 0
            item.isAudio -> 1
            else -> 2
        }
    }

    override fun compareItems(old: CategoriesDto, new: CategoriesDto) = old.url == new.url

    override fun compareContent(old: CategoriesDto, new: CategoriesDto) = old == new

    override fun compareContentForPayload(old: CategoriesDto, new: CategoriesDto) = emptyList<CategoriesDto>()

    override fun proceedPayloads(payloads: MutableList<Any>, holder: ViewHolder, position: Int) {
        // handle changes from payloads
        holder.bind(getListItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }

            1 -> {
                val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StationViewHolder(binding) {}
            }

            else -> {
                val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CategoriesViewHolder(binding) { onClick(getListItem(it)) }
            }
        }
    }

    abstract inner class ViewHolder(containerView: View) : BaseViewHolder<CategoriesDto>(containerView)

    inner class CategoriesViewHolder(private val binding: ItemCategoryBinding, onItemClick: (Int) -> Unit) : ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onItemClick(bindingAdapterPosition) }
        }

        override fun bind(currentItem: CategoriesDto) {
            binding.categoryNameTv.precomputeAndSetText(currentItem.text)
        }

        private fun AppCompatTextView.precomputeAndSetText(text: String) {
            val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
            val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
            setTextFuture(textFuture)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemCategoryHeaderBinding) : ViewHolder(binding.root) {

        override fun bind(currentItem: CategoriesDto) {
            binding.categoryNameTv.precomputeAndSetText(currentItem.text)
        }

        private fun AppCompatTextView.precomputeAndSetText(text: String) {
            val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
            val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
            setTextFuture(textFuture)
        }
    }

    inner class StationViewHolder(private val binding: ItemAudioBinding, onItemClick: (Int) -> Unit) : ViewHolder(binding.root) {

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
}