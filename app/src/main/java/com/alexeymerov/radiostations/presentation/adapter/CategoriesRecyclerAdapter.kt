package com.alexeymerov.radiostations.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.alexeymerov.radiostations.databinding.ItemAudioBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryBinding
import com.alexeymerov.radiostations.databinding.ItemCategoryHeaderBinding
import com.alexeymerov.radiostations.databinding.ItemSubcategoryBinding
import com.alexeymerov.radiostations.domain.dto.CategoryItemDto
import com.alexeymerov.radiostations.domain.dto.DtoItemType
import com.alexeymerov.radiostations.presentation.adapter.viewholder.BaseViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.CategoriesViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.HeaderViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.StationViewHolder
import com.alexeymerov.radiostations.presentation.adapter.viewholder.SubCategoriesViewHolder
import com.bumptech.glide.RequestManager
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

/**
 * Main/common recycler adapter, for every list in this project.
 * Displays Categories, Headers with children (Audio or Category)
 * */
@FragmentScoped
class CategoriesRecyclerAdapter @Inject constructor() : BaseRecyclerAdapter<CategoryItemDto, BaseViewHolder>() {

    lateinit var onClick: (CategoryItemDto) -> Unit

    lateinit var requestManager: RequestManager

    override val differ: AsyncListDiffer<CategoryItemDto> = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int): Int {
        val item = getListItem(position)
        return item.type.value
    }

    override fun compareItems(old: CategoryItemDto, new: CategoryItemDto) = old.url == new.url

    override fun compareContent(old: CategoryItemDto, new: CategoryItemDto) = old == new

    override fun compareContentForPayload(old: CategoryItemDto, new: CategoryItemDto) = emptyList<CategoryItemDto>()

    override fun proceedPayloads(payloads: MutableList<Any>, holder: BaseViewHolder, position: Int) {
        // handle changes from payloads
        holder.bind(getListItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            DtoItemType.HEADER.value -> {
                val binding = ItemCategoryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }

            DtoItemType.AUDIO.value -> {
                val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StationViewHolder(binding, requestManager) {} //todo process click and open new screen to play audio
            }

            DtoItemType.SUBCATEGORY.value -> {
                val binding = ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SubCategoriesViewHolder(binding) { onClick(getListItem(it)) }
            }

            else -> {
                val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CategoriesViewHolder(binding) { onClick(getListItem(it)) }
            }
        }
    }

}