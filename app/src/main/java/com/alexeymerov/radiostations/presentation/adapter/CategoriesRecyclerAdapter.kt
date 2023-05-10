package com.alexeymerov.radiostations.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.alexeymerov.radiostations.databinding.ItemAudioBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryHeaderBinding
import com.alexeymerov.radiostations.domain.dto.CategoriesDto
import com.alexeymerov.radiostations.presentation.adapter.viewholder.BaseViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.CategoriesViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.HeaderViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.StationViewHolder
import com.bumptech.glide.RequestManager
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Main/common recycler adapter, for every list in this project.
 * Displays Categories, Headers with children (Audio or Category)
 * */
@FragmentScoped
class CategoriesRecyclerAdapter @Inject constructor() : BaseRecyclerAdapter<CategoriesDto, BaseViewHolder>() {

    lateinit var onClick: (CategoriesDto) -> Unit

    lateinit var requestManager: RequestManager

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

    override fun proceedPayloads(payloads: MutableList<Any>, holder: BaseViewHolder, position: Int) {
        // handle changes from payloads
        holder.bind(getListItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }

            1 -> {
                val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StationViewHolder(binding, requestManager) {} //todo process click and open new screen to play audio
            }

            else -> {
                val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CategoriesViewHolder(binding) { onClick(getListItem(it)) }
            }
        }
    }

}