package com.alexeymerov.radiostations.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.databinding.ItemCategoryBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CategoriesRecyclerAdapter @Inject constructor() : BaseRecyclerAdapter<CategoryEntity, CategoriesRecyclerAdapter.ViewHolder>() {

    lateinit var onClick: (CategoryEntity) -> Unit

    override val differ: AsyncListDiffer<CategoryEntity> = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int) = 0

    override fun compareItems(old: CategoryEntity, new: CategoryEntity) = old.url == new.url

    override fun compareContent(old: CategoryEntity, new: CategoryEntity) = old == new

    override fun compareContentForPayload(old: CategoryEntity, new: CategoryEntity) = emptyList<CategoryEntity>()

    override fun proceedPayloads(payloads: MutableList<Any>, holder: ViewHolder, position: Int) {
        // handle changes from payloads
        holder.bind(getListItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding) { onClick(getListItem(it)) }
    }

    abstract inner class ViewHolder(containerView: View) : BaseViewHolder<CategoryEntity>(containerView)

    inner class CategoriesViewHolder(private val binding: ItemCategoryBinding, onItemClick: (Int) -> Unit) : ViewHolder(binding.root) {

        init {
            binding.categoryNameTv.setOnClickListener { onItemClick(bindingAdapterPosition) }
        }

        override fun bind(currentItem: CategoryEntity) {
            binding.categoryNameTv.precomputeAndSetText(currentItem.text)
        }

        private fun AppCompatTextView.precomputeAndSetText(text: String) {
            val textMetricsParams = TextViewCompat.getTextMetricsParams(this)
            val textFuture = PrecomputedTextCompat.getTextFuture(text, textMetricsParams, null)
            setTextFuture(textFuture)
        }
    }
}